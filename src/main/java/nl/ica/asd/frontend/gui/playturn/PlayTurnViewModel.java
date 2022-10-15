package nl.ica.asd.frontend.gui.playturn;

import java.util.TimerTask;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import nl.ica.asd.logic.domain.Business;
import nl.ica.asd.logic.domain.businesses.FactoryBusiness;
import nl.ica.asd.logic.domain.businesses.RegionalWarehouseBusiness;
import nl.ica.asd.logic.domain.businesses.RetailBusiness;
import nl.ica.asd.logic.domain.businesses.WholesaleBusiness;

public class PlayTurnViewModel extends TimerTask {

  private static final String SUPPLIERKEY = "SUPPLIER";

  private static final String FACTORYKEY = "FACTORY";

  private static final String WAREHOUSEKEY = "WAREHOUSE";

  private static final String WHOLESALEKEY = "WHOLESALE";

  private static final String RETAILKEY = "RETAIL";

  private static final String DEMANDKEY = "DEMAND";

  private final IntegerProperty roundProperty = new SimpleIntegerProperty();

  private final IntegerProperty timerProperty = new SimpleIntegerProperty();

  private final StringProperty placeOrderStyleProperty = new SimpleStringProperty("");

  private final StringProperty senderLevelProperty = new SimpleStringProperty("");

  private final StringProperty playerLevelProperty = new SimpleStringProperty("");

  private final StringProperty toggleAgentProperty = new SimpleStringProperty("");

  private final StringProperty recipientLevelProperty = new SimpleStringProperty("");

  private final BooleanProperty waitingForOtherPlayersDisplayProperty = new SimpleBooleanProperty(
      false);

  private final BooleanProperty invalidOrderAmountDisplayProperty = new SimpleBooleanProperty(
      false);

  private final BooleanProperty placeOrderDisabledProperty = new SimpleBooleanProperty(false);

  private final StringProperty placeOrderProperty = new SimpleStringProperty("0");

  private static final String PLACE_ORDER_FIELD_ERROR_BORDER = "-fx-border-color: #FF0000";

  private Business business;

  public PlayTurnViewModel(Business business) {
    this.business = business;

    doPlayerAndNeighbourLevels(business);

    placeOrderProperty.addListener(this::placeOrderTextListener);
  }

  public Business getBusiness() {
    return business;
  }

  public IntegerProperty getRoundProperty() {
    return roundProperty;
  }

  public IntegerProperty getTimerProperty() {
    return timerProperty;
  }

  public void setTimer(int timer) {
    timerProperty.set(timer);
  }

  public int getTimer() {
    return timerProperty.get();
  }

  public StringProperty getPlaceOrderStyleProperty() {
    return placeOrderStyleProperty;
  }

  public BooleanProperty getWaitingForOtherPlayersDisplayProperty() {
    return waitingForOtherPlayersDisplayProperty;
  }

  public BooleanProperty getInvalidOrderAmountDisplayProperty() {
    return invalidOrderAmountDisplayProperty;
  }

  public BooleanProperty getPlaceOrderDisabledProperty() {
    return placeOrderDisabledProperty;
  }

  public StringProperty getPlaceOrderProperty() {
    return placeOrderProperty;
  }

  public boolean isPlaceOrderAmountValid() {
    return !invalidOrderAmountDisplayProperty.get();
  }

  public void setRound(int round) {
    roundProperty.set(round);
  }

  public int getRound() {
    return roundProperty.get();
  }

  public String getBusinessUsername() {
    return business.getPlayer().getUsername();
  }

  public int getPlaceOrderAmount() {
    return isPlaceOrderAmountValid() ? Integer.parseInt(placeOrderProperty.get()) : 0;
  }

  private void setBooleanProperty(BooleanProperty booleanProperty, boolean value) {
    if (booleanProperty.get() != value) {
      booleanProperty.set(value);
    }
  }

  public void doWaitForNextRound(boolean visible) {
    setBooleanProperty(placeOrderDisabledProperty, visible);
    setBooleanProperty(waitingForOtherPlayersDisplayProperty, visible);
  }

  public void doPlaceOrderErrorIndicator(boolean visible) {
    if (!placeOrderStyleProperty.get().contains(PLACE_ORDER_FIELD_ERROR_BORDER) && visible) {
      placeOrderStyleProperty.set(PLACE_ORDER_FIELD_ERROR_BORDER);
    } else if (!visible) {
      placeOrderStyleProperty.set("");
    }

    setBooleanProperty(invalidOrderAmountDisplayProperty, visible);
  }

  public StringProperty getSenderLevelProperty() {
    return senderLevelProperty;
  }

  public StringProperty getPlayerLevelProperty() {
    return playerLevelProperty;
  }

  public StringProperty getRecipientLevelProperty() {
    return recipientLevelProperty;
  }

  private void doPlayerAndNeighbourLevels(Business business) {
    if (business instanceof FactoryBusiness) {
      senderLevelProperty.set(SUPPLIERKEY);
      playerLevelProperty.set(FACTORYKEY);
      recipientLevelProperty.set(WAREHOUSEKEY);
    }
    if (business instanceof RegionalWarehouseBusiness) {
      senderLevelProperty.set(FACTORYKEY);
      playerLevelProperty.set(WAREHOUSEKEY);
      recipientLevelProperty.set(WHOLESALEKEY);
    }
    if (business instanceof WholesaleBusiness) {
      senderLevelProperty.set(WAREHOUSEKEY);
      playerLevelProperty.set(WHOLESALEKEY);
      recipientLevelProperty.set(RETAILKEY);
    }
    if (business instanceof RetailBusiness) {
      senderLevelProperty.set(WHOLESALEKEY);
      playerLevelProperty.set(RETAILKEY);
      recipientLevelProperty.set(DEMANDKEY);
    }
  }

  private void placeOrderTextListener(ObservableValue observableValue, Object oldValue,
      Object newValue) {
    doPlaceOrderErrorIndicator(!isPlaceOrderAmountValid((String) newValue));
  }

  private static boolean isPlaceOrderAmountValid(String orderAmount) {
    try {
      return Integer.parseInt(orderAmount) >= 0;
    } catch (NumberFormatException exception) {
      return false;
    }
  }

  public StringProperty getToggleAgentButtonProperty() {
    return toggleAgentProperty;
  }

  public void setToggleAgentProperty(String value) {
    toggleAgentProperty.set(value);
  }

  @Override
  public void run() {
    Platform.runLater(() ->
        timerProperty.set(timerProperty.get() - 1)
    );
  }
}