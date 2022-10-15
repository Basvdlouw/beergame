package nl.ica.asd.network.communication.twophasecommit;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import nl.ica.asd.agenthandler.AgentHandler;
import nl.ica.asd.agenthandler.exceptions.AgentException;
import nl.ica.asd.logic.domain.GameState;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.logic.domain.actions.OrderAction;
import nl.ica.asd.logic.gamemanager.GameManager;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.LogEntry;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.logentrydatatypes.BusinessUpdate;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.logentrydatatypes.GameStateUpdate;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.logentrydatatypes.OrderUpdate;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.logentrydatatypes.PlayerUpdate;
import nl.ica.asd.network.communication.gameinitialisation.InitializeGame;
import nl.ica.asd.network.playerlobby.ConnectedPlayers;
import nl.ica.asd.network.playerlobby.exceptions.PlayerNotFoundException;
import nl.ica.asd.network.playerlobby.exceptions.UsernameAlreadyInUseException;
import nl.ica.asd.storage.exception.UnknownGameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class LogEntryProcessor {

  @Named("PlayerLobby")
  private final ConnectedPlayers connectedPlayers;

  @Named("GameManager")
  private final GameManager gameManager;

  private final InitializeGame initializeGame;

  private final AgentHandler agentHandler;

  private static final Logger logger = LoggerFactory.getLogger(LogEntryProcessor.class);

  @Inject
  public LogEntryProcessor(ConnectedPlayers connectedPlayers, GameManager gameManager,
      AgentHandler agentHandler, InitializeGame initializeGame) {
    this.connectedPlayers = connectedPlayers;
    this.gameManager = gameManager;
    this.agentHandler = agentHandler;
    this.initializeGame = initializeGame;
  }

  void update(LogEntry logEntry) throws UnknownGameException {
    if (logEntry instanceof PlayerUpdate) {
      updatePlayer((PlayerUpdate) logEntry);
    } else if (logEntry instanceof OrderUpdate) {
      updateOrders((OrderUpdate) logEntry);
    } else if (logEntry instanceof BusinessUpdate) {
      updateBusiness((BusinessUpdate) logEntry);
    } else if (logEntry instanceof GameStateUpdate) {
      updateGameState((GameStateUpdate) logEntry);
    } else {
      throw new IllegalStateException();
    }
  }

  void rollback(LogEntry logEntry) {
    throw new UnsupportedOperationException();
  }

  private void updatePlayer(PlayerUpdate playerUpdate) throws UnknownGameException {
    Player player = playerUpdate.getPlayer();
    try {
      connectedPlayers.updatePlayers(player);
    } catch (PlayerNotFoundException e) {
      try {
        connectedPlayers.playerConnected(player);
      } catch (UsernameAlreadyInUseException f) {
        logger.error(f.getMessage());
      }
    }
  }

  private void updateOrders(OrderUpdate orderUpdate) {
    List<OrderAction> orderactions = new ArrayList<>();
    orderactions.add(orderUpdate.getOrder());
    try {
      gameManager.saveCurrentOrderActions(orderactions);
    } catch (UnknownGameException e) {
      try {
        gameManager.saveCurrentOrderActions(orderactions);
      } catch (UnknownGameException e1) {
        logger.error(
            "!!!!!SEVERE ERROR, STATE CONSENSUS IS NOT GUARANTEED ANYMORE!!!!! Failed to save order twice after state consensus confirmed the change should be committed",
            e1);
      }
    }
  }

  private void updateBusiness(BusinessUpdate businessUpdate) {
    try {
      agentHandler.saveBusinessRules(businessUpdate.getBusinessRules());
    } catch (AgentException e) {
      try {
        agentHandler.saveBusinessRules(businessUpdate.getBusinessRules());
      } catch (AgentException e1) {
        logger.error(
            "!!!!!SEVERE ERROR, STATE CONSENSUS IS NOT GUARANTEED ANYMORE!!!!! Failed to save business rules twice after state consensus confirmed the change should be committed",
            e1);
      }
    }
  }

  private void updateGameState(GameStateUpdate gameStateUpdate) {
    gameManager.updateGameState(gameStateUpdate.getGameState());

    if (gameStateUpdate.getGameState() == GameState.FINISHED) {
      System.out.println("help");
      initializeGame.stopServer();
    }
  }

}
