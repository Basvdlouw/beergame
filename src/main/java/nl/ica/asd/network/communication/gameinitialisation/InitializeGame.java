package nl.ica.asd.network.communication.gameinitialisation;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import nl.ica.asd.agenthandler.AgentHandler;
import nl.ica.asd.frontend.gui.service.GuiService;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.network.communication.clientserver.IncomingDataHandler;
import nl.ica.asd.network.communication.clientserver.Server;
import nl.ica.asd.network.communication.interfaces.GameCreator;
import nl.ica.asd.network.communication.interfaces.PublicIPAddress;
import nl.ica.asd.network.communication.twophasecommit.LogController;
import nl.ica.asd.network.communication.twophasecommit.OutgoingDataController;
import nl.ica.asd.network.failurehandler.HeartbeatHandler;
import nl.ica.asd.network.playerlobby.PlayerLobby;
import nl.ica.asd.network.playerlobby.exceptions.UsernameAlreadyInUseException;
import nl.ica.asd.storage.applicationdatastorage.ApplicationDataAccess;
import nl.ica.asd.storage.domain.GameData;
import nl.ica.asd.storage.exception.UnknownGameDataException;
import nl.ica.asd.storage.exception.UnknownGameException;
import nl.ica.asd.storage.gameconfig.GameConfig;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class InitializeGame implements GameCreator {

  @Inject
  private AgentHandler agentHandler;

  @Inject
  private PlayerLobby playerLobby;

  @Inject

  private IncomingDataHandler incomingDataHandler;

  @Inject
  private PublicIPAddress publicIPAddress;

  @Inject
  private ApplicationDataAccess applicationDataAccess;

  @Inject
  private GameConfig gameConfig;

  @Inject
  private HeartbeatHandler heartbeatHandler;

  @Inject
  private OutgoingDataController outgoingDataController;

  @Inject
  private GuiService guiService;

  private Server server;

  private boolean connectionStarted = false;

  private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Server.class);


  @Inject
  public InitializeGame(IncomingDataHandler incomingDataHandler) {
    this.incomingDataHandler = incomingDataHandler;
  }

  @Override
  public void createGame() throws UnknownGameException, IOException {
    createServer();
    updatePlayerLobby(playerLobby.getPlayerForSelf());
    startServer();
    heartbeatHandler.startSendingHeartbeats();
  }

  @Override
  public void joinLobby(Player self, Set<Player> players, GameData gameData,
      ConnectionResult connectionResult) {
    createServer();

    if (connectionStarted) {
      return;
    }

    if (connectionResult == ConnectionResult.SUCCESS) {
      connectionStarted = true;
      startServer();
      playerLobby.setPlayerForSelf(self);
      try {
        gameConfig.setGameData(gameData);
        playerLobby.resetPlayers(players);
      } catch (UnknownGameException exc) {
        // Since "joinLobby()" only gets called when the connection attempt was successful, this case should never occur
        logger.error("The game appears to be unknown, this should be impossible and is a bug.");
      } catch (UnknownGameDataException exc) {
        // Since "joinLobby()" only gets called with gameData which has been sent by the game initiator (which means it's already verified to be valid), this case should never occur
        logger.error("The gamedata appears to be invalid, this should be impossible and is a bug.");
      }
    }
    guiService.updateJoinIsConnected(connectionResult);
  }

  public void stopServer() {
    try {
      connectionStarted = false;
      server.shutdown();
      server.awaitShutdown();
    } catch (IOException ex) {
      logger.warn(String.format("The server thread didn't want to close properly, forcing it. %s",
          ex.toString()));
      server.interrupt();
    }
  }

  private void startServer() {
    server.start();
    server.awaitInitialization();
  }

  private void createServer() {
    try {
      try (InputStream propFile = new FileInputStream("config.properties")) {
        Properties properties = new Properties();
        properties.load(propFile);
        server = new Server(properties.getProperty("listen_address"), Server.DEFAULT_PORT,
            incomingDataHandler);
      }
    } catch (IOException exc) {
      logger.error("Something went wrong reading the properties file, please make sure it exists", exc);
    }
  }

  private void updatePlayerLobby(Player player) throws UnknownGameException {
    try {
      playerLobby.playerConnected(player);
    } catch (UsernameAlreadyInUseException e) {
      logger.error(
          "It seems the username is already in use, this is impossible and is a bug, please report it to the developer");
    }
  }

  public void reset() {
    server = null;
    playerLobby.reset();
    outgoingDataController.reset();
    heartbeatHandler.reset();
    agentHandler.reset();
  }
}