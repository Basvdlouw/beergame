package nl.ica.asd.logic.watchers;

import nl.ica.asd.logic.watchers.events.Event;

public interface Watcher {

  void subscribeAll();

  void unSubscribeAll();

  <E extends Event> void onEvent(E event);
}
