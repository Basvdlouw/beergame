package nl.ica.asd.logic.gamemanager.playermanager;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashSet;
import java.util.Set;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.logic.watchers.PlayerListChangedWatcher;
import nl.ica.asd.logic.watchers.Watcher;
import nl.ica.asd.logic.watchers.events.Event;
import nl.ica.asd.logic.watchers.events.PlayerListChangedEvent;
import nl.ica.asd.storage.exception.UnknownGameException;
import nl.ica.asd.storage.gamedatastorage.GameDataAccess;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DefaultPlayerManagerTest {

  @Mock
  private GameDataAccess gameDataAccess;

  @InjectMocks
  private DefaultPlayerManager playerManager;

//  @Test
//  void setPlayerList_should_callGameDataAccess() throws UnknownGameException {
//    final Set<Player> players = new HashSet<>();
//    doThrow(UnknownGameException.class).when(gameDataAccess).savePlayerList(players);
//    playerManager.setPlayerList(players);
//    verify(gameDataAccess, times(1)).savePlayerList(players);
//  }

  @Test
  void isOwnWatcher_shouldReturnTrue_when_watcherIsPlayerListChangedWatcher() {
    Watcher watcher = new MockPlayerWatcher();
    assertTrue(playerManager.isOwnWatcher(watcher));
  }

  private class MockPlayerWatcher implements PlayerListChangedWatcher {

    @Override
    public void onPlayerListChanged(PlayerListChangedEvent event) {
    }

    @Override
    public void subscribeAll() {
    }

    @Override
    public void unSubscribeAll() {
    }

    @Override
    public <E extends Event> void onEvent(E event) {
    }
  }
}
