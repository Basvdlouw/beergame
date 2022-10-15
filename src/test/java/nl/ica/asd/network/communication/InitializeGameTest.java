package nl.ica.asd.network.communication;

import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import nl.ica.asd.frontend.gui.service.GuiService;
import nl.ica.asd.logic.domain.BusinessType;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.logic.domain.PlayerStatus;
import nl.ica.asd.network.communication.clientserver.Server;
import nl.ica.asd.network.communication.gameinitialisation.ConnectionResult;
import nl.ica.asd.network.communication.gameinitialisation.InitializeGame;
import nl.ica.asd.network.communication.interfaces.PublicIPAddress;
import nl.ica.asd.network.failurehandler.HeartbeatHandler;
import nl.ica.asd.network.playerlobby.PlayerLobby;
import nl.ica.asd.storage.applicationdatastorage.ApplicationDataAccess;
import nl.ica.asd.storage.exception.UnknownGameDataException;
import nl.ica.asd.storage.exception.UnknownGameException;
import nl.ica.asd.storage.gameconfig.GameConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InitializeGameTest {

  @Mock
  private ApplicationDataAccess applicationDataAccess;

  @Mock
  private PublicIPAddress publicIPAddress;

  @Mock
  private Server server;

  @Mock
  private PlayerLobby playerLobby;

  @Mock
  private GameConfig gameConfig;

  @Mock
  private HeartbeatHandler heartbeatHandler;

  @Mock
  private GuiService guiService;

  @InjectMocks
  private InitializeGame initializeGame;

  @BeforeEach
  void setup() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  void joinLobby_should_invokeMocks_when_Called()
      throws UnknownGameDataException, UnknownGameException {
    final Player player = new Player("test", UUID.randomUUID(), BusinessType.FACTORY,
        PlayerStatus.CONNECTED_AND_ACTIVE, "ip", 8080);
    final Set<Player> players = new HashSet<>();
    initializeGame.joinLobby(player, players, null, ConnectionResult.SUCCESS);
    verify(playerLobby).resetPlayers(players);
    verify(playerLobby).setPlayerForSelf(player);
    verify(guiService).updateJoinIsConnected(ConnectionResult.SUCCESS);
    verify(gameConfig).setGameData(null);
    verify(server).start();
  }

  @Test
  void createGame_should_startServer()
      throws IOException, UnknownGameException {
    initializeGame.createGame();

    verify(server).start();
  }

  @Test
  void stopServer_should_stopServer() throws UnknownGameException, IOException {
    initializeGame.reset();

    verify(server).shutdown();
  }
}