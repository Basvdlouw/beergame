package nl.ica.asd.logic.gamemanager.gamestatemanager;

import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import nl.ica.asd.agenthandler.exceptions.AgentException;
import nl.ica.asd.frontend.gui.service.dto.PlayTurnState;
import nl.ica.asd.frontend.gui.service.dto.PlayTurnState.PlayTurnLevel;
import nl.ica.asd.logic.domain.BeerGame;
import nl.ica.asd.logic.domain.Business;
import nl.ica.asd.logic.domain.GameState;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.logic.gamemanager.DefaultGameManager;
import nl.ica.asd.logic.gamemanager.ordermanager.OrderManager;
import nl.ica.asd.logic.statecalculator.BusinessState;
import nl.ica.asd.logic.statecalculator.StateCalculator;
import nl.ica.asd.network.playerlobby.PlayerLobby;
import nl.ica.asd.storage.domain.GameConfiguration;
import nl.ica.asd.storage.exception.UnknownBusinessException;
import nl.ica.asd.storage.exception.UnknownException;
import nl.ica.asd.storage.exception.UnknownGameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DefaultGameStateManager extends DefaultGameManager implements
    GameStateManager {

  private static final Logger logger = LoggerFactory.getLogger(DefaultGameStateManager.class);

  @Inject
  StateCalculator stateCalculator;
  @Inject
  OrderManager orderManager;
  @Inject
  protected PlayerLobby playerLobby;

  protected abstract void nextGameState()
      throws UnknownGameException, AgentException, UnknownBusinessException;

  @Override
  public void updateGameState(GameState gameState) {
    try {
      this.nextGameState();
    } catch (UnknownGameException | AgentException | UnknownBusinessException e) {
      logger.info(e.getMessage());
    }
  }

  public PlayTurnState getPlayTurnStateForRound(int round) {
    try {
      BeerGame beerGame = gameDataAccess.getBeerGame();
      return buildPlayTurnState(beerGame, round);
    }
    catch (UnknownGameException exception) {
      logger.error(exception.getMessage(), exception);
    }

    return null;
  }

  PlayTurnState getPlayTurnStateForCurrentRound() {
    try {
      BeerGame beerGame = gameDataAccess.getBeerGame();
      return buildPlayTurnState(beerGame, beerGame.getCurrentRound());
    }
    catch (UnknownGameException exception) {
      logger.error(exception.getMessage(), exception);
    }

    return null;
  }

  private PlayTurnState buildPlayTurnState(BeerGame beerGame, int round) {
    try {
      final GameConfiguration gameConfiguration = beerGame.getGameConfiguration();
      final Player player = playerLobby.getPlayerForSelf();
      final Business business = beerGame.getBusinesses().stream()
          .filter(tmpBusiness -> tmpBusiness.getPlayer() != null && tmpBusiness.getPlayer()
              .getUsername().equals(player.getUsername()))
          .findFirst()
          .orElseThrow(
              () -> new UnknownException("Username changed during game creation"));
      final PlayTurnLevel playTurnLevel = PlayTurnLevel.values()[
          player.getBusinessType().getIndexInChain() - 1];
      final BusinessState businessState = stateCalculator
          .getBusinessStateForBusiness(business.getUUID(), round);

      return new PlayTurnState(
          playTurnLevel, round, gameConfiguration.getMaxRounds(),
          gameDataAccess.getBeerGame().getGameConfiguration().getTimeLimitPerRound() * 60,
          businessState.getIncomingGoods(),
          businessState.getStock(), businessState.getOpenOrders(), businessState.getBudget(),
          businessState.getOutgoingGoods(), businessState.getIncomingOrders());
    } catch (IOException | UnknownException e) {
      logger.error("Player is not set yet!", e);
    }
    return null;
  }
}
