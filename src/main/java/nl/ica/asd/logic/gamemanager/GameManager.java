package nl.ica.asd.logic.gamemanager;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import nl.ica.asd.frontend.gui.service.dto.PlayTurnState;
import nl.ica.asd.logic.domain.Action;
import nl.ica.asd.logic.domain.GameState;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.storage.exception.UnknownGameException;

public interface GameManager {

  void placeOrder(String username, int amount);

  void saveCurrentOrderActions(List<? extends Action> actions) throws UnknownGameException;

  Map<Integer, List<Action>> getAllActionsBusiness(UUID uuid);

  void saveActions(Map<Integer, List<Action>> businessActionsMap);

  void setPlayerList(Set<Player> players);

  void updateGameState(GameState gameState);

  PlayTurnState getPlayTurnStateForRound(int round);

}
