package nl.ica.asd.logic.watchers;

import nl.ica.asd.logic.watchers.events.BusinessrulesChangedEvent;

public interface BusinessrulesChangedWatcher extends Watcher {

  void onBusinessrulesChanged(BusinessrulesChangedEvent event);
}
