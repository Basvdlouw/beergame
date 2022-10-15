package nl.ica.asd.network.communication.twophasecommit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import nl.ica.asd.logic.domain.BusinessRules;
import nl.ica.asd.logic.domain.GameState;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.logic.domain.actions.OrderAction;
import nl.ica.asd.network.communication.datatransfer.DataSenderForJsonSerializableObjects;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.AppendEntry;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.AppendEntryResult;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.InitialConnectionRequest;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.InitialConnectionResponse;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.Vote;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.VoteRequest;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.LogEntry;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.logentrydatatypes.BusinessUpdate;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.logentrydatatypes.GameStateUpdate;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.logentrydatatypes.OrderUpdate;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.logentrydatatypes.PlayerUpdate;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.logentrydatatypes.UpdateType;
import nl.ica.asd.network.communication.gameinitialisation.ConnectionResult;
import nl.ica.asd.network.communication.gameinitialisation.InitializeGame;
import nl.ica.asd.network.communication.twophasecommit.Messenger.CallBackHandler;
import nl.ica.asd.network.failurehandler.LeaderElection;
import nl.ica.asd.network.failurehandler.exceptions.NoLeaderException;
import nl.ica.asd.network.failurehandler.leaderelection.ElectionState;
import nl.ica.asd.storage.exception.UnknownGameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Named("OutgoingDataController")
public class OutgoingDataController implements ExternalConnectionResponses {

  private static final Logger logger = LoggerFactory.getLogger(OutgoingDataController.class);
  private static final int ELECTION_TERM_NOT_SET = Integer.MIN_VALUE;
  public static final int LOG_INDEX_NOT_SET = -1;

  @Named("LogController")
  private final LogController logController;
  private final List<LogEntry> queuedLogEntries;
  @Named("DataSenderForJsonSerializableObjects")
  private final DataSenderForJsonSerializableObjects dataSenderForJsonSerializableObjects;
  private final InitializeGame initializeGame;

  @Inject
  public OutgoingDataController(
      LogController logController,
      DataSenderForJsonSerializableObjects dataSenderForJsonSerializableObjects,
      InitializeGame initializeGame
  ) {
    this.logController = logController;
    this.queuedLogEntries = new ArrayList<>();
    this.dataSenderForJsonSerializableObjects = dataSenderForJsonSerializableObjects;
    this.initializeGame = initializeGame;
  }

  void sendBusinessRules(BusinessRules businessRules) {
    queuedLogEntries.add(new BusinessUpdate(LOG_INDEX_NOT_SET,
        ELECTION_TERM_NOT_SET, UpdateType.SET, businessRules, UUID.randomUUID()));
  }

  void sendOrder(OrderAction order) {
    queuedLogEntries.add(new OrderUpdate(LOG_INDEX_NOT_SET,
        ELECTION_TERM_NOT_SET, UpdateType.SET, order, UUID.randomUUID()));
  }

  void sendPlayerListUpdate(Player player) {
    queuedLogEntries.add(new PlayerUpdate(LOG_INDEX_NOT_SET,
        ELECTION_TERM_NOT_SET, UpdateType.SET, player, UUID.randomUUID()));
  }

  void sendGameStateUpdate(GameState gameState) {
    queuedLogEntries.add(new GameStateUpdate(LOG_INDEX_NOT_SET,
        ELECTION_TERM_NOT_SET, UpdateType.SET, gameState, UUID.randomUUID()));
  }

  synchronized void sendVoteRequest(Set<Player> targetsForVoteRequest,
      CallBackHandler callBackHandler, Player proposedLeader) throws IOException {
    VoteRequest voteRequest = getVoteRequest(callBackHandler.getLeaderElection(), proposedLeader);
    for (Player target : targetsForVoteRequest) {
      dataSenderForJsonSerializableObjects.sendObjectAsSerializedData(
          target,
          voteRequest,
          callBackHandler
      );
    }
  }

