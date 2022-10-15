package nl.ica.asd.frontend.gui.configgame;


import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextField;
import javax.inject.Inject;
import javax.inject.Named;
import nl.ica.asd.frontend.gui.converters.GameVisibilityTypeStringConverter;
import nl.ica.asd.frontend.gui.converters.InputRestrictor;
import nl.ica.asd.frontend.gui.converters.SupplyChainTypeStringConverter;
import nl.ica.asd.frontend.gui.scenecreator.SceneController;
import nl.ica.asd.frontend.gui.scenecreator.SceneCreator;
import nl.ica.asd.logic.domain.SupplyChainType;
import nl.ica.asd.network.communication.gameinitialisation.InitializeGame;
import nl.ica.asd.network.communication.interfaces.GameCreator;
import nl.ica.asd.storage.domain.GameConfiguration;
import nl.ica.asd.storage.domain.GameData;
import nl.ica.asd.storage.exception.UnknownGameDataException;
import nl.ica.asd.storage.exception.UnknownGameException;
import nl.ica.asd.storage.gameconfig.GameConfig;
import nl.ica.asd.storage.gameconfig.GameConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigGameController extends SceneController implements Initializable {

  private static final Logger logger = LoggerFactory.getLogger(ConfigGameController.class);

  @Inject
  private @Named("MainMenuSceneCreator")
  SceneCreator mainMenuSceneCreator;

  @Inject
  private @Named("PlayerLobbySceneCreator")
  SceneCreator playerLobbySceneCreator;

  @Inject
  private GameConfig gameConfig;

  @Inject
  private GameCreator gameCreator;

  @Inject
  private GameConfigManager gameConfigManager;

  @FXML
  private TextField gameName;

  @FXML
  private ComboBox gameType;

  @FXML
  private ComboBox visibility;

  @FXML
  private TextField players;

  @FXML
  private TextField turns;

  @FXML
  private CheckBox timeLimitBox;

  @FXML
  private TextField timeLimit;

  @FXML
  private Button cancel;

  @FXML
  private Button createGame;

  @FXML
  private Label playerErrorMessage;

  @FXML
  private Label turnErrorMessage;

  @FXML
  private Label timeLimitErrorMessage;

  @FXML
  private Label nameErrorMessage;

  @Inject
  private ConfigGameViewModel configGameViewModel;

  @Inject
  private InputRestrictor inputRestrictor;

  @FXML
  private void handleCancelButton(ActionEvent action) {
    switchScene(mainMenuSceneCreator, action);
  }

  @FXML
  private void handleCreateButton(ActionEvent action) {
    if (checkInput()) {
      if (!configGameViewModel.getTimeLimitCheckedProperty().get()) {
        //If the timelimit is disabled the timelimit is set to 0 so it is disabled in the game
        configGameViewModel.setTimeLimit(0);
      }

      GameConfiguration gameConfiguration = new GameConfiguration(
          configGameViewModel.getPlayerAmount(),
          configGameViewModel.getTurnAmount(),
          configGameViewModel.getSelectedGameType(),
          configGameViewModel.getTimeLimit(),
          configGameViewModel.getSelectedVisibility(),
          gameConfigManager.getInitialBusinessStateLineair());

      GameData gameData = new GameData(
          configGameViewModel.getName(),
          new Date(),
          gameConfiguration);

      try {
        if (gameConfig.isGameDataValid(gameData)) {

          gameConfig.setGameData(gameData);

          gameCreator.createGame();

          switchScene(playerLobbySceneCreator, action);
        }
      } catch (UnknownGameException | UnknownGameDataException | IOException exception) {
        logger.error("Something went wrong when creating the game.", exception);
      }
    }
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    gameName.textProperty().bindBidirectional(configGameViewModel.getNameProperty());

    configGameViewModel.getSelectedGameTypeProperty()
        .set((SingleSelectionModel) gameType.selectionModelProperty().get());
    gameType.setConverter(new SupplyChainTypeStringConverter(resources));
    gameType.itemsProperty().bind(configGameViewModel.getGameTypesProperty());
    configGameViewModel.getSelectedVisibilityProperty()
        .set((SingleSelectionModel) visibility.selectionModelProperty().get());
    visibility.setConverter(new GameVisibilityTypeStringConverter(resources));
    visibility.itemsProperty().bind(configGameViewModel.getVisibilitiesProperty());

    players.textProperty().bindBidirectional(configGameViewModel.getPlayerAmountProperty());
    turns.textProperty().bindBidirectional(configGameViewModel.getTurnAmountProperty());

    timeLimitBox.selectedProperty()
        .bindBidirectional(configGameViewModel.getTimeLimitCheckedProperty());
    timeLimit.disableProperty().bind(configGameViewModel.getTimeLimitCheckedProperty());
    timeLimitBox.selectedProperty()
        .bindBidirectional(configGameViewModel.getTimeLimitCheckedProperty());
    timeLimit.disableProperty().bind(configGameViewModel.getTimeLimitCheckedProperty().not());
    timeLimit.textProperty().bindBidirectional(configGameViewModel.getTimeLimitProperty());

    inputRestrictor.restrictInput(players, inputRestrictor.getONLYNUMERIC());
    inputRestrictor.restrictInput(turns, inputRestrictor.getONLYNUMERIC());
    inputRestrictor.restrictInput(timeLimit, inputRestrictor.getONLYNUMERIC());
    inputRestrictor.restrictInput(gameName, inputRestrictor.getGAMENAME());

    gameType.setOnAction(event -> {
      if (configGameViewModel.getSelectedGameType() == SupplyChainType.LINEAR) {
        players.setDisable(true);
        players.setText(Integer.toString(4));
      } else if (configGameViewModel.getSelectedGameType() == SupplyChainType.PYRAMID) {
        players.setDisable(true);
        players.setText(Integer.toString(15));
      } else {
        players.setDisable(false);
      }
    });

    try {
      final GameData gameData = gameConfig.getDefaultGameData();

      final GameConfiguration gameConfiguration = gameData.getSettings();

      configGameViewModel.setName(gameData.getName());
      configGameViewModel.setPlayerAmount(String.valueOf(gameConfiguration.getMaxPlayers()));
      players.setDisable(true);
      configGameViewModel.setTurnAmount(String.valueOf(gameConfiguration.getMaxRounds()));

      configGameViewModel.selectGameType(gameConfiguration.getSupplyChainType());
      configGameViewModel.selectVisibility(gameConfiguration.getGameVisibilityType());

      configGameViewModel.setTimeLimit(gameConfiguration.getTimeLimitPerRound());
    } catch (IOException e) {
      logger.error(e.getMessage());
    }
  }

  private boolean checkInput() {
    boolean accepted = true;
    resetErrorMessages();

    if (gameName.getText().isEmpty()) {
      nameErrorMessage.setVisible(true);
      accepted = false;
    }

    if (!isPositiveNumber(players)) {
      playerErrorMessage.setVisible(true);
      accepted = false;
    }

    if (!isPositiveNumber(turns)) {
      turnErrorMessage.setVisible(true);
      accepted = false;
    }

    if (configGameViewModel.getTimeLimitCheckedProperty().get() && !isPositiveNumber(timeLimit)) {
      timeLimitErrorMessage.setVisible(true);
      accepted = false;
    }
    return accepted;
  }

  private void resetErrorMessages() {
    playerErrorMessage.setVisible(false);
    turnErrorMessage.setVisible(false);
    timeLimitErrorMessage.setVisible(false);
  }


  private boolean isPositiveNumber(TextField field) {
    if (!field.getText().isEmpty()) {
      NumberFormat nf = NumberFormat.getInstance();
      try {
        String input = field.getText();
        Number parsedNumber = nf.parse(input);
        return parsedNumber.intValue() > 0;
      } catch (ParseException ex) {
        return false;
      }
    }
    return false;
  }
}