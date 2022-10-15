package nl.ica.asd.logic.watchers;

import nl.ica.asd.logic.watchers.events.OrderPlacedEvent;

public interface OrderPlacedWatcher extends Watcher {

  void onOrderPlaced(OrderPlacedEvent event);
}
