package nl.ica.asd.network.communication.twophasecommit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import nl.ica.asd.logic.domain.BusinessRules;
import nl.ica.asd.logic.domain.GameState;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.logic.domain.actions.OrderAction;
import nl.ica.asd.network.communication.DataTransferObjectGenerator;
import nl.ica.asd.network.communication.datatransfer.DataSenderForJsonSerializableObjects;
import nl.ica.asd.network.communication.datatransferobjects.DataTransferObject;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.AppendEntry;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.AppendEntryResult;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.Vote;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.VoteRequest;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.LogEntry;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.logentrydatatypes.BusinessUpdate;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.logentrydatatypes.UpdateType;
import nl.ica.asd.network.communication.gameinitialisation.InitializeGame;
import nl.ica.asd.network.communication.twophasecommit.Messenger.CallBackHandler;
import nl.ica.asd.network.failurehandler.LeaderElection;
import nl.ica.asd.network.failurehandler.exceptions.NoLeaderException;
import nl.ica.asd.network.failurehandler.leaderelection.ElectionState;
import nl.ica.asd.storage.exception.UnknownGameException;
import nl.ica.asd.util.DomainObjectGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

class OutgoingDataControllerTest {

  private Field queuedLogEntries;
  private Set<Player> playerSet;

  @Mock
  private LogController logController;

  @Mock
  private LeaderElection leaderElection;

  @Mock
  private CallBackHandler callBackHandler;

  @Mock
  DataSenderForJsonSerializableObjects dataSenderForJsonSerializableObjects;

  @Mock
  InitializeGame initializeGame;

  private OutgoingDataController sut;


  @BeforeEach
  void setUp() throws NoSuchFieldException, IllegalAccessException {
    leaderElection = mock(LeaderElection.class);
    logController = mock(LogController.class);
    callBackHandler = mock(CallBackHandler.class);
    dataSenderForJsonSerializableObjects = mock(DataSenderForJsonSerializableObjects.class);
    sut = new OutgoingDataController(logController, dataSenderForJsonSerializableObjects,
        initializeGame);
    queuedLogEntries = sut.getClass().getDeclaredField("queuedLogEntries");
    queuedLogEntries.setAccessible(true);
    playerSet = new TreeSet<>();
  }

  @Test
  void sendBusinessRules_should_addBusinessRuleToTheQueuedLogEntries()
      throws IllegalAccessException {
    BusinessRules businessRules = mock(BusinessRules.class);
    sut.sendBusinessRules(businessRules);
    assertEquals(1, ((List<LogEntry>) queuedLogEntries.get(sut)).size());
  }

  @Test
  void sendHeartbeat_should_haveAnEmptyLogEntry_when_heartbeatIsSentAsAFollower()
      throws IOException, NoLeaderException, IllegalAccessException {
    Random r = new Random();
    for (int i = 0; i < 100; i++) {
      Player player = mock(Player.class);
      playerSet.add(player);
    }

    Player player = DataTransferObjectGenerator.getPlayer(1);
    when(leaderElection.getElectionState()).thenReturn(ElectionState.FOLLOWER);
    when(callBackHandler.getLeaderElection()).thenReturn(leaderElection);
    when(leaderElection.getElectionTerm()).thenReturn(1);
    when(leaderElection.getLeader()).thenReturn(player);
    doReturn(new BusinessUpdate(0, 0, UpdateType.SET, null, UUID.nameUUIDFromBytes("1".getBytes())))
        .when(logController).getLastLogEntry();

    sut.sendHeartbeat(playerSet, callBackHandler);

    assertEquals(0, ((List<LogEntry>) queuedLogEntries.get(sut)).size());
  }

  @Test
  void sendHeartbeat_should_haveAnEmptyLogEntry_when_heartbeatIsSentAsALeader()
      throws IOException, NoLeaderException, IllegalAccessException {
    for (int i = 0; i < 100; i++) {
      Player player = mock(Player.class);
      playerSet.add(player);
    }
    Player player = DataTransferObjectGenerator.getPlayer(1);
    when(leaderElection.getElectionState()).thenReturn(ElectionState.FOLLOWER);
    when(callBackHandler.getLeaderElection()).thenReturn(leaderElection);
    when(leaderElection.getElectionTerm()).thenReturn(1);
    when(leaderElection.getLeader()).thenReturn(player);
    doReturn(new BusinessUpdate(0, 0, UpdateType.SET, null, UUID.nameUUIDFromBytes("1".getBytes())))
        .when(logController).getLastLogEntry();

    sut.sendHeartbeat(playerSet, callBackHandler);

    assertEquals(0, ((List<LogEntry>) queuedLogEntries.get(sut)).size());
  }


  @Test
  void sendOrder_should_addOrderToTheQueuedLogEntries() throws IllegalAccessException {
    OrderAction ac = mock(OrderAction.class);
    sut.sendOrder(ac);
    assertEquals(1, ((List<LogEntry>) queuedLogEntries.get(sut)).size());
  }

