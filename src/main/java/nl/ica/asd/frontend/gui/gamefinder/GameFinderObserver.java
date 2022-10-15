package nl.ica.asd.frontend.gui.gamefinder;

import java.util.Observable;
import java.util.ResourceBundle;
import nl.ica.asd.frontend.gui.service.ThreadRipperObserver;
import nl.ica.asd.network.communication.gameinitialisation.ConnectionResult;

public class GameFinderObserver extends ThreadRipperObserver {

  private GameFinderViewModel viewModel;

  private ResourceBundle resourceBundle;

  private static final String ERROR_LOBBY_FULL = "gamefinder.error_lobby_full";

  private static final String ERROR_USERNAME_ALREADY_EXISTS = "gamefinder.error_username_already_chosen";

  private static final String ERROR_GAME_NOT_FOUND = "gamefinder.error_game_not_found";

  public GameFinderObserver(ResourceBundle resources, GameFinderViewModel viewModel) {
    this.viewModel = viewModel;
    this.resourceBundle = resources;
  }

  @Override
  protected void synchronizedUpdate(Observable observable, Object argument) {
    if (argument instanceof ConnectionResult) {
      ConnectionResult connectionResult = (ConnectionResult) argument;

      switch (connectionResult) {
        case SUCCESS:
          viewModel.setIsConnected(true);
          break;

        case LOBBY_FULL:
          viewModel.doShowErrorMessage(resourceBundle.getString(ERROR_LOBBY_FULL));
          break;

        case USERNAME_ALREADY_CHOSEN:
          viewModel.doShowErrorMessage(resourceBundle.getString(ERROR_USERNAME_ALREADY_EXISTS));
          break;

        case GAME_NOT_FOUND:
          viewModel.doShowErrorMessage(resourceBundle.getString(ERROR_GAME_NOT_FOUND));
          break;
      }
    }
  }
}
