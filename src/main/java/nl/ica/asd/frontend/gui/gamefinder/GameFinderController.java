package nl.ica.asd.frontend.gui.gamefinder;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javax.inject.Inject;
import javax.inject.Named;
import nl.ica.asd.frontend.gui.converters.InputRestrictor;
import nl.ica.asd.frontend.gui.scenecreator.SceneController;
import nl.ica.asd.frontend.gui.scenecreator.SceneCreator;
import nl.ica.asd.network.communication.interfaces.GameConnection;
import nl.ica.asd.storage.applicationdatastorage.ApplicationDataAccess;


public class GameFinderController extends SceneController implements Initializable {

  @Inject
  private @Named("MainMenuSceneCreator")
  SceneCreator mainMenuSceneCreator;

  @Inject
  private @Named("PlayerLobbySceneCreator")
  SceneCreator playerLobbySceneCreator;

  @Inject
  private GameConnection gameConnection;

  @Inject
  private ApplicationDataAccess applicationDataAccess;

  @FXML
  private Button backButton;

  @FXML
  private Button joinButton;

  @FXML
  private TextField ipAddressField;

  @FXML
  private Label connectErrorMessage;

  @FXML
  private Label isConnected;

  @Inject
  private GameFinderViewModel viewModel;

  @Inject
  private InputRestrictor inputRestrictor;

  private ResourceBundle resources;

  private static final String ERROR_CONNECT_MESSAGE = "gamefinder.connectErrorMessage";

  @FXML
  protected void handleJoinButton(ActionEvent action) {
    viewModel.setConnectErrorMessageVisible(false);
    try {
      gameConnection.connectToGame(viewModel.getIpAddress(), applicationDataAccess.getUsername());
    } catch (IOException e) {
      viewModel.doShowErrorMessage(resources.getString(ERROR_CONNECT_MESSAGE));
    }
  }

  @FXML
  protected void handleBackButton(ActionEvent action) {
    switchScene(mainMenuSceneCreator, action);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    this.resources = resources;
    guiService.addObserver(new GameFinderObserver(resources, viewModel));
    ipAddressField.textProperty().bindBidirectional(viewModel.getIpAddressProperty());
    isConnected.disableProperty().bind(viewModel.getIsConnectedProperty());
    connectErrorMessage.textProperty().bind(viewModel.getConnectErrorMessageProperty());
    connectErrorMessage.visibleProperty().bind(viewModel.getConnectErrorMessageVisibleProperty());
    inputRestrictor.restrictInput(ipAddressField, inputRestrictor.getIPADDRESS());

    viewModel.getIsConnectedProperty()
        .addListener((obs, oldSelection, newSelection) -> {
          if (newSelection) {
            switchScene(playerLobbySceneCreator, (Stage) isConnected.getScene().getWindow());
          }
        });
  }
}
