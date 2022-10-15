package nl.ica.asd.logic.watchers;

import nl.ica.asd.logic.watchers.events.PlayerListChangedEvent;

public interface PlayerListChangedWatcher extends Watcher {

  void onPlayerListChanged(PlayerListChangedEvent event);
}
