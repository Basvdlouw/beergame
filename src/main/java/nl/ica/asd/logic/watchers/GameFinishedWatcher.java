package nl.ica.asd.logic.watchers;

import nl.ica.asd.logic.watchers.events.GameFinishedEvent;

public interface GameFinishedWatcher extends Watcher {

  void onGameFinished(GameFinishedEvent event);
}
