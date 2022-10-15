package nl.ica.asd.frontend.gui.mainmenu;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Named;
import nl.ica.asd.frontend.gui.evaluate.gameselect.EvaluateGameSelectViewModel;
import nl.ica.asd.frontend.gui.scenecreator.SceneController;
import nl.ica.asd.frontend.gui.scenecreator.SceneCreator;
import nl.ica.asd.network.communication.gameinitialisation.InitializeGame;
import nl.ica.asd.storage.gamedatastorage.GameDataAccess;

@Default
public class MainMenuController extends SceneController {

  @Inject
  private @Named("PersonalSettingsSceneCreator")
  SceneCreator personalSettingsSceneCreator;

  @Inject
  private @Named("GameFinderSceneCreator")
  SceneCreator gameFinderSceneCreator;

  @Inject
  private @Named("EvaluateGameSelectSceneCreator")
  SceneCreator evaluateGameSelectSceneCreator;

  @Inject
  private @Named("ConfigGameSceneCreator")
  SceneCreator configGameSceneCreator;

  @FXML
  private Button joinGameButton;

  @FXML
  private Button createGameButton;

  @FXML
  private Button evaluateGameButton;

  @FXML
  private Button settingsButton;

  @FXML
  private void handleJoinGameButton(ActionEvent action) {
    switchScene(gameFinderSceneCreator, action);
  }


  @FXML
  private void handleCreateGameButton(ActionEvent action) {
    switchScene(configGameSceneCreator, action);
  }

  @FXML
  private void handleEvaluateGameButton(ActionEvent action) {
    switchScene(evaluateGameSelectSceneCreator, action, new EvaluateGameSelectViewModel());
  }

  @FXML
  private void handleSettingsButton(ActionEvent action) {
    switchScene(personalSettingsSceneCreator, action);
  }

}