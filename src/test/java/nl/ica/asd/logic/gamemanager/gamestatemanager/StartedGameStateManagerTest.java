package nl.ica.asd.logic.gamemanager.gamestatemanager;

import static org.junit.jupiter.api.Assertions.assertTrue;

import nl.ica.asd.logic.watchers.NextRoundStartedWatcher;
import nl.ica.asd.logic.watchers.Watcher;
import nl.ica.asd.logic.watchers.events.Event;
import nl.ica.asd.logic.watchers.events.NextRoundStartedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StartedGameStateManagerTest {

  @InjectMocks
  private StartedGameStateManager startedGameStateManager;

  @Test
  void isOwnWatcher_shouldReturnTrue_when_watcherIsGameFinishedWatcher() {
    Watcher watcher = new MockWatcher();
    assertTrue(startedGameStateManager.isOwnWatcher(watcher));
  }

  private class MockWatcher implements NextRoundStartedWatcher {

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
    public void onNextRoundStarted(NextRoundStartedEvent event) {

    }
  }
}
