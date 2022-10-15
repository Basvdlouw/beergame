package nl.ica.asd.frontend.gui.playerlobby;

import java.util.List;
import java.util.Observable;
import nl.ica.asd.frontend.gui.service.ThreadRipperObserver;
import nl.ica.asd.frontend.gui.service.dto.AvailableBusiness;
import nl.ica.asd.frontend.gui.service.dto.PlayerLobbyState;

public class PlayerLobbyObserver extends ThreadRipperObserver {

  private PlayerLobbyViewModel playerLobbyViewModel;

  public PlayerLobbyObserver(PlayerLobbyViewModel playerLobbyViewModel) {
    this.playerLobbyViewModel = playerLobbyViewModel;
  }

  @Override
  protected void synchronizedUpdate(Observable observable, Object argument) {
    if (argument instanceof PlayerLobbyState) {
      PlayerLobbyState playerLobbyState = ((PlayerLobbyState) argument);

      playerLobbyViewModel
          .setConnectedPlayersWithBusiness(playerLobbyState.getConnectedPlayerWithBusinessList());
      playerLobbyViewModel.setAvailableBusiness(playerLobbyState.getAvailableBusinessList());

      setErrorMessage(playerLobbyState.getAvailableBusinessList());
      setDisabledBusinesses(playerLobbyState.getAvailableBusinessList());
    }
  }

  private static boolean allPlayersAreReady(List<AvailableBusiness> availableBusinesses) {
    return availableBusinesses.stream().allMatch(AvailableBusiness::getReady);
  }

  private void setErrorMessage(List<AvailableBusiness> availableBusinesses) {
    String errorMessage = allPlayersAreReady(availableBusinesses)
        ? "Alle Spelers hebben een niveau gekozen!" // TODO: Translation
        : "Nog niet alle spelers hebben een niveau gekozen";

    playerLobbyViewModel.setErrorMessage(errorMessage);
  }

  private void setDisabledBusinesses(List<AvailableBusiness> availableBusinesses) {
    for (AvailableBusiness availableBusiness : availableBusinesses) {
      switch (availableBusiness.getBusinessType()) {
        case FACTORY:
          playerLobbyViewModel.setFactoryDisabledProperty(availableBusiness.getReady());
          break;
        case REGIONAL_WAREHOUSE:
          playerLobbyViewModel.setRegionalWarheouseDisabledProperty(availableBusiness.getReady());
          break;
        case WHOLESALE:
          playerLobbyViewModel.setWholesaleDisabledProperty(availableBusiness.getReady());
          break;
        case RETAIL:
          playerLobbyViewModel.setRetailDisabledProperty(availableBusiness.getReady());
          break;
        default:
          break;
      }
    }
  }
}
