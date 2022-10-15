package nl.ica.asd.frontend.gui.service.dto;

import java.util.List;

public class PlayerLobbyState {

  private List<ConnectedPlayerWithBusiness> connectedPlayerWithBusinessList;
  private List<AvailableBusiness> availableBusinessList;

  public PlayerLobbyState(List<ConnectedPlayerWithBusiness> connectedPlayerWithBusinessList,
      List<AvailableBusiness> availableBusinessList) {
    this.connectedPlayerWithBusinessList = connectedPlayerWithBusinessList;
    this.availableBusinessList = availableBusinessList;
  }

  public List<ConnectedPlayerWithBusiness> getConnectedPlayerWithBusinessList() {
    return connectedPlayerWithBusinessList;
  }

  public List<AvailableBusiness> getAvailableBusinessList() {
    return availableBusinessList;
  }
}
