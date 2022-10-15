package nl.ica.asd.network.failurehandler.leader;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import nl.ica.asd.frontend.gui.service.GuiService;
import nl.ica.asd.logic.domain.BusinessType;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.logic.domain.PlayerStatus;
import nl.ica.asd.logic.domain.builders.PlayerBuilder;
import nl.ica.asd.logic.gamemanager.GameManager;
import nl.ica.asd.network.communication.gameinitialisation.DefaultIPAddressProvider;
import nl.ica.asd.network.communication.twophasecommit.Messenger;
import nl.ica.asd.network.failurehandler.LeaderElection;
import nl.ica.asd.network.failurehandler.exceptions.UnknownNodeException;
import nl.ica.asd.network.playerlobby.PlayerLobby;
import nl.ica.asd.network.playerlobby.exceptions.PlayerNotFoundException;
import nl.ica.asd.storage.applicationdatastorage.ApplicationDataAccess;
import nl.ica.asd.storage.exception.UnknownBusinessException;
import nl.ica.asd.storage.exception.UnknownGameException;
import nl.ica.asd.storage.gamedatastorage.GameDataAccess;
import nl.ica.asd.util.TestHelpers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.collections.Sets;


public class LeaderFailureDetectionTest {

  //Mock for test which can be mocked.
  private PlayerLobby lobby;

  private Messenger messenger;

  //Non mock for tests which need an actual object.
  private PlayerLobby playerLobby;

  private Player ryanPlayer;

  @Mock
  private GameManager gameManager;

  @Mock
  private GameDataAccess gameDataAccess;

  @Mock
  private ApplicationDataAccess applicationDataAccess;

  @Mock
  private DefaultIPAddressProvider defaultIPAddressProvider;

  @Mock
  private GuiService guiService;

  @InjectMocks
  private LeaderFailureDetection leaderFailureDetection;

  @BeforeEach
  void setUp() throws UnknownGameException, UnknownBusinessException {
    lobby = mock(PlayerLobby.class);
    messenger = mock(Messenger.class);
    LeaderElection leaderElection = mock(LeaderElection.class);
    leaderFailureDetection = new LeaderFailureDetection(lobby, messenger, leaderElection);

    ryanPlayer = PlayerBuilder.aPlayer().withUsername("ryanPlayer")
        .withPlayerStatus(PlayerStatus.CONNECTED_AND_ACTIVE)
        .withBusinessUUID(UUID.randomUUID()).build();

    gameManager = mock(GameManager.class);
    gameDataAccess = mock(GameDataAccess.class);
    applicationDataAccess = mock(ApplicationDataAccess.class);
    defaultIPAddressProvider = mock(DefaultIPAddressProvider.class);
    guiService = mock(GuiService.class);

    when(gameDataAccess.getBeerGame()).thenReturn(TestHelpers.createTestingBeerGame());
    when(applicationDataAccess.getUsername()).thenReturn("ryanPlayer");

    playerLobby = new PlayerLobby(gameManager,
        gameDataAccess,
        applicationDataAccess,
        defaultIPAddressProvider,
        guiService);

    when(gameDataAccess.getBusinessType(any())).thenReturn(BusinessType.FACTORY);

    playerLobby.resetPlayers(Sets.newSet(ryanPlayer));

    MockitoAnnotations.initMocks(this);
  }

  @Test
  void receiveHeartbeat_ShouldCallLobbyRefreshPLayerTimeStamp_WhenLeaderFailureDetectionIsCalled()
      throws UnknownNodeException {

    leaderFailureDetection.receiveHeartbeat(new Player("Testplayer",
        UUID.randomUUID(),
        BusinessType.FACTORY,
        PlayerStatus.CONNECTED_AND_ACTIVE,
        "123",
        123));
    verify(lobby).refreshPlayerTimestamp(any(), any());
  }

  @Test
  void detectionLoop_Should_BeStartedWhenFunctionIsCalled() {

    assertFalse(leaderFailureDetection.getDetectionLoopStatus());
  }

  @Test
  void detectionLoop_Should_BeFlipped_When_LoopIsStopped() {

    leaderFailureDetection.stop();
    assertTrue(leaderFailureDetection.getDetectionLoopStatus());
  }

  @Test
  void player_Should_BeAddedToSet_When_sendPlayerUpdateIsCalled() {

    leaderFailureDetection.sendPlayerUpdate(new Player("SendPlayerUpdateTest",
        UUID.randomUUID(),
        BusinessType.FACTORY,
        PlayerStatus.CONNECTED_AND_ACTIVE,
        "123.123.123.123",
        1233));
    verify(messenger).sendPlayerListUpdate(any());
  }

  @Test
  void playerUpdate_Should_NotBeCalled_When_CalledWithANull() {

    leaderFailureDetection.stop();

    leaderFailureDetection.sendPlayerUpdate(null);
    verify(messenger, never()).sendPlayerListUpdate(any());
  }

  @Test
  void getPlayersWithStatuses_should_returnOne_when_PlayerStatusIsConnectedAndActive_and_PlayerLobbyDoesntGetAnyHeartbeatsAfterTheTimeoutHasNotBeenSurpassed()
      throws UnknownGameException {

    playerLobby.resetPlayers(Sets.newSet(ryanPlayer));

    assertEquals(1, playerLobby.getPlayersWithStatuses(PlayerStatus.CONNECTED_AND_ACTIVE).size());

    await().atMost(1500, TimeUnit.MILLISECONDS).until(() ->
        playerLobby.getPlayersWithStatuses(PlayerStatus.CONNECTED_AND_ACTIVE).size() == 1);
  }

  @Test
  void getPlayersWithConnectedAndPlayingStatusSize_shouldReturnOne_when_PlayerLobbyDoesntGetAnyHeartbeatsAfterTheTimeoutHasBeenSurpassedButHasBeenRecentlyRefreshedBeforeASecondTimeoutHasOccurred()
      throws PlayerNotFoundException, UnknownGameException {

    playerLobby.resetPlayers(Sets.newSet(ryanPlayer));

    assertEquals(1, playerLobby.getPlayersWithStatuses(PlayerStatus.CONNECTED_AND_ACTIVE).size());

    await().atLeast(1, TimeUnit.SECONDS);

    playerLobby.refreshPlayerTimestamp(ryanPlayer, leaderFailureDetection);

    await().atMost(1500, TimeUnit.MILLISECONDS).until(() ->
        playerLobby.getPlayersWithStatuses(PlayerStatus.CONNECTED_AND_ACTIVE).size() == 1);
  }

  @Test
  void failureDetection_shouldNotChangeThePlayerLobby_whenItHasStopped()
      throws UnknownGameException {

    playerLobby.resetPlayers(Sets.newSet(ryanPlayer));

    assertEquals(1, playerLobby.getPlayersWithStatuses(PlayerStatus.CONNECTED_AND_ACTIVE).size());

    await().atLeast(1, TimeUnit.SECONDS);

    leaderFailureDetection.stop();

    await().atMost(1500, TimeUnit.MILLISECONDS).until(() ->
        playerLobby.getPlayersWithStatuses(PlayerStatus.CONNECTED_AND_ACTIVE).size() == 1);
  }

  @AfterEach
  void afterEach() {
    leaderFailureDetection.stop();
  }
}