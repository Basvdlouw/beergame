package nl.ica.asd.frontend.gui.evaluate.gameselect;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class EvaluateGameSelectViewModel {

  private BooleanProperty gameSelected = new SimpleBooleanProperty(true);

  public BooleanProperty getGameSelectedProperty() {
    return gameSelected;
  }

  public void setGameSelectedProperty(boolean value) {
    setBooleanProperty(gameSelected, value);
  }

  private void setBooleanProperty(BooleanProperty booleanProperty, boolean value) {
    if (booleanProperty.get() != value) {
      booleanProperty.set(value);
    }
  }
}
