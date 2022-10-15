package nl.ica.asd.logic.gamemanager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import javafx.application.Platform;
import javax.inject.Inject;
import nl.ica.asd.agenthandler.AgentHandler;
import nl.ica.asd.logic.watchers.Watcher;
import nl.ica.asd.logic.watchers.events.Event;
import nl.ica.asd.network.communication.interfaces.SendGameStateUpdate;
import nl.ica.asd.storage.gamedatastorage.GameDataAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DefaultGameManager {

  private static final Logger logger = LoggerFactory.getLogger(DefaultGameManager.class);

  protected static final String BEERGAME_NOT_FOUND_MSG = "Beer game could not be found: %s";
  protected static final List<Watcher> watchers = new ArrayList<>();

  @Inject
  protected GameDataAccess gameDataAccess;
  @Inject
  protected AgentHandler agentHandler;
  @Inject
  protected SendGameStateUpdate sendGameStateUpdate;

  protected abstract boolean isOwnWatcher(Watcher watcher);

  public  <E extends Event> void onEvent(E event) {
    Platform.runLater(() ->new ArrayList<>(DefaultGameManager.watchers).stream()
        .filter(this::isOwnWatcher)
        .forEach(watcher -> watcher.onEvent(event)));
  }

  public static void subscribe(Watcher watcher) {
    DefaultGameManager.watchers.add(watcher);
  }

  public static <W extends Watcher> void unSubscribe(W watcher) {
    DefaultGameManager.watchers.remove(watcher);
  }

  protected static <T> Optional<T> withCallable(Callable<T> callable) {
    try {
      return Optional.of(callable.call());
    } catch (Exception e) {
      logger.error("GameManager error: ", e);
    }
    return Optional.empty();
  }
}
