package nl.ica.asd.frontend.gui.gamefinder;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class GameFinderViewModel {

  private StringProperty ipAddressProperty = new SimpleStringProperty("");

  private StringProperty connectErrorMessageProperty = new SimpleStringProperty("");

  private BooleanProperty connectErrorMessageVisibleProperty = new SimpleBooleanProperty(false);

  private BooleanProperty isConnectedProperty = new SimpleBooleanProperty(false);

  public StringProperty getConnectErrorMessageProperty() {
    return connectErrorMessageProperty;
  }

  public BooleanProperty getConnectErrorMessageVisibleProperty() {
    return connectErrorMessageVisibleProperty;
  }

  public BooleanProperty getIsConnectedProperty() {
    return isConnectedProperty;
  }

  public StringProperty getIpAddressProperty() {
    return ipAddressProperty;
  }

  public String getIpAddress() {
    return ipAddressProperty.get();
  }

  public void setIpAddress(String ipAddress) {
    ipAddressProperty.set(ipAddress);
  }

  public void setIsConnected(boolean isConnected) {
    isConnectedProperty.set(isConnected);
  }

  public void setConnectErrorMessage(String connectErrorMessage) {
    connectErrorMessageProperty.set(connectErrorMessage);
  }

  public void setConnectErrorMessageVisible(boolean isConnectErrorMessageVisible) {
    connectErrorMessageVisibleProperty.set(isConnectErrorMessageVisible);
  }

  public void doShowErrorMessage(String errorMessage) {
    if (!errorMessage.isEmpty()) {
      setConnectErrorMessage(errorMessage);
      setConnectErrorMessageVisible(true);
    } else {
      setConnectErrorMessageVisible(false);
    }
  }
}
