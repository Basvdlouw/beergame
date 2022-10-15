package nl.ica.asd.network.communication.twophasecommit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Named;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.AppendEntry;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.AppendEntryResult;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.InitialConnectionRequest;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.InitialConnectionResponse;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.Vote;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.VoteRequest;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.LogEntry;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.logentrydatatypes.PlayerUpdate;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.logentrydatatypes.UpdateType;
import nl.ica.asd.network.communication.gameinitialisation.ConnectionResult;
import nl.ica.asd.network.failurehandler.HeartbeatHandler;
import nl.ica.asd.network.failurehandler.LeaderElection;
import nl.ica.asd.network.failurehandler.exceptions.UnknownNodeException;
import nl.ica.asd.network.playerlobby.ConnectedPlayers;
import nl.ica.asd.storage.exception.UnknownGameException;
import nl.ica.asd.storage.gameconfig.GameConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IncomingDataController implements ExternalConnectionEndpoint {

  @Named("LeaderElection")
  private final LeaderElection leaderElection;
  @Named("LogController")
  private final LogController logController;

  @Named("HeartbeatHandler")
  private final HeartbeatHandler heartbeatHandler;

  @Named("PlayerLobby")
  private ConnectedPlayers connectedPlayers;
  @Named("GameConfig")
  private GameConfig gameConfig;

  private static final Logger logger = LoggerFactory.getLogger(IncomingDataController.class);

  @Inject
  public IncomingDataController(
      LeaderElection leaderElection,
      HeartbeatHandler heartbeatHandler,
      LogController logController,
      ConnectedPlayers connectedPlayers,
      GameConfig gameConfig
  ) {
    this.leaderElection = leaderElection;
    this.logController = logController;
    this.heartbeatHandler = heartbeatHandler;
    this.connectedPlayers = connectedPlayers;
    this.gameConfig = gameConfig;
  }

  @Override
  public AppendEntryResult receiveAppendEntry(Player sender, AppendEntry appendEntry)
      throws IOException {
    int electionTerm = leaderElection.getElectionTerm();
    try {
      //Letting the FailureHandler know a new Heartbeat has been received
      heartbeatHandler.receiveHeartbeat(sender, appendEntry);
    } catch (UnknownNodeException e) {
      logger.error("The node the AppendEntry is received from is not a player in this game", e);
      return getAppendEntryResult(false);
    }

    switch (leaderElection.getElectionState()) {
      case FOLLOWER:
        //Follower checking if the appendEntry being received should be accepted.
        if (appendEntry.getElectionTerm() < electionTerm ||
            !logController.verifyElectionTermOfLogEntryMatches(
                appendEntry.getPreviousLogIndex(), appendEntry.getPreviousLogElectionTerm())) {
          //Rejecting new Entries for the log.
          return getAppendEntryResult(false);
        } else {
          //Accepting new Entries for the log.
          logController.synchronizeLogEntries(appendEntry.getLogEntries());
          try {
            logController.updateCommitIndex(appendEntry.getLeaderCommit());
          } catch (UnknownGameException e) {
            logger.error("Failed to update commitIndex because there is no active game found",
                e.getCause());
          }
          return getAppendEntryResult(true);
        }
      case LEADER:
        //Accepting new Entries for the log.
        if (!sender.equals(connectedPlayers.getPlayerForSelf())) {
          logController.synchronizeLogEntries(
              logController.assignLogIndexAndElectionTerm(
                  appendEntry.getLogEntries(),
                  leaderElection
              )
          );
        } else {
          logController.synchronizeLogEntries(appendEntry.getLogEntries());
        }
        return getAppendEntryResult(true);
      case CANDIDATE:
        return getAppendEntryResult(false);
      default:
        //should be unreachable
        throw new IllegalStateException(
            "The node was found to be in an non existing election state" + leaderElection
                .getElectionState().toString());
    }
  }

  private AppendEntryResult getAppendEntryResult(boolean success) {
    return new AppendEntryResult(leaderElection.getElectionTerm(), logController.getLastLogIndex(),
        success);
  }

  @Override
  public Vote receiveVoteRequest(Player player, VoteRequest voteRequest) {
    LogEntry lastLogEntry = logController.getLastLogEntry();
    return leaderElection
        .receiveVoteRequest(voteRequest, lastLogEntry.getLogIndex(),
            lastLogEntry.getElectionTerm());
  }

  @Override
  public InitialConnectionResponse receiveConnectionRequest(
      InitialConnectionRequest initialConnectionRequest) {
    if (connectedPlayers.isFull(gameConfig)) {
      return new InitialConnectionResponse(null, null, ConnectionResult.LOBBY_FULL);
    }

    Set<Player> players = this.connectedPlayers.getPlayersWhoAreConnected();
    Player result = players.stream()
        .filter(player -> initialConnectionRequest.getPlayer().getUsername()
            .equals(player.getUsername()))
        .findAny()
        .orElse(null);
    if (result != null) {
      return new InitialConnectionResponse(null, null, ConnectionResult.USERNAME_ALREADY_CHOSEN);
    }

    Set<Player> allPlayers = this.connectedPlayers.getAllPlayers();
    Player allResult = allPlayers.stream()
        .filter(player -> initialConnectionRequest.getPlayer().getUsername()
            .equals(player.getUsername()))
        .findAny()
        .orElse(null);
    if (allResult == null) {
      LogEntry playerUpdate = new PlayerUpdate(logController.getNextLogIndex(),
          leaderElection.getElectionTerm(), UpdateType.SET, initialConnectionRequest
          .getPlayer(), UUID.randomUUID());
      List<LogEntry> logEntries = new ArrayList<>();
      logEntries.add(playerUpdate);
      logController.synchronizeLogEntries(logEntries);
    }

    return new InitialConnectionResponse(this.connectedPlayers.getPlayersWhoAreConnected(),
        gameConfig.getGameData(), ConnectionResult.SUCCESS);
  }
}