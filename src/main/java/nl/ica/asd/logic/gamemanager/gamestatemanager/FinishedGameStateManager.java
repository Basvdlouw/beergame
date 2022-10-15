package nl.ica.asd.logic.gamemanager.gamestatemanager;

import javax.inject.Inject;
import javax.inject.Named;
import nl.ica.asd.agenthandler.exceptions.AgentException;
import nl.ica.asd.logic.domain.BeerGame;
import nl.ica.asd.logic.domain.GameState;
import nl.ica.asd.logic.domain.builders.BeerGameBuilder;
import nl.ica.asd.logic.watchers.GameFinishedWatcher;
import nl.ica.asd.logic.watchers.Watcher;
import nl.ica.asd.logic.watchers.events.GameFinishedEvent;
import nl.ica.asd.network.communication.gameinitialisation.InitializeGame;
import nl.ica.asd.storage.exception.UnknownBusinessException;
import nl.ica.asd.storage.exception.UnknownGameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("FinishedManager")
public class FinishedGameStateManager extends DefaultGameStateManager {

  private static final Logger logger = LoggerFactory.getLogger(FinishedGameStateManager.class);

  @Inject
  private InitializeGame initializeGame;

  @Override
  protected void nextGameState()
      throws UnknownGameException, AgentException, UnknownBusinessException {
    try {
      final BeerGame beerGame = BeerGameBuilder.fromBaseBeerGame(gameDataAccess.getBeerGame())
          .withGameState(GameState.FINISHED).build();
      gameDataAccess.saveBeerGameSettings(beerGame);
      gameDataAccess.setCurrentGame(beerGame);
      onEvent(new GameFinishedEvent(beerGame));
    } catch (UnknownGameException e) {
      logger.warn("Game finished while no game has been set!", e);
    }
  }

  @Override
  protected boolean isOwnWatcher(Watcher watcher) {
    return watcher instanceof GameFinishedWatcher;
  }
}
