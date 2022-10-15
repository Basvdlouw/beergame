package nl.ica.asd.network.communication.twophasecommit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.network.communication.DataTransferObjectGenerator;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.AppendEntry;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.AppendEntryResult;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.InitialConnectionRequest;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.InitialConnectionResponse;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.Vote;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.VoteRequest;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.LogEntry;
import nl.ica.asd.network.communication.gameinitialisation.ConnectionResult;
import nl.ica.asd.network.failurehandler.HeartbeatHandler;
import nl.ica.asd.network.failurehandler.LeaderElection;
import nl.ica.asd.network.failurehandler.exceptions.UnknownNodeException;
import nl.ica.asd.network.failurehandler.leaderelection.ElectionState;
import nl.ica.asd.network.playerlobby.PlayerLobby;
import nl.ica.asd.storage.domain.GameData;
import nl.ica.asd.storage.gameconfig.GameConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class IncomingDataControllerTest {

  private IncomingDataController sut;

  @Mock
  private LeaderElection leaderElection;
  @Mock
  private HeartbeatHandler heartbeatHandler;
  @Mock
  private LogController logController;
  @Mock
  private PlayerLobby playerLobby;
  @Mock
  private GameConfig gameConfig;
  @Mock
  private GameData gameData;

  @BeforeEach
  void setUp() {
    leaderElection = mock(LeaderElection.class);
    heartbeatHandler = mock(HeartbeatHandler.class);
    logController = mock(LogController.class);
    playerLobby = mock(PlayerLobby.class);
    gameConfig = mock(GameConfig.class);
    gameData = mock(GameData.class);

    sut = new IncomingDataController(
        leaderElection, heartbeatHandler, logController, playerLobby, gameConfig);
  }

  @Test
  void receiveAppendEntry_should_returnASuccessfulAppendEntryResult_when_ThePreviousLogIndexAndPreviousElectionTermOfTheAppendEntryMatchThePreviousLogIndexAndPreviousElectionTernOfTheElectionControllerAndTheElectionStateIsFollower()
      throws IOException {
    AppendEntry appendEntry = DataTransferObjectGenerator.getAppendEntry(1);
    Player player = appendEntry.getLeader();
    when(leaderElection.getElectionTerm()).thenReturn(1);
    when(leaderElection.getElectionState()).thenReturn(ElectionState.FOLLOWER);
    when(logController.verifyElectionTermOfLogEntryMatches(anyInt(), anyInt())).thenReturn(true);
    AppendEntryResult appendEntryResult = sut.receiveAppendEntry(player, appendEntry);
    assertEquals(1, appendEntryResult.getElectionTerm());
    assertTrue(appendEntryResult.isSuccess());
  }

  @Test
  void receiveAppendEntry_should_returnASuccessfulAppendEntryResult_when_theLeaderReceivesNewEntries()
      throws IOException {
    AppendEntry appendEntry = DataTransferObjectGenerator.getAppendEntry(1);
    Player player = appendEntry.getLeader();
    when(leaderElection.getElectionTerm()).thenReturn(1);
    when(leaderElection.getElectionState()).thenReturn(ElectionState.LEADER);
    AppendEntryResult appendEntryResult = sut.receiveAppendEntry(player, appendEntry);
    assertEquals(1, appendEntryResult.getElectionTerm());
    assertTrue(appendEntryResult.isSuccess());
  }

  @Test
  void receiveAppendEntry_should_returnAUnsuccessfulAppendEntryResult_when_ACandidateReceivesNewEntries()
      throws IOException {
    AppendEntry appendEntry = DataTransferObjectGenerator.getAppendEntry(1);
    Player player = appendEntry.getLeader();
    when(leaderElection.getElectionTerm()).thenReturn(1);
    when(leaderElection.getElectionState()).thenReturn(ElectionState.CANDIDATE);
    AppendEntryResult appendEntryResult = sut.receiveAppendEntry(player, appendEntry);
    assertEquals(1, appendEntryResult.getElectionTerm());
    assertFalse(appendEntryResult.isSuccess());
  }

  @Test
  void receiveAppendEntry_should_returnAUnsuccessfulAppendEntryResult_when_theSenderOfTheAppendEntryIsNotPartOfTheGame()
      throws UnknownNodeException, IOException {
    AppendEntry appendEntry = DataTransferObjectGenerator.getAppendEntry(1);
    Player player = appendEntry.getLeader();
    when(leaderElection.getElectionTerm()).thenReturn(1);
    doThrow(new UnknownNodeException("Node not part of this game")).when(heartbeatHandler)
        .receiveHeartbeat(any(Player.class), any(AppendEntry.class));
    AppendEntryResult appendEntryResult = sut.receiveAppendEntry(player, appendEntry);
  }

  @Test
  void receiveAppendEntry_should_returnAUnsuccessfulAppendEntryResult_when_electionControllerElectionTermIsHigherThanAppendEntryElectionTerm()
      throws IOException {
    AppendEntry appendEntry = DataTransferObjectGenerator.getAppendEntry(1);
    Player player = appendEntry.getLeader();
    when(leaderElection.getElectionTerm()).thenReturn(2);
    when(leaderElection.getElectionState()).thenReturn(ElectionState.FOLLOWER);

    AppendEntryResult appendEntryResult = sut.receiveAppendEntry(player, appendEntry);
    assertEquals(2, appendEntryResult.getElectionTerm());
    assertFalse(appendEntryResult.isSuccess());
  }

  @Test
  void receiveVoteRequest() {
    VoteRequest voteRequest = DataTransferObjectGenerator.getVoteRequest(1);
    Player player = DataTransferObjectGenerator.getPlayer(2);
    Vote vote = new Vote(1, true);
    LogEntry logEntry = DataTransferObjectGenerator.getOrderUpdate(3, 0);
    when(logController.getLastLogEntry()).thenReturn(logEntry);
    when(leaderElection
        .receiveVoteRequest(voteRequest, logEntry.getLogIndex(), logEntry.getElectionTerm()))
        .thenReturn(vote);
    Vote result = sut.receiveVoteRequest(player, voteRequest);
    assertEquals(vote, result);
  }

  @Test
  void receiveConnectionRequest_should_returnASuccessfulConnectionResponse_when_playerLobbyIsNotFull() {
    Player sender = DataTransferObjectGenerator.getPlayer(1);
    InitialConnectionRequest initialConnectionRequest = new InitialConnectionRequest(sender);

    when(playerLobby.isFull(gameConfig)).thenReturn(false);
    when(gameConfig.getGameData()).thenReturn(gameData);

    InitialConnectionResponse result = sut
        .receiveConnectionRequest(initialConnectionRequest);
    assertEquals(ConnectionResult.SUCCESS, result.getConnectionResult());
  }

  @Test
  void receiveConnectionRequest_should_returnAnUnsuccessfulConnectionResponse_when_playerLobbyIsFull() {
    Player sender = DataTransferObjectGenerator.getPlayer(1);
    InitialConnectionRequest initialConnectionRequest = new InitialConnectionRequest(sender);

    when(playerLobby.isFull(gameConfig)).thenReturn(true);
    when(gameConfig.getGameData()).thenReturn(gameData);

    InitialConnectionResponse result = sut
        .receiveConnectionRequest(initialConnectionRequest);
    assertEquals(ConnectionResult.LOBBY_FULL, result.getConnectionResult());
  }

  @Test
  void receiveConnectionRequest_should_returnAnUnsuccessfulConnectionResponse_when_usernameIsAlreadyChosen() {
    Player sender = DataTransferObjectGenerator.getPlayer(1);
    InitialConnectionRequest initialConnectionRequest = new InitialConnectionRequest(sender);

    when(playerLobby.isFull(gameConfig)).thenReturn(false);
    Set<Player> players = new HashSet<>();
    players.add(sender);
    when(playerLobby.getPlayersWhoAreConnected()).thenReturn(players);
    when(gameConfig.getGameData()).thenReturn(gameData);

    InitialConnectionResponse result = sut
        .receiveConnectionRequest(initialConnectionRequest);
    assertEquals(ConnectionResult.USERNAME_ALREADY_CHOSEN, result.getConnectionResult());
  }
}