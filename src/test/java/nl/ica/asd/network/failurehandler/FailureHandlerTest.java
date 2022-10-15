package nl.ica.asd.network.failurehandler;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.AppendEntry;
import nl.ica.asd.network.communication.interfaces.SendHeartbeat;
import nl.ica.asd.network.failurehandler.exceptions.NoLeaderException;
import nl.ica.asd.network.failurehandler.exceptions.UnknownNodeException;
import nl.ica.asd.network.failurehandler.leader.LeaderFailureDetection;
import nl.ica.asd.network.failurehandler.leaderelection.ElectionController;
import nl.ica.asd.network.failurehandler.leaderelection.ElectionState;
import nl.ica.asd.network.playerlobby.ConnectedPlayers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class FailureHandlerTest {

  private ConnectedPlayers connectedPlayers;

  private SendHeartbeat sendHeartbeat;

  private AppendEntry testHeartbeat;

  private ElectionController electionControllerMock;

  private FailureHandler failureHandler;

  private Player leader;

  private Set<Player> setWithAll;

  @BeforeEach
  void setUp() throws NoSuchFieldException, IllegalAccessException {
    connectedPlayers = mock(ConnectedPlayers.class);
    sendHeartbeat = mock(SendHeartbeat.class);
    testHeartbeat = mock(AppendEntry.class);
    electionControllerMock = mock(ElectionController.class);
    LeaderFailureDetection leaderFailureDetection = mock(LeaderFailureDetection.class);

    Field leaderField = electionControllerMock.getClass().getDeclaredField("leader");
    leaderField.setAccessible(true);

    leaderField.set(electionControllerMock, leader);

    failureHandler = new FailureHandler(connectedPlayers, sendHeartbeat,
        electionControllerMock, leaderFailureDetection);

    leader = mock(Player.class);
    setWithAll = new HashSet<>();

    when(connectedPlayers.getPlayersWhoAreConnected()).thenReturn(setWithAll);
  }

  @Test
  void receiveHeartbeat_should_send_heartbeat_to_electionController()
      throws UnknownNodeException, IOException {
    when(electionControllerMock.getElectionState()).thenReturn(ElectionState.FOLLOWER);
    when(testHeartbeat.getLeader()).thenReturn(mock(Player.class));

    failureHandler.receiveHeartbeat(leader, testHeartbeat);

    verify(electionControllerMock, times(1)).receiveHeartbeat(testHeartbeat);
  }

  @Test
  void sendHeartbeat_should_get_all_players_and_call_interface_when_leader() {
    when(electionControllerMock.getElectionState()).thenReturn(ElectionState.LEADER);

    failureHandler.sendHeartbeat();

    verify(connectedPlayers, atLeast(1)).getAllPlayers();
    verify(sendHeartbeat, atLeast(1)).sendHeartbeat(setWithAll, electionControllerMock);
  }

  @Test
  void sendHeartbeat_should_get_leader_and_call_interface_when_not_leader()
      throws NoLeaderException {
    when(electionControllerMock.getElectionState()).thenReturn(ElectionState.FOLLOWER);

    when(electionControllerMock.isLeader()).thenReturn(false);
    when(electionControllerMock.getLeader()).thenReturn(leader);
    when(leader.getUsername()).thenReturn("leader");

    failureHandler.sendHeartbeat();

    verify(electionControllerMock, atLeast(1)).getLeader();
    verify(sendHeartbeat, atLeast(1))
        .sendHeartbeat(Mockito.anySet(), Mockito.any(LeaderElection.class));
  }

  @Test
  void sendHeartbeat_should_catch_NoLeaderException()
      throws NoLeaderException {
    when(electionControllerMock.getElectionState()).thenReturn(ElectionState.FOLLOWER);


    when(electionControllerMock.isLeader()).thenReturn(false);
    when(electionControllerMock.getLeader())
        .thenThrow(NoLeaderException.class);

    Assertions.assertDoesNotThrow(() -> failureHandler.sendHeartbeat());
  }
}