package nl.ica.asd.logic.gamemanager;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import nl.ica.asd.frontend.gui.service.dto.PlayTurnState;
import nl.ica.asd.logic.domain.Action;
import nl.ica.asd.logic.domain.GameState;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.logic.gamemanager.gamestatemanager.GameStateManagerFactory;
import nl.ica.asd.logic.gamemanager.ordermanager.OrderManager;
import nl.ica.asd.logic.gamemanager.playermanager.PlayerManager;
import nl.ica.asd.storage.exception.UnknownGameException;
import nl.ica.asd.storage.gamedatastorage.GameDataAccess;

@Named("GameManager")
@ApplicationScoped
public class GameManagerHandler implements GameManager {

  @Inject
  private GameStateManagerFactory gameStateManagerFactory;
  @Inject
  private OrderManager orderManager;
  @Inject
  private PlayerManager playerManager;
  @Inject
  private GameDataAccess gameDataAccess;

  @Override
  public void placeOrder(String username, int amount) {
    orderManager.placeOrder(username, amount);
  }

  @Override
  public void saveCurrentOrderActions(List<? extends Action> actions) throws UnknownGameException {
    orderManager.saveCurrentOrderActions(actions);
  }

  @Override
  public Map<Integer, List<Action>> getAllActionsBusiness(UUID uuid) {
    return orderManager.getAllActionsBusiness(uuid);
  }

  @Override
  public void saveActions(Map<Integer, List<Action>> businessActionsMap) {
    orderManager.saveActions(businessActionsMap);
  }

  @Override
  public void setPlayerList(Set<Player> players) {
    playerManager.setPlayerList(players);
  }

  @Override
  public void updateGameState(@NotNull GameState gameState) {
    gameStateManagerFactory.getGameStateManager(gameState).updateGameState(gameState);
  }

  @Override
  public PlayTurnState getPlayTurnStateForRound(int round) {
    try {
      return getPlayTurnStateForRound(gameDataAccess.getBeerGame().getGameState(), round);
    }
    catch (UnknownGameException exception) {
      return null;
    }
  }

  private PlayTurnState getPlayTurnStateForRound(@NotNull GameState gameState, int round) {
    return gameStateManagerFactory.getGameStateManager(gameState).getPlayTurnStateForRound(round);
  }
}
