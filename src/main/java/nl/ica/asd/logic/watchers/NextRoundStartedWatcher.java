package nl.ica.asd.logic.watchers;

import nl.ica.asd.logic.watchers.events.NextRoundStartedEvent;

public interface NextRoundStartedWatcher extends Watcher {

  void onNextRoundStarted(NextRoundStartedEvent event);
}