  synchronized void sendHeartbeat(Set<Player> targetsForHeartbeat, CallBackHandler callBackHandler)
      throws NoLeaderException, IOException {
    if (callBackHandler.getLeaderElection().getElectionState().equals(ElectionState.LEADER)) {
      logController.synchronizeLogEntries(
          logController.assignLogIndexAndElectionTerm(
              queuedLogEntries,
              callBackHandler.getLeaderElection()
          )
      );
      queuedLogEntries.clear();
    }
    for (Player target : targetsForHeartbeat) {
      dataSenderForJsonSerializableObjects
          .sendObjectAsSerializedData(target,
              getAppendEntry(callBackHandler.getLeaderElection(), target), callBackHandler);
    }
  }

  private AppendEntry getAppendEntry(LeaderElection leaderElection, Player target)
      throws NoLeaderException {
    //The switch statement builds the AppendEntry according to the role the node is in.
    switch (leaderElection.getElectionState()) {
      case LEADER:
        return buildAppendEntry(logController.getLogsNotConfirmedByPlayer(target), leaderElection,
            target);
      case FOLLOWER:
        return buildAppendEntry(queuedLogEntries, leaderElection, target);
      case CANDIDATE:
        throw new IllegalStateException(
            "No appendEntries should be send while in the CandidateState");
      default:
        throw new IllegalStateException(
            "The node was found to be in an non existing election state" +
                leaderElection.getElectionState().toString());
    }
  }

  private AppendEntry buildAppendEntry(
      List<LogEntry> logEntries,
      LeaderElection leaderElection,
      Player target
  ) throws NoLeaderException {
    return new AppendEntry(
        leaderElection.getElectionTerm(),
        leaderElection.getLeader(),
        logController.getLastLogIndexForPlayer(target),
        logController.getLastLogElectionTermForPlayer(target),
        logEntries,
        logController.getCommitIndex()
    );
  }

  private VoteRequest getVoteRequest(LeaderElection leaderElection, Player proposedLeader) {
    return new VoteRequest(leaderElection.getElectionTerm(), proposedLeader,
        logController.getLastLogIndex(),
        logController.getLastLogElectionTerm());
  }

  @Override
  public void receiveVote(Vote vote, Player sender,
      LeaderElection leaderElection) {
    try {
      leaderElection.receiveVote(vote, sender);
    } catch (IOException e) {
      logger.warn("Failed to process the received vote", e.getCause());
    }
  }

  @Override
  public void receiveAppendEntryResult(AppendEntry originalRequest,
      AppendEntryResult appendEntryResult, Player responder, LeaderElection leaderElection) {
    if (leaderElection.getElectionState() == ElectionState.LEADER) {
      try {
        logController.updateCommitIndex(
            responder,
            appendEntryResult.getNewestReceivedLogIndex()
        );
      } catch (UnknownGameException e) {
        logger.error("Failed to update commitIndex because there is no active game found", e);
      }
    }
    if (appendEntryResult.isSuccess()) {
      clearSentLogEntriesFromQueue(originalRequest);
    }
  }

  private void clearSentLogEntriesFromQueue(AppendEntry originalRequest) {
    logController.getUncommittedLogs();
    List<LogEntry> logEntriesToRemove = new ArrayList<>();
    for (LogEntry queuedLogEntry : queuedLogEntries) {
      for (LogEntry confirmedLogEntry : originalRequest.getLogEntries()) {
        if (queuedLogEntry.getQueueIdentifier().equals(confirmedLogEntry.getQueueIdentifier())) {
          logEntriesToRemove.add(queuedLogEntry);
        }
      }
    }
    queuedLogEntries.removeAll(logEntriesToRemove);
  }

  @Override
  public void receiveConnectionResponse(InitialConnectionRequest originalRequest,
      InitialConnectionResponse initialConnectionResponse) {
    initializeGame.joinLobby(originalRequest.getPlayer(), initialConnectionResponse.getPlayers(),
        initialConnectionResponse.getGameData(), initialConnectionResponse.getConnectionResult());
  }

  @Override
  public void receiveConnectionFailed() {
    initializeGame.joinLobby(null, null, null, ConnectionResult.GAME_NOT_FOUND);
  }

  void connectToGame(Player target, Player player, CallBackHandler callbackHandler)
      throws IOException {
    dataSenderForJsonSerializableObjects
        .sendObjectAsSerializedData(target, new InitialConnectionRequest(player), callbackHandler);
  }

  public void reset() {
    queuedLogEntries.clear();
    logController.reset();
  }
}