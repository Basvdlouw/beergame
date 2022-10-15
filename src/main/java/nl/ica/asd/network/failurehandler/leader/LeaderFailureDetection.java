package nl.ica.asd.network.failurehandler.leader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.logic.domain.PlayerStatus;
import nl.ica.asd.logic.domain.builders.PlayerBuilder;
import nl.ica.asd.network.communication.interfaces.SendPlayerListUpdate;
import nl.ica.asd.network.failurehandler.LeaderElection;
import nl.ica.asd.network.failurehandler.exceptions.UnknownNodeException;
import nl.ica.asd.network.playerlobby.ConnectedPlayers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("LeaderFailureDetection")
public class LeaderFailureDetection implements SendPlayerUpdateCallback {

  private final ConnectedPlayers playerLobby;
  private final SendPlayerListUpdate sendPlayerListUpdate;
  private final LeaderElection leaderElection;
  private static final Logger logger = LoggerFactory.getLogger(LeaderFailureDetection.class);
  private final DetectionLoop detectionLoop;
  private final List<Player> playersAlreadyUpdated;

  @Inject
  public LeaderFailureDetection(@Named("PlayerLobby") ConnectedPlayers playerLobby,
      @Named("Messenger") SendPlayerListUpdate sendPlayerListUpdate,
      @Named("ElectionController") LeaderElection leaderElection) {
    this.sendPlayerListUpdate = sendPlayerListUpdate;
    this.playerLobby = playerLobby;
    this.leaderElection = leaderElection;
    this.detectionLoop = new DetectionLoop();
    start();
    playersAlreadyUpdated = new ArrayList<>();
  }

  public void start() {
    detectionLoop.start();
  }

  public void stop() {
    detectionLoop.stopped = true;
  }

  public void receiveHeartbeat(Player player) throws UnknownNodeException {
    playerLobby.refreshPlayerTimestamp(player, this);
  }

  @Override
  public void sendPlayerUpdate(Player player) {
    if (player != null) {
      Set<Player> playerSet = new HashSet<>();
      playerSet.add(player);
      sendPlayerListUpdate.sendPlayerListUpdate(playerSet);
    } else {
      logger.error("Player is null");
    }
  }

  public boolean getDetectionLoopStatus() {
    return detectionLoop.stopped;
  }

  private class DetectionLoop extends Thread {

    private static final long TIMEOUT = 2000;
    private boolean stopped = false;

    @Override
    public void run() {
      while (!stopped) {
        if (leaderElection.isLeader()) {
          try {
            sleep(1500);
            Set<Player> playersNotRefreshedInLast = playerLobby
                .getPlayersNotRefreshedInLast(TIMEOUT);

            Set<Player> playersWhoNeedToBeChanged = playersNotRefreshedInLast.stream()
                .filter(player -> !Arrays
                    .asList(PlayerStatus.LEFT_BY_ACCIDENT, PlayerStatus.LEFT_ON_PURPOSE)
                    .contains(player.getPlayerStatus()))
                .collect(Collectors.toSet());

            Set<Player> updatedPlayers =
                playersWhoNeedToBeChanged
                    .stream()
                    .map(player -> PlayerBuilder
                        .fromBasePlayer(player)
                        .withPlayerStatus(PlayerStatus.LEFT_BY_ACCIDENT)
                        .build())
                    .collect(Collectors.toSet());

            for (Player player : playersAlreadyUpdated) {
              if (!updatedPlayers.contains(player)) {
                playersAlreadyUpdated.remove(player);
              }
            }

            if (!updatedPlayers.isEmpty()) {
              for (Player player : playersAlreadyUpdated) {
                if (updatedPlayers.contains(player)) {
                  updatedPlayers.remove(player);
                }
              }
              sendPlayerListUpdate.sendPlayerListUpdate(updatedPlayers);
              playersAlreadyUpdated.addAll(updatedPlayers);
            }

          } catch (InterruptedException e) {
            interrupt();
            logger.debug("Thread interrupted", e);
          }
        }
      }
    }
  }
}

