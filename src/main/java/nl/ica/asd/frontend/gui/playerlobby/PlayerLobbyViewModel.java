package nl.ica.asd.frontend.gui.playerlobby;

import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nl.ica.asd.frontend.gui.service.dto.AvailableBusiness;
import nl.ica.asd.frontend.gui.service.dto.ConnectedPlayerWithBusiness;

public class PlayerLobbyViewModel {


  private final StringProperty ipAddress = new SimpleStringProperty("");
  private final StringProperty errorMessage = new SimpleStringProperty("");
  private final BooleanProperty factoryDisabled = new SimpleBooleanProperty(false);
  private final BooleanProperty regionalWarehouseDisabled = new SimpleBooleanProperty(false);
  private final BooleanProperty wholesaleDisabled = new SimpleBooleanProperty(false);
  private final BooleanProperty retailDisabled = new SimpleBooleanProperty(false);
  private final ObjectProperty<ObservableList<ConnectedPlayerWithBusiness>> connectedPlayersWithBusiness = new SimpleObjectProperty<>(
      FXCollections.emptyObservableList());
  private final ObjectProperty<ObservableList<AvailableBusiness>> availableBusiness = new SimpleObjectProperty<>(
      FXCollections.emptyObservableList());


  public StringProperty getIpAddressProperty() {
    return ipAddress;
  }

  public StringProperty getErrorMessageProperty() {
    return errorMessage;
  }

  public BooleanProperty getFactoryDisabledProperty() {
    return factoryDisabled;
  }

  public BooleanProperty getRegionalWarehouseDisabled() {
    return regionalWarehouseDisabled;
  }

  public BooleanProperty getWholesaleDisabledProperty() {
    return wholesaleDisabled;
  }

  public BooleanProperty getRetailDisabledProperty() {
    return retailDisabled;
  }

  public ObjectProperty<ObservableList<ConnectedPlayerWithBusiness>> getConnectedPlayersWithBusiness() {
    return connectedPlayersWithBusiness;
  }

  public ObjectProperty<ObservableList<AvailableBusiness>> getAvailableBusiness() {
    return availableBusiness;
  }

  public void setIpAddressProperty(String ipAddress) {
    this.ipAddress.set(ipAddress);
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage.set(errorMessage);
  }

  public void setFactoryDisabledProperty(boolean factoryDisabled) {
    this.factoryDisabled.set(factoryDisabled);
  }

  public void setRegionalWarheouseDisabledProperty(boolean regionalWarheouseDisabled) {
    this.regionalWarehouseDisabled
        .set(regionalWarheouseDisabled);
  }

  public void setWholesaleDisabledProperty(boolean wholesaleDisabled) {
    this.wholesaleDisabled.set(wholesaleDisabled);
  }

  public void setRetailDisabledProperty(boolean retailDisabled) {
    this.retailDisabled.set(retailDisabled);
  }

  public void setConnectedPlayersWithBusiness(
      List<ConnectedPlayerWithBusiness> connectedPlayerWithBusinesses) {
    this.connectedPlayersWithBusiness
        .set(FXCollections.observableArrayList(connectedPlayerWithBusinesses));
  }

  public void setAvailableBusiness(List<AvailableBusiness> availableBusinesses) {
    this.availableBusiness.set(FXCollections.observableArrayList(availableBusinesses));
  }
}
