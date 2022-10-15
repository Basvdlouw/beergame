package nl.ica.asd.logic.gamemanager.gamestatemanager;

import static nl.ica.asd.util.MockitoHelpers.once;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import nl.ica.asd.agenthandler.exceptions.AgentException;
import nl.ica.asd.logic.watchers.GameFinishedWatcher;
import nl.ica.asd.logic.watchers.Watcher;
import nl.ica.asd.logic.watchers.events.Event;
import nl.ica.asd.logic.watchers.events.GameFinishedEvent;
import nl.ica.asd.storage.exception.UnknownBusinessException;
import nl.ica.asd.storage.exception.UnknownGameException;
import nl.ica.asd.storage.gamedatastorage.GameDataAccess;
import nl.ica.asd.util.TestHelpers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FinishedGameStateManagerTest {

  @Mock
  private GameDataAccess gameDataAccess;

  @InjectMocks
  private FinishedGameStateManager finishedGameStateManager;

//  @Test
//  void nextGameState_should_callGameDataAccessTrice()
//      throws AgentException, UnknownBusinessException, UnknownGameException {
//    when(gameDataAccess.getBeerGame()).thenReturn(TestHelpers.createTestingBeerGame());
//    finishedGameStateManager.nextGameState();
//    verify(gameDataAccess, once()).getBeerGame();
//    verify(gameDataAccess, once()).saveBeerGameSettings(any());
//    verify(gameDataAccess, once()).setCurrentGame(any());
//  }

  @Test
  void nextGameState_should_callThrowException_when_gameNotSet()
      throws UnknownGameException, AgentException, UnknownBusinessException {
    when(gameDataAccess.getBeerGame()).thenThrow(UnknownGameException.class);
    finishedGameStateManager.nextGameState();
    verify(gameDataAccess, once()).getBeerGame();
    verify(gameDataAccess, never()).saveBeerGameSettings(any());
    verify(gameDataAccess, never()).setCurrentGame(any());
  }

  @Test
  void isOwnWatcher_shouldReturnTrue_when_watcherIsGameFinishedWatcher() {
    Watcher watcher = new MockWatcher();
    assertTrue(finishedGameStateManager.isOwnWatcher(watcher));
  }

  private class MockWatcher implements GameFinishedWatcher {

    @Override
    public void subscribeAll() {
    }

    @Override
    public void unSubscribeAll() {
    }

    @Override
    public <E extends Event> void onEvent(E event) {
    }

    @Override
    public void onGameFinished(GameFinishedEvent event) {

    }
  }
}