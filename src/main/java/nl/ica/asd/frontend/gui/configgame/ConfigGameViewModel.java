package nl.ica.asd.frontend.gui.configgame;

import java.util.Arrays;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.SingleSelectionModel;
import nl.ica.asd.logic.domain.GameVisibilityType;
import nl.ica.asd.logic.domain.SupplyChainType;

public class ConfigGameViewModel {

  private StringProperty nameProperty = new SimpleStringProperty("");

  private StringProperty playerAmountProperty = new SimpleStringProperty("");

  private StringProperty turnAmountProperty = new SimpleStringProperty("");

  private StringProperty timeLimitProperty = new SimpleStringProperty("1");

  private StringProperty timeLimitErrorMessageProperty = new SimpleStringProperty("");

  private BooleanProperty timeLimitCheckedProperty = new SimpleBooleanProperty(false);

  private ObjectProperty<ObservableList> gameTypesProperty = new SimpleObjectProperty<>(
      FXCollections.observableList(
          Arrays.asList(SupplyChainType.values())));

  private ObjectProperty<SingleSelectionModel> selectedGameTypeProperty = new SimpleObjectProperty<>(
      null);

  private ObjectProperty<ObservableList> visibilitiesProperty = new SimpleObjectProperty<>(
      FXCollections.observableList(
          Arrays.asList(GameVisibilityType.values())));

  private ObjectProperty<SingleSelectionModel> selectedVisibilityProperty = new SimpleObjectProperty<>(
      null);

  public StringProperty getNameProperty() {
    return nameProperty;
  }

  public StringProperty getPlayerAmountProperty() {
    return playerAmountProperty;
  }

  public StringProperty getTurnAmountProperty() {
    return turnAmountProperty;
  }

  public StringProperty getTimeLimitProperty() {
    return timeLimitProperty;
  }

  public BooleanProperty getTimeLimitCheckedProperty() {
    return timeLimitCheckedProperty;
  }

  public ObjectProperty<ObservableList> getGameTypesProperty() {
    return gameTypesProperty;
  }

  public ObjectProperty<SingleSelectionModel> getSelectedGameTypeProperty() {
    return selectedGameTypeProperty;
  }

  public ObjectProperty<ObservableList> getVisibilitiesProperty() {
    return visibilitiesProperty;
  }

  public ObjectProperty<SingleSelectionModel> getSelectedVisibilityProperty() {
    return selectedVisibilityProperty;
  }

  public String getName() {
    return nameProperty.get();
  }

  public void setName(String name) {
    nameProperty.set(name);
  }

  public SupplyChainType getSelectedGameType() {
    return (SupplyChainType) selectedGameTypeProperty.get().getSelectedItem();
  }

  public void selectGameType(SupplyChainType gameType) {
    selectedGameTypeProperty.get().select(gameType);
  }

  public GameVisibilityType getSelectedVisibility() {
    return (GameVisibilityType) selectedVisibilityProperty.get().getSelectedItem();
  }

  public void selectVisibility(GameVisibilityType visibility) {
    selectedVisibilityProperty.get().select(visibility);
  }

  public int getPlayerAmount() {
    return Integer.parseInt(playerAmountProperty.getValue());
  }

  public void setPlayerAmount(String playerAmount) {
    playerAmountProperty.setValue(playerAmount);
  }

  public int getTurnAmount() {
    return Integer.parseInt(turnAmountProperty.getValue());
  }

  public void setTurnAmount(String turnAmount) {
    turnAmountProperty.setValue(turnAmount);
  }

  public boolean isTimeLimit() {
    return timeLimitCheckedProperty.get();
  }

  public int getTimeLimit() {
    try {
      return Integer.parseInt(timeLimitProperty.get());
    } catch (NumberFormatException exception) {
      return 0;
    }
  }

  public void setTimeLimit(int timeLimit) {
    timeLimitCheckedProperty.set(timeLimit != 0);
    timeLimitProperty.set(timeLimit + "");
  }

  public StringProperty getTimeLimitErrorMessageProperty() {
    return timeLimitErrorMessageProperty;
  }

  public void setTimeLimitErrorMessageProperty(String timeLimitErrorMessageProperty) {
    this.timeLimitErrorMessageProperty.set(timeLimitErrorMessageProperty);
  }
}