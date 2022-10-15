package nl.ica.asd.network.failurehandler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.AppendEntry;
import nl.ica.asd.network.communication.interfaces.SendHeartbeat;
import nl.ica.asd.network.failurehandler.exceptions.NoLeaderException;
import nl.ica.asd.network.failurehandler.exceptions.UnknownNodeException;
import nl.ica.asd.network.failurehandler.leader.LeaderFailureDetection;
import nl.ica.asd.network.failurehandler.leaderelection.ElectionController;
import nl.ica.asd.network.playerlobby.ConnectedPlayers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Named("FailureHandler")
public class FailureHandler implements HeartbeatHandler {

  private final ConnectedPlayers connectedPlayers;
  private final SendHeartbeat sendHeartbeat;
  private final ElectionController electionController;
  private final LeaderFailureDetection leaderFailureHandler;

  private static Logger logger = LoggerFactory.getLogger(FailureHandler.class);

  private HeartbeatTimer heartbeatTimer;

  @Inject
  public FailureHandler(ConnectedPlayers connectedPlayers, SendHeartbeat sendHeartbeat,
      ElectionController electionController, LeaderFailureDetection leaderFailureHandler) {
    this.connectedPlayers = connectedPlayers;
    this.sendHeartbeat = sendHeartbeat;
    this.electionController = electionController;
    this.leaderFailureHandler = leaderFailureHandler;
  }

  @Override
  public void receiveHeartbeat(Player player, AppendEntry heartbeat)
      throws UnknownNodeException, IOException {
    electionController.receiveHeartbeat(heartbeat);

    if (electionController.isLeader()) {
      leaderFailureHandler.receiveHeartbeat(player);
    }

    startSendingHeartbeats();
  }

  void sendHeartbeat() {
    try {
      switch (electionController.getElectionState()) {
        case LEADER:

          Set<Player> targets = new HashSet<>(connectedPlayers.getAllPlayers());
          sendHeartbeat.sendHeartbeat(targets, electionController);
          break;
        case FOLLOWER:
          sendHeartbeatToLeader();
          break;
        case CANDIDATE:
          logger.info("No heartbeats are to be sent during candidacy");
          break;
      }
    } catch (Exception e) {
      logger.error(String
          .format("Heartbeat failed to send. Following error was thrown: %s", e.getMessage()));
    }
  }

  private void sendHeartbeatToLeader() {
    try {
      Set<Player> recipients = new HashSet<>();
      recipients.add(electionController.getLeader());
      sendHeartbeat.sendHeartbeat(recipients, electionController);
    } catch (NoLeaderException e) {
      logger.error("Error sending heartbeat to game, no leader set", e);
    }
  }

  public void startSendingHeartbeats() throws IOException {
    if (heartbeatTimer == null) {
      try {
        electionController.getLeader();
      } catch (NoLeaderException e) {
        if (electionController.getElectionTerm() == 0) {
          electionController.becomeLeader(1);
        }
      }

      logger.info("Heartbeat timer started");
      heartbeatTimer = new HeartbeatTimer(this);
    }
  }

  public void reset() {
    electionController.reset();
    heartbeatTimer.stopTimer();
    heartbeatTimer = null;
  }
}
