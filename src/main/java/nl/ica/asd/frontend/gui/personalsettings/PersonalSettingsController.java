package nl.ica.asd.frontend.gui.personalsettings;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextField;
import javax.inject.Inject;
import javax.inject.Named;
import nl.ica.asd.frontend.gui.scenecreator.SceneController;
import nl.ica.asd.frontend.gui.scenecreator.SceneCreator;
import nl.ica.asd.frontend.languageservice.LanguageService;
import nl.ica.asd.storage.applicationdatastorage.ApplicationDataAccess;

public class PersonalSettingsController extends SceneController implements Initializable {

  @Inject
  private @Named("MainMenuSceneCreator")
  SceneCreator mainMenuSceneCreator;

  @Inject
  private ApplicationDataAccess applicationDataAccess;

  @Inject
  private LanguageService languageService;

  @FXML
  private Button saveSettingsButton;

  @FXML
  private Button cancelButton;

  @FXML
  private ComboBox languageBox;

  @FXML
  private Label languageOption;

  @FXML
  private Label userNameOption;

  @FXML
  private TextField userNameField;

  @FXML
  private Label userNameErrorMessage;

  @Inject
  private PersonalSettingsViewModel viewModel;

  @FXML
  protected void handleSaveSettingsButton(ActionEvent action) {
    if (checkInput()) {
      applicationDataAccess.setUsername(viewModel.getUsername());
      applicationDataAccess.setLanguageConfig(viewModel.getSelectedLanguageCode());
      switchScene(mainMenuSceneCreator, action);
    } else {
      userNameErrorMessage.setVisible(true);
    }
  }

  private boolean checkInput() {
    userNameErrorMessage.setVisible(false);
    return !viewModel.getUsername().isEmpty();
  }
  @FXML
  protected void handleCancelSettingButton(ActionEvent action) {
    if (checkInput()) {
      switchScene(mainMenuSceneCreator, action);
    } else {
      userNameErrorMessage.setVisible(true);
    }
  }


  @Override
  public void initialize(URL location, ResourceBundle resources) {
    viewModel.getSelectedLanguageProperty().set(
        (SingleSelectionModel) languageBox.selectionModelProperty().get());

    languageBox.selectionModelProperty().bind(viewModel.getSelectedLanguageProperty());
    languageBox.itemsProperty().bind(viewModel.getLanguagesProperty());

    userNameField.textProperty().bindBidirectional(viewModel.getUsernameProperty());

    viewModel.setUsername(applicationDataAccess.getUsername());
    viewModel.setLanguages(languageService.getAllLanguageDataKeys());
    viewModel.selectLanguageByCode(applicationDataAccess.getCurrentLanguage());
  }
}