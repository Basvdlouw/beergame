package nl.ica.asd.network.playerlobby;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import nl.ica.asd.frontend.gui.service.GuiService;
import nl.ica.asd.logic.domain.BusinessType;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.logic.domain.PlayerStatus;
import nl.ica.asd.logic.domain.builders.PlayerBuilder;
import nl.ica.asd.logic.gamemanager.GameManager;
import nl.ica.asd.network.communication.gameinitialisation.DefaultIPAddressProvider;
import nl.ica.asd.network.failurehandler.leader.LeaderFailureDetection;
import nl.ica.asd.network.playerlobby.exceptions.PlayerNotFoundException;
import nl.ica.asd.network.playerlobby.exceptions.UsernameAlreadyInUseException;
import nl.ica.asd.storage.applicationdatastorage.ApplicationDataAccess;
import nl.ica.asd.storage.exception.UnknownException;
import nl.ica.asd.storage.exception.UnknownGameException;
import nl.ica.asd.storage.gamedatastorage.GameDataAccess;
import nl.ica.asd.util.TestHelpers;
import nl.ica.asd.util.TryAgain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

class PlayerLobbyTest {

  private GameManager mockedManager;
  private GameDataAccess mockedGameDataAccess;
  private LeaderFailureDetection mockedLeaderFailureDetection;
  private ApplicationDataAccess mockedApplicationDataAccess;
  private DefaultIPAddressProvider mockedDefaultIPAddressProvider;
  private GuiService mockedGuiService;
  private PlayerLobby playerLobby;
  private Player ryanPlayer;

  @BeforeEach
  void setUp() throws UnknownGameException {
    mockedManager = mock(GameManager.class);
    mockedGameDataAccess = mock(GameDataAccess.class);
    mockedLeaderFailureDetection = mock(LeaderFailureDetection.class);
    mockedApplicationDataAccess = mock(ApplicationDataAccess.class);
    mockedDefaultIPAddressProvider = mock(DefaultIPAddressProvider.class);
    mockedGuiService = mock(GuiService.class);

    when(mockedGameDataAccess.getBeerGame()).thenReturn(TestHelpers.createTestingBeerGame());

    playerLobby = new PlayerLobby(mockedManager,
        mockedGameDataAccess,
        mockedApplicationDataAccess,
        mockedDefaultIPAddressProvider,
        mockedGuiService);

    ryanPlayer = PlayerBuilder.aPlayer().withUsername("ryanPlayer").withIp("oldIp")
        .withPlayerStatus(
            PlayerStatus.CONNECTED_AND_ACTIVE).build();
  }

  @Test
  @ExtendWith(TryAgain.class)
  void testConnectPlayer() throws UsernameAlreadyInUseException, UnknownGameException {
    playerLobby.playerConnected(PlayerBuilder.aPlayer().build());
    assertEquals(1, playerLobby.getAllPlayers().size());
  }

  @Test
  @ExtendWith(TryAgain.class)
  void testConnectDuplicatePlayer() throws UsernameAlreadyInUseException, UnknownGameException {

    playerLobby.playerConnected(ryanPlayer);

    assertThrows(UsernameAlreadyInUseException.class, () -> {
      Player ryanPlayerCopy = PlayerBuilder.fromBasePlayer(ryanPlayer).withIp("newIp").build();
      playerLobby.playerConnected(ryanPlayerCopy);
    });

    assertEquals(1, playerLobby.getAllPlayers().size());
  }

  @Test
  @ExtendWith(TryAgain.class)
  void testUpdateUnconnectedPlayer() throws UsernameAlreadyInUseException {

    assertThrows(PlayerNotFoundException.class, () -> {
      Player ryanPlayerCopy = PlayerBuilder.fromBasePlayer(ryanPlayer).withIp("newIp").build();
      playerLobby.updatePlayers(ryanPlayerCopy);
    });

    assertEquals(0, playerLobby.getAllPlayers().size());
  }

  @Test
  @ExtendWith(TryAgain.class)
  void testGetPlayersWithStatuses() throws UsernameAlreadyInUseException, UnknownException {

    Player ryanPlayer = PlayerBuilder.aPlayer().withUsername("ryanPlayer").withIp("oldIp")
        .withPlayerStatus(
            PlayerStatus.CONNECTED_AND_ACTIVE).build();

    when(mockedGameDataAccess.getBusinessType(any(UUID.class)))
        .thenReturn(BusinessType.FACTORY);

    playerLobby.playerConnected(ryanPlayer);

    assertEquals(1, playerLobby.getPlayersWhoAreConnected().size());
  }

  @Test
  @ExtendWith(TryAgain.class)
  void testResetPlayers() throws UsernameAlreadyInUseException, UnknownGameException {
    when(mockedApplicationDataAccess.getUsername()).thenReturn("delina");

    playerLobby.playerConnected(ryanPlayer);

    Player delinaPlayer = PlayerBuilder.aPlayer().withIp("ip").withUsername("delina")
        .withPlayerStatus(PlayerStatus.AGENT_ACTIVATED).build();

    Set<Player> newPlayers = new TreeSet<>();
    newPlayers.add(delinaPlayer);
    playerLobby.resetPlayers(newPlayers);

    assertEquals(1, playerLobby.getPlayersWhoNeedToBePlayedByAgents().size());
  }

  @Test
  @ExtendWith(TryAgain.class)
  void testOccupiedBusinesses() throws UsernameAlreadyInUseException, UnknownException {

    UUID uuid = UUID.randomUUID();

    Player ryanPlayer = PlayerBuilder.aPlayer().withUsername("ryanPlayer").withIp("oldIp")
        .withBusinessUUID(uuid).withPlayerStatus(
            PlayerStatus.CONNECTED_AND_ACTIVE).build();

    when(mockedGameDataAccess.getBusinessType(any(UUID.class)))
        .thenReturn(BusinessType.FACTORY);

    playerLobby.playerConnected(ryanPlayer);

    assertEquals(1, playerLobby.getOccupiedBusinesses().size());
    assertEquals(uuid, playerLobby.getOccupiedBusinesses().iterator().next());
  }

  @Test
  @ExtendWith(TryAgain.class)
  void testTimestamps()
      throws UsernameAlreadyInUseException, InterruptedException, UnknownGameException {

    playerLobby.playerConnected(ryanPlayer);

    await().atLeast(100, TimeUnit.MILLISECONDS);
    assertEquals(1, playerLobby.getPlayersRefreshedWithinLast(305).size());
  }

  @Test
  @ExtendWith(TryAgain.class)
  void testExpiredTimestamp()
      throws UsernameAlreadyInUseException, InterruptedException, UnknownGameException {

    playerLobby.playerConnected(ryanPlayer);
    Thread.sleep(210);

    assertEquals(0, playerLobby.getPlayersRefreshedWithinLast(205).size());
  }

  @Test
  @ExtendWith(TryAgain.class)
  void testRefreshedTimestamp()
      throws UsernameAlreadyInUseException, InterruptedException, PlayerNotFoundException, UnknownGameException {

    playerLobby.playerConnected(ryanPlayer);
    await().atLeast(210, TimeUnit.MILLISECONDS);
    playerLobby.refreshPlayerTimestamp(ryanPlayer, mockedLeaderFailureDetection);

    assertEquals(1, playerLobby.getPlayersRefreshedWithinLast(205).size());
  }

  @Test
  @ExtendWith(TryAgain.class)
  void testUpdatePlayer()
      throws UsernameAlreadyInUseException, InterruptedException, PlayerNotFoundException, UnknownGameException {

    Player ryanPlayer = PlayerBuilder.aPlayer().withUsername("ryanPlayer").withIp("oldIp")
        .withPlayerStatus(
            PlayerStatus.CONNECTED_AND_ACTIVE).build();

    playerLobby.playerConnected(ryanPlayer);
    Thread.sleep(210);
    Player newRyan = PlayerBuilder.fromBasePlayer(ryanPlayer).withIp("brandNewIP").build();
    playerLobby.updatePlayers(newRyan);

    //Test that updateDoesn't auto update the timestamp
    assertEquals(0, playerLobby.getPlayersRefreshedWithinLast(205).size());
    assertEquals(1, playerLobby.getAllPlayers().size());
    assertEquals("brandNewIP", playerLobby.getAllPlayers().iterator().next().getIP());
  }

  @Test
  @ExtendWith(TryAgain.class)
  void allPlayersHaveABusiness_should_returnFalse_when_notAllPlayersHaveABusiness()
      throws UsernameAlreadyInUseException, UnknownException {
    when(mockedGameDataAccess.getBusinessType(any(UUID.class)))
        .thenReturn(BusinessType.FACTORY);
    PlayerLobby playerLobby = new PlayerLobby(mockedManager, mockedGameDataAccess,
        mockedApplicationDataAccess, mockedDefaultIPAddressProvider, mockedGuiService);
    Player player1 = new Player("player1", UUID.randomUUID(), BusinessType.FACTORY,
        PlayerStatus.CONNECTED_AND_ACTIVE,
        "123", 1);
    // TODO  BusinessType.NOT_SET
    Player player2 = new Player("player2", null, BusinessType.NOT_SET,
        PlayerStatus.CONNECTED_AND_ACTIVE, "1233", 1);
    playerLobby.playerConnected(player1);
    playerLobby.playerConnected(player2);
    assertFalse(playerLobby.allPlayersHaveABusiness());
  }

  @Test
  void refreshPlayerTimestamp_should_callSendPlayerListUpdate_when_statusDiffersFromOneSavedInPlayerLobby()
      throws UsernameAlreadyInUseException, PlayerNotFoundException, UnknownException {

    Player player1 = new Player("player1", UUID.randomUUID(),
        BusinessType.FACTORY,
        PlayerStatus.LEFT_BY_ACCIDENT,
        "123", 1);

    when(mockedGameDataAccess.getBusinessType(any(UUID.class)))
        .thenReturn(BusinessType.FACTORY);

    playerLobby.playerConnected(player1);

    Player player1WithDifferentStatus = new Player("player1", UUID.randomUUID(),
        BusinessType.FACTORY,
        PlayerStatus.CONNECTED_AND_ACTIVE,
        "123", 1);

    playerLobby.refreshPlayerTimestamp(player1WithDifferentStatus, mockedLeaderFailureDetection);

    verify(mockedLeaderFailureDetection, times(1)).sendPlayerUpdate(player1WithDifferentStatus);
  }

  @Test
  void refreshPlayerTimestamp_should_NotCallSendPlayerListUpdate_when_statusMatchesOneSavedInPlayerLobby()
      throws UsernameAlreadyInUseException, PlayerNotFoundException, UnknownException {

    final Player player1 = new Player("player1", UUID.randomUUID(),
        BusinessType.FACTORY,
        PlayerStatus.CONNECTED_AND_ACTIVE,
        "123", 1);

    when(mockedGameDataAccess.getBusinessType(any(UUID.class)))
        .thenReturn(BusinessType.FACTORY);

    playerLobby.playerConnected(player1);
    playerLobby.refreshPlayerTimestamp(player1, mockedLeaderFailureDetection);

    verify(mockedLeaderFailureDetection, never()).sendPlayerUpdate(player1);
  }

  @Test
  void getPlayerForSelf_Should_returnSamePlayerAfterSet() throws IOException {

    // Create new player
    final Player player1 = new Player("player1", UUID.randomUUID(),
        BusinessType.FACTORY,
        PlayerStatus.CONNECTED_AND_ACTIVE,
        "123", 1);

    // Should set player above for self
    playerLobby.setPlayerForSelf(player1);

    // Should retrieve player set above
    final Player player = playerLobby.getPlayerForSelf();

    // Compares both players
    assertEquals(player.getUsername(), player1.getUsername());
  }

  @Test
  void getPlayersForSelf_Should_beAbleToThrowError_When_failed() throws IOException {

    // Mock lobby
    PlayerLobby lobby = mock(PlayerLobby.class);

    // Check if error gets thrown.
    doThrow(new IOException()).when(lobby).getPlayerForSelf();
    assertThrows(IOException.class, () -> lobby.getPlayerForSelf());
  }

  @Test
  void getPlayerForSelf_Should_createNewPlayers_When_noneIsPresent() throws IOException {

    // Check if player gets when none is present.
    final Player player = playerLobby.getPlayerForSelf();
    assertEquals(player.getUsername(), playerLobby.getPlayerForSelf().getUsername());
  }
}