  @Test
  void sendPlayerListUpdate_should_addPlayerUpdateToTheQueuedLogEntries()
      throws IllegalAccessException {
    Player player = mock(Player.class);
    sut.sendPlayerListUpdate(player);
    assertEquals(1, ((List<LogEntry>) queuedLogEntries.get(sut)).size());
  }

  @Test
  void sendGameStateUpdate_should_addGameStateUpdateToTheQueuedLogEntries()
      throws IllegalAccessException {
    GameState gameState = GameState.STARTED;
    sut.sendGameStateUpdate(gameState);
    assertEquals(1, ((List<LogEntry>) queuedLogEntries.get(sut)).size());
  }

  @Test
  void receiveVote_should_callVoteRequestHandlerDotReceiveVote() throws IOException {
    Vote vote = new Vote(1, true);
    Player player = DataTransferObjectGenerator.getPlayer(2);
    sut.receiveVote(vote, player, leaderElection);
    verify(leaderElection, times(1)).receiveVote(vote, player);
  }

  @Test
  void receiveAppendEntryResult_should_tryToUpdateTheCommitIndexOfLogController_when_electionStateIsLeaderAndAppendsEntryResultIsSuccessful()
      throws UnknownGameException {
    when(callBackHandler.getLeaderElection()).thenReturn(leaderElection);
    when(leaderElection.getElectionState()).thenReturn(ElectionState.LEADER);
    Player player = DataTransferObjectGenerator.getPlayer(1);
    AppendEntry appendEntry = DataTransferObjectGenerator.getAppendEntry(3);
    AppendEntryResult appendEntryResult = new AppendEntryResult(1, 3, true);

    sut.receiveAppendEntryResult(appendEntry, appendEntryResult, player, leaderElection);

    verify(logController, times(1)).updateCommitIndex(player, 3);
  }

  @Test
  void sendVoteRequest_should_sendAVoteRequestToAllGivenPlayers_when_called() throws IOException {
    Player player1 = DomainObjectGenerator.getPlayer(1);
    Player player2 = DomainObjectGenerator.getPlayer(2);
    Set<Player> players = new HashSet<>();
    players.add(player1);
    players.add(player2);
    Player leader = DomainObjectGenerator.getPlayer(3);

    when(callBackHandler.getLeaderElection()).thenReturn(leaderElection);
    when(leaderElection.getElectionTerm()).thenReturn(1);
    when(logController.getLastLogIndex()).thenReturn(1);
    when(logController.getLastLogElectionTerm()).thenReturn(1);

    sut.sendVoteRequest(players, callBackHandler, leader);

    ArgumentCaptor<DataTransferObject> argumentCaptor = ArgumentCaptor
        .forClass(DataTransferObject.class);

    verify(dataSenderForJsonSerializableObjects)
        .sendObjectAsSerializedData(eq(player1), argumentCaptor.capture(), eq(callBackHandler));
    assertEquals(VoteRequest.class, argumentCaptor.getValue().getClass());
    verify(dataSenderForJsonSerializableObjects)
        .sendObjectAsSerializedData(eq(player2), argumentCaptor.capture(), eq(callBackHandler));
    assertEquals(VoteRequest.class, argumentCaptor.getValue().getClass());
  }

  @Test
  void receiveAppendEntryResult_should_clearSentLogEntriesFromQueuedLogEntries_when_theyAreSuccessfullyReceivedByTheTargetNode()
      throws IllegalAccessException, NoLeaderException, IOException {
    Player player = DataTransferObjectGenerator.getPlayer(1);
    when(leaderElection.getElectionState()).thenReturn(ElectionState.LEADER);

    sut.sendBusinessRules(DataTransferObjectGenerator.getBusinessRules(1));
    List<LogEntry> queue = (List<LogEntry>) queuedLogEntries.get(sut);
    BusinessUpdate businessUpdate = new BusinessUpdate(0, Integer.MIN_VALUE, UpdateType.SET,
        DomainObjectGenerator.getBusinessRules(0), queue.get(0).getQueueIdentifier());
    doReturn(businessUpdate).when(logController).getLastLogEntry();

    List<LogEntry> logEntries = new ArrayList<>();
    logEntries.add(businessUpdate);
    AppendEntry originalRequest = new AppendEntry(0, DataTransferObjectGenerator.getPlayer(0), 0, 0,
        logEntries, 0);
    AppendEntryResult appendEntryResult = DataTransferObjectGenerator.getAppendEntryResult(2);

    when(callBackHandler.getLeaderElection()).thenReturn(leaderElection);
    when(leaderElection.getElectionTerm()).thenReturn(0);
    when(leaderElection.getLeader()).thenReturn(player);

    playerSet.add(player);

    sut.sendHeartbeat(playerSet, callBackHandler);
    sut.receiveAppendEntryResult(originalRequest, appendEntryResult, player, leaderElection);

    assertEquals(0, queue.size());
  }
}