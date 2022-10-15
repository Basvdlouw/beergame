package nl.ica.asd.frontend.gui.service;

import java.util.Observable;
import java.util.Observer;
import javafx.application.Platform;

public abstract class ThreadRipperObserver implements Observer {

  @Override
  public void update(Observable observable, Object argument) {
    Platform.runLater(() -> synchronizedUpdate(observable, argument));
  }

  protected abstract void synchronizedUpdate(Observable observable, Object argument);
}
