package nl.ica.asd.network.communication.twophasecommit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Optional;
import java.util.TreeSet;
import java.util.UUID;
import nl.ica.asd.logic.domain.BusinessType;
import nl.ica.asd.logic.domain.GameState;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.logic.domain.PlayerStatus;
import nl.ica.asd.logic.domain.builders.PlayerBuilder;
import nl.ica.asd.network.communication.DataTransferObjectGenerator;
import nl.ica.asd.network.communication.clientserver.Server;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.AppendEntry;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.AppendEntryResult;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.InitialConnectionRequest;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.InitialConnectionResponse;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.Vote;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.VoteRequest;
import nl.ica.asd.network.communication.gameinitialisation.ConnectionResult;
import nl.ica.asd.network.communication.gameinitialisation.InitializeGame;
import nl.ica.asd.network.communication.interfaces.PublicIPAddress;
import nl.ica.asd.network.communication.twophasecommit.Messenger.CallBackHandler;
import nl.ica.asd.network.failurehandler.LeaderElection;
import nl.ica.asd.network.playerlobby.PlayerLobby;
import nl.ica.asd.storage.domain.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class MessengerTest {

  private PublicIPAddress publicIPAddress;

  private OutgoingDataController outgoingDataController;

  private CallBackHandler callBackHandler;

  private LeaderElection leaderElection;

  private GameData gameData;

  @Mock
  private InitializeGame initializeGame;

  @Mock
  private PlayerLobby playerLobby;

  @Mock
  private Server server;

  @InjectMocks
  private Messenger messenger;

  @BeforeEach
  void setUp() throws NoSuchFieldException, IllegalAccessException {
    MockitoAnnotations.initMocks(this);
    outgoingDataController = mock(OutgoingDataController.class);
    publicIPAddress = mock(PublicIPAddress.class);
    callBackHandler = mock(CallBackHandler.class);
    leaderElection = mock(LeaderElection.class);
    gameData = mock(GameData.class);
    messenger = new Messenger(outgoingDataController, publicIPAddress);
    initializeGame = mock(InitializeGame.class);

    Field initializeGameField = messenger.getClass().getDeclaredField("initializeGame");
    initializeGameField.setAccessible(true);

    initializeGameField.set(messenger, initializeGame);
  }

  @Test
  void sendGameStateUpdate_should_callSendGameStateUpdateInOutgoingDataController_when_called() {
    GameState testGameState = GameState.STARTED;
    messenger.sendGameStateUpdate(testGameState);
    verify(outgoingDataController, times(1)).sendGameStateUpdate(testGameState);
  }

  @Test
  void receiveResponse_should_invokeOutgoingDataControllerMock_when_receivingVote() {
    final VoteRequest voteRequest = new VoteRequest(2,
        DataTransferObjectGenerator.getPlayer(1), 1, 1);
    final Vote vote = new Vote(2, true);
    final Player responder = DataTransferObjectGenerator.getPlayer(2);
    when(callBackHandler.getLeaderElection()).thenReturn(leaderElection);
    messenger.receiveResponse(voteRequest, vote, responder, callBackHandler);
    verify(outgoingDataController).receiveVote(vote, responder, leaderElection);
  }

  @Test
  void receiveResponse_should_invokeOutgoingDataControllerMock_when_receivingAppendEntryResult() {
    final AppendEntryResult appendEntryResult = new AppendEntryResult(1, 1, true);
    final AppendEntry appendEntry = new AppendEntry(2,
        DataTransferObjectGenerator.getPlayer(1), 1, 1, new ArrayList<>(), 1);
    final Player responder = DataTransferObjectGenerator.getPlayer(2);
    when(callBackHandler.getLeaderElection()).thenReturn(leaderElection);
    messenger.receiveResponse(appendEntry, appendEntryResult, responder, callBackHandler);
    verify(outgoingDataController)
        .receiveAppendEntryResult(appendEntry, appendEntryResult, responder, leaderElection);
  }

  @Test
  void receiveResponse_should_invokeOutgoingDataControllerMock_when_receivingInitialConnectionResponse() {
    final InitialConnectionResponse initialConnectionResponse = new InitialConnectionResponse(
        new TreeSet<>(), gameData, ConnectionResult.SUCCESS);
    final InitialConnectionRequest initialConnectionRequest = new InitialConnectionRequest(
        DataTransferObjectGenerator.getPlayer(1));
    final Player responder = DataTransferObjectGenerator.getPlayer(2);
    messenger.receiveResponse(initialConnectionRequest, initialConnectionResponse, responder,
        callBackHandler);
    verify(outgoingDataController)
        .receiveConnectionResponse(initialConnectionRequest, initialConnectionResponse);
  }

  @Test
  void connectToGame_should_invokeOutgoingDataControllerMock_when_called() throws IOException {
    final ArgumentCaptor<Player> captor1 = ArgumentCaptor.forClass(Player.class);
    final ArgumentCaptor<Player> captor2 = ArgumentCaptor.forClass(Player.class);
    final String serverIP = "216.3.128.11";
    final String clientIP = "216.3.120.12";
    final String username = "testname";
    doReturn(clientIP).when(publicIPAddress).getIPAddress();
    messenger.connectToGame(serverIP, username);
    verify(outgoingDataController).connectToGame(captor1.capture(), captor2.capture(), any());
    verify(outgoingDataController)
        .connectToGame(any(Player.class), any(Player.class), any(CallBackHandler.class));
    assertEquals(serverIP, captor1.getValue().getIP());
    assertEquals(clientIP, captor2.getValue().getIP());
    assertEquals(username, captor2.getValue().getUsername());
  }

  @Test
  void leaveGame_should_sendANewPlayerUpdateWithLeftOnPurposeMessage() {
    Player player = PlayerBuilder.aPlayer().withUsername("Test")
        .withPlayerStatus(PlayerStatus.CONNECTED_AND_ACTIVE)
        .withIp("123.456.789.101")
        .withPort(666)
        .withBusinessUUID(UUID.randomUUID())
        .withBusinessType(BusinessType.FACTORY).build();
    doNothing().when(outgoingDataController).sendPlayerListUpdate(player);
    doNothing().when(initializeGame).reset();
    messenger.leaveGame(player);
    initializeGame.reset();
    verify(outgoingDataController, times(1)).sendPlayerListUpdate(player);
  }
}