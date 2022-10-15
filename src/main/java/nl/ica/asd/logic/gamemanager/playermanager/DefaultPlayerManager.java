package nl.ica.asd.logic.gamemanager.playermanager;

import java.util.Set;
import javax.inject.Inject;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.logic.gamemanager.DefaultGameManager;
import nl.ica.asd.logic.gamemanager.ordermanager.OrderManager;
import nl.ica.asd.logic.watchers.PlayerListChangedWatcher;
import nl.ica.asd.logic.watchers.Watcher;
import nl.ica.asd.logic.watchers.events.PlayerListChangedEvent;
import nl.ica.asd.storage.exception.UnknownGameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultPlayerManager extends DefaultGameManager implements PlayerManager {

  private static final Logger logger = LoggerFactory.getLogger(DefaultPlayerManager.class);

  @Inject
  private OrderManager orderManager;

  @Override
  public void setPlayerList(Set<Player> players) {
    onEvent(new PlayerListChangedEvent(players));
    try {
      gameDataAccess.savePlayerList(players);
      orderManager.checkOrderProgress();
    } catch (UnknownGameException e) {
      logger.error(e.getMessage());
    }
  }

  protected boolean isOwnWatcher(Watcher watcher) {
    return watcher instanceof PlayerListChangedWatcher;
  }

}
