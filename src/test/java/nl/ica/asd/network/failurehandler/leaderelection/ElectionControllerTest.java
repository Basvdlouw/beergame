package nl.ica.asd.network.failurehandler.leaderelection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.AppendEntry;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.Vote;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.VoteRequest;
import nl.ica.asd.network.communication.interfaces.SendVoteRequests;
import nl.ica.asd.network.failurehandler.exceptions.NoLeaderException;
import nl.ica.asd.network.playerlobby.ConnectedPlayers;
import nl.ica.asd.util.DomainObjectGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ElectionControllerTest {

  private ConnectedPlayers connectedPlayers;

  private ElectionTimer electionTimerMock;

  private Player player;

  private Player leaderMock;

  private AppendEntry testHeartbeat;

  private ElectionController electionController;

  private SendVoteRequests sendVoteRequests;

  private Field electionState;
  private Field votesReceived;
  private Field leader;

  @BeforeEach
  void setUp() throws NoSuchFieldException, IllegalAccessException {
    connectedPlayers = mock(ConnectedPlayers.class);
    sendVoteRequests = mock(SendVoteRequests.class);
    electionTimerMock = mock(ElectionTimer.class);
    electionTimerMock = mock(ElectionTimer.class);

    player = mock(Player.class);
    when(player.getUsername()).thenReturn("testPlayer");
    leaderMock = mock(Player.class);
    when(leaderMock.getUsername()).thenReturn("testLeader");

    electionController = new ElectionController();

    Field connectedPlayersField = electionController.getClass()
        .getDeclaredField("connectedPlayers");
    connectedPlayersField.setAccessible(true);
    connectedPlayersField.set(electionController, connectedPlayers);

    Field sendVoteRequestField = electionController.getClass().getDeclaredField("sendVoteRequests");
    sendVoteRequestField.setAccessible(true);
    sendVoteRequestField.set(electionController, sendVoteRequests);

    Field electionTimer = electionController.getClass().getDeclaredField("electionTimer");
    electionTimer.setAccessible(true);

    electionState = electionController.getClass().getDeclaredField("state");
    electionState.setAccessible(true);

    votesReceived = electionController.getClass().getDeclaredField("votesReceived");
    votesReceived.setAccessible(true);

    leader = electionController.getClass().getDeclaredField("leader");
    leader.setAccessible(true);

    leader.set(electionController, leaderMock);
    electionTimer.set(electionController, electionTimerMock);

    testHeartbeat = new AppendEntry(0, player, 0, 0, new ArrayList<>(), 0);
  }

  @Test
  void receiveHeartbeat_should_callBecomeFOLLOWER_when_heartbeatContainsNewerElectionTerm()
      throws NoLeaderException {

    testHeartbeat = new AppendEntry(1, player, 0, 0, new ArrayList<>(), 0);

    electionController.receiveHeartbeat(testHeartbeat);

    assertEquals(player, electionController.getLeader());
    assertEquals(1, electionController.getElectionTerm());
    assertEquals(ElectionState.FOLLOWER, electionController.getElectionState());
    verify(electionTimerMock, times(1)).startAndResetTimeout();
  }

  @Test
  void receiveHeartbeat_should_resetElectionTimer_when_heartbeatContainsCurrentElectionTermAndNodeIsNotCANDIDATE() {

    electionController.receiveHeartbeat(testHeartbeat);

    assertEquals(0, electionController.getElectionTerm());
    verify(electionTimerMock, times(1)).startAndResetTimeout();
  }

  @Test
  void receiveHeartbeat_should_callBecomeFollower_when_heartbeatContainsCurrentElectionTermAndNodeIsCANDIDATE()
      throws NoLeaderException, IllegalAccessException {

    electionState.set(electionController, ElectionState.CANDIDATE);

    electionController.receiveHeartbeat(testHeartbeat);

    assertEquals(player, electionController.getLeader());
    assertEquals(0, electionController.getElectionTerm());
    assertEquals(ElectionState.FOLLOWER, electionController.getElectionState());
    verify(electionTimerMock, times(1)).startAndResetTimeout();
  }

  @Test
  void receiveHeartbeat_should_notResetElectionTimer_when_heartbeatContainsOlderElectionTermAndNodeIsNotCANDIDATE() {

    testHeartbeat = new AppendEntry(-1, player, 0, 0, new ArrayList<>(), 0);

    electionController.receiveHeartbeat(testHeartbeat);

    assertEquals(0, electionController.getElectionTerm());
    verify(electionTimerMock, never()).startAndResetTimeout();
  }

  @Test
  void startElection_should_increaseElectionTermChangeElectionStateAddOwnVoteAndSendVoteRequests_when_stateIsFOLLOWER()
      throws IllegalAccessException, IOException {
    Player myself = DomainObjectGenerator.getPlayer(1);
    when(connectedPlayers.getPlayerForSelf()).thenReturn(myself);
    Set<Player> playerSet = new HashSet<>();
    playerSet.add(myself);
    playerSet.add(DomainObjectGenerator.getPlayer(2));
    when(connectedPlayers.getAllPlayers()).thenReturn(playerSet);
    electionState.set(electionController, ElectionState.FOLLOWER);

    electionController.startElection();
    verify(sendVoteRequests).sendVoteRequests(playerSet, electionController, myself);

    assertEquals(1, electionController.getElectionTerm());
    assertEquals(ElectionState.CANDIDATE, electionState.get(electionController));
    assertEquals(1, ((Set) votesReceived.get(electionController)).size());
  }

  @Test
  void startElection_should_increaseElectionTermChangeElectionStateAddOwnVoteAndSendVoteRequests_when_stateIsCANDIDATE()
      throws IllegalAccessException, IOException {

    Set<Player> playerSet = new HashSet<>();
    playerSet.add(DomainObjectGenerator.getPlayer(2));
    playerSet.add(DomainObjectGenerator.getPlayer(3));
    when(connectedPlayers.getAllPlayers()).thenReturn(playerSet);

    electionState.set(electionController, ElectionState.CANDIDATE);

    electionController.startElection();

    assertEquals(1, electionController.getElectionTerm());
    assertEquals(ElectionState.CANDIDATE, electionState.get(electionController));
    assertEquals(1, ((Set) votesReceived.get(electionController)).size());
  }

  @Test
  void startElection_should_notStartElection_when_nodeIsLeader()
      throws IllegalAccessException, IOException {

    electionState.set(electionController, ElectionState.LEADER);

    electionController.startElection();

    assertEquals(0, electionController.getElectionTerm());
    verify(electionTimerMock, times(1)).startAndResetTimeout();
  }

  @Test
  void getLeader_should_throwNoLeaderExceptions_when_noLeaderIsSet()
      throws IllegalAccessException {

    leader.set(electionController, null);

    Assertions.assertThrows(NoLeaderException.class, () -> electionController.getLeader());

  }

  @Test
  void receiveVoteRequest_should_returnAVoteGrantedNotChangeElectionTermChangeElectionStateAndResetElectionTimer_when_electionTermIsHigherAndLastLogIndexIsHigher()
      throws IllegalAccessException {

    electionState.set(electionController, ElectionState.LEADER);
    VoteRequest voteRequest = new VoteRequest(1, player, 1, 0);

    Vote vote = electionController.receiveVoteRequest(voteRequest, 0, 0);

    assertTrue(vote.isVoteGranted());
    assertEquals(1, vote.getElectionTerm());

    assertEquals(0, electionController.getElectionTerm());
    assertEquals(ElectionState.FOLLOWER, electionState.get(electionController));
    verify(electionTimerMock, times(1)).startAndResetTimeout();
  }

  @Test
  void receiveVoteRequest_should_returnAVoteGrantedNotChangeElectionTermChangeElectionStateAndResetElectionTimer_when_electionTermIsHigherAndLastLogIndexIsTheSame()
      throws IllegalAccessException {

    electionState.set(electionController, ElectionState.LEADER);

    VoteRequest voteRequest = new VoteRequest(1, player, 0, 0);

    Vote vote = electionController.receiveVoteRequest(voteRequest, 0, 0);

    assertTrue(vote.isVoteGranted());
    assertEquals(1, vote.getElectionTerm());

    assertEquals(0, electionController.getElectionTerm());
    assertEquals(ElectionState.FOLLOWER, electionState.get(electionController));
    verify(electionTimerMock, times(1)).startAndResetTimeout();
  }

  @Test
  void receiveVoteRequest_should_notReturnAVoteGranted_when_electionTermIsLower()
      throws IllegalAccessException {

    electionState.set(electionController, ElectionState.LEADER);

    VoteRequest voteRequest = new VoteRequest(-1, player, 1, 0);

    Vote vote = electionController.receiveVoteRequest(voteRequest, 0, 0);

    assertFalse(vote.isVoteGranted());
    assertEquals(-1, vote.getElectionTerm());

    assertEquals(0, electionController.getElectionTerm());
    assertEquals(ElectionState.LEADER, electionState.get(electionController));
    verify(electionTimerMock, never()).startAndResetTimeout();
  }

  @Test
  void receiveVoteRequest_should_notReturnAVoteGranted_when_electionTermIsTheSame()
      throws IllegalAccessException {

    electionState.set(electionController, ElectionState.LEADER);

    VoteRequest voteRequest = new VoteRequest(0, player, 1, 0);

    Vote vote = electionController.receiveVoteRequest(voteRequest, 0, 1);

    assertFalse(vote.isVoteGranted());
    assertEquals(0, vote.getElectionTerm());

    assertEquals(0, electionController.getElectionTerm());
    assertEquals(ElectionState.LEADER, electionState.get(electionController));
    verify(electionTimerMock, never()).startAndResetTimeout();
  }

  @Test
  void receiveVoteRequest_should_notReturnAVoteGranted_when_LastLogIndexIsLower()
      throws IllegalAccessException {

    electionState.set(electionController, ElectionState.LEADER);

    VoteRequest voteRequest = new VoteRequest(1, player, -1, 0);

    Vote vote = electionController.receiveVoteRequest(voteRequest, 0, 0);

    assertFalse(vote.isVoteGranted());
    assertEquals(1, vote.getElectionTerm());

    assertEquals(0, electionController.getElectionTerm());
    assertEquals(ElectionState.LEADER, electionState.get(electionController));
    verify(electionTimerMock, never()).startAndResetTimeout();
  }

  @Test
  void receiveVote_should_addVoteToVotesReceived_when_nodeIsCANDIDATEVoteIsGrantedElectionTermIsTheSameAndPlayerHasNotAlreadyVotedForNode()
      throws IllegalAccessException, IOException {

    electionState.set(electionController, ElectionState.CANDIDATE);

    when(connectedPlayers.getAllPlayers()).thenReturn((getPlayerSetWithNPlayers(5)));

    Vote testVote = new Vote(0, true);
    Player testPlayer = mock(Player.class);

    assertEquals(0, ((Set) votesReceived.get(electionController)).size());

    electionController.receiveVote(testVote, testPlayer);

    assertEquals(1, ((Set) votesReceived.get(electionController)).size());
  }

  @Test
  void receiveVote_should_notAddVoteToVotesReceived_when_nodeIsFOLLOWER()
      throws IllegalAccessException, IOException {

    electionState.set(electionController, ElectionState.FOLLOWER);

    Vote testVote = new Vote(0, true);
    Player testPlayer = mock(Player.class);

    assertEquals(0, ((Set) votesReceived.get(electionController)).size());

    electionController.receiveVote(testVote, testPlayer);

    assertEquals(0, ((Set) votesReceived.get(electionController)).size());
  }

  @Test
  void receiveVote_should_notAddVoteToVotesReceived_when_nodeIsLEADER()
      throws IllegalAccessException, IOException {

    electionState.set(electionController, ElectionState.LEADER);

    Vote testVote = new Vote(0, true);
    Player testPlayer = mock(Player.class);

    assertEquals(0, ((Set) votesReceived.get(electionController)).size());

    electionController.receiveVote(testVote, testPlayer);

    assertEquals(0, ((Set) votesReceived.get(electionController)).size());

  }

  @Test
  void receiveVote_should_notAddVoteToVotesReceived_when_VoteIsNotGranted()
      throws IllegalAccessException, IOException {

    electionState.set(electionController, ElectionState.CANDIDATE);

    Vote testVote = new Vote(0, false);
    Player testPlayer = mock(Player.class);

    assertEquals(0, ((Set) votesReceived.get(electionController)).size());

    electionController.receiveVote(testVote, testPlayer);

    assertEquals(0, ((Set) votesReceived.get(electionController)).size());
  }

  @Test
  void receiveVote_should_notAddVoteToVotesReceived_when_ElectionTermIsNotTheSame()
      throws IllegalAccessException, IOException {

    electionState.set(electionController, ElectionState.CANDIDATE);

    Vote testVote = new Vote(-1, true);
    Player testPlayer = mock(Player.class);

    assertEquals(0, ((Set) votesReceived.get(electionController)).size());

    electionController.receiveVote(testVote, testPlayer);

    assertEquals(0, ((Set) votesReceived.get(electionController)).size());

  }

  @Test
  void receiveVote_should_notAddVoteToVotesReceived_when_PlayerHasAlreadyVotedForNode()
      throws IllegalAccessException, IOException {

    electionState.set(electionController, ElectionState.CANDIDATE);

    when(connectedPlayers.getAllPlayers()).thenReturn((getPlayerSetWithNPlayers(5)));

    Vote testVote = new Vote(0, true);
    Player testPlayer = mock(Player.class);

    assertEquals(0, ((Set) votesReceived.get(electionController)).size());

    electionController.receiveVote(testVote, testPlayer);

    assertEquals(1, ((Set) votesReceived.get(electionController)).size());

    electionController.receiveVote(testVote, testPlayer);

    assertEquals(1, ((Set) votesReceived.get(electionController)).size());
  }

  @Test
  void receiveVote_should_causeNodeToBecomeLEADER_when_enoughVotesAreReceived()
      throws IllegalAccessException, NoLeaderException, IOException {

    electionState.set(electionController, ElectionState.CANDIDATE);

    Vote testVote = new Vote(0, true);
    Player testPlayer = mock(Player.class);
    Player testPlayerToReachMajority = mock(Player.class);
    Player playerForSelf = mock(Player.class);

    when(connectedPlayers.getPlayerForSelf()).thenReturn(playerForSelf);
    when(connectedPlayers.getAllPlayers()).thenReturn((getPlayerSetWithNPlayers(3)));

    assertEquals(0, ((Set) votesReceived.get(electionController)).size());

    electionController.receiveVote(testVote, testPlayer);

    assertEquals(leaderMock, electionController.getLeader());
    assertEquals(1, ((Set) votesReceived.get(electionController)).size());
    electionState.set(electionController, ElectionState.CANDIDATE);

    electionController.receiveVote(testVote, testPlayerToReachMajority);

    assertEquals(playerForSelf, electionController.getLeader());
    assertEquals(0, ((Set) votesReceived.get(electionController)).size());
    electionState.set(electionController, ElectionState.LEADER);
  }

  private Set<Player> getPlayerSetWithNPlayers(int n) {

    Set<Player> newSet = new HashSet<>();
    for (int i = 0; i < n; i++) {
      Player player = mock(Player.class);
      newSet.add(player);
    }

    return newSet;
  }

}
