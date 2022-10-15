package nl.ica.asd.logic.gamemanager.gamestatemanager;

import nl.ica.asd.frontend.gui.service.dto.PlayTurnState;
import nl.ica.asd.logic.domain.GameState;

public interface GameStateManager {

  PlayTurnState getPlayTurnStateForRound(int round);

  void updateGameState(GameState gameState);
}
