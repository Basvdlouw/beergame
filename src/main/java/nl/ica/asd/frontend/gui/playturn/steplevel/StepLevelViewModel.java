package nl.ica.asd.frontend.gui.playturn.steplevel;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public class StepLevelViewModel {

  private final ObjectProperty<StepLevel> stepProperty = new SimpleObjectProperty<>();

  private final IntegerProperty stepValueProperty = new SimpleIntegerProperty();

  public IntegerProperty getStepValueProperty() {
    return stepValueProperty;
  }

  public ObjectProperty getStepProperty() {
    return stepProperty;
  }

  public void setStepValue(int stepValue) {
    this.stepValueProperty.set(stepValue);
  }

  public int getStepValue() {
    return stepValueProperty.get();
  }

  public void setStep(StepLevel step) {
    this.stepProperty.set(step);
  }

  public StepLevel getStep() {
    return stepProperty.get();
  }
}
