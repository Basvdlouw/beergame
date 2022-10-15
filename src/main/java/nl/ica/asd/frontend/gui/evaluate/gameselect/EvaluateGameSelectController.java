package nl.ica.asd.frontend.gui.evaluate.gameselect;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.inject.Inject;
import javax.inject.Named;
import nl.ica.asd.frontend.gui.evaluate.graphs.linear.GraphLinearGameViewModel;
import nl.ica.asd.frontend.gui.evaluate.linear.EvaluateLinearGameViewModel;
import nl.ica.asd.frontend.gui.scenecreator.SceneController;
import nl.ica.asd.frontend.gui.scenecreator.SceneCreator;
import nl.ica.asd.frontend.gui.scenecreator.SceneLoaderInitializable;
import nl.ica.asd.logic.domain.BeerGame;
import nl.ica.asd.logic.postgameprocessor.PostGameProcessor;
import nl.ica.asd.logic.postgameprocessor.PostGameProcessorException;
import nl.ica.asd.logic.postgameprocessor.dto.PlayedGame;
import nl.ica.asd.network.communication.gameinitialisation.InitializeGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EvaluateGameSelectController extends
    SceneController<EvaluateGameSelectViewModel> implements
    SceneLoaderInitializable {

  @Inject
  private @Named("EvaluateLinearGameSceneCreator")
  SceneCreator evaluateLinearGame;

  @Inject
  private @Named("MainMenuSceneCreator")
  SceneCreator mainMenuSceneCreator;

  @Inject
  private @Named("GraphLinearGameSceneCreator")
  SceneCreator graphLinearGameSceneCreator;

  @Inject
  private EvaluateLinearGameViewModel evaluateLinearGameViewModel;

  @Inject
  private PostGameProcessor postGameProcessor;

  @FXML
  private TableView<PlayedGame> playedGames;

  @FXML
  private Button cancelButton;

  @FXML
  private Button evaluateButton;

  @FXML
  private Button showGraphButton;

  @FXML
  private TableColumn<BeerGame, String> gameName;

  @FXML
  private TableColumn<BeerGame, Date> gameDate;

  private static final Logger logger = LoggerFactory.getLogger(EvaluateGameSelectController.class);

  @Override
  public void sceneLoaderInitialize(URL location, ResourceBundle resources) {
    gameName.setCellValueFactory(new PropertyValueFactory<>("name"));
    gameDate.setCellValueFactory(new PropertyValueFactory<>("date"));

    playedGames.getSelectionModel().selectedItemProperty()
        .addListener((obs, oldSelection, newSelection) -> viewModel.setGameSelectedProperty(false));
    playedGames.setItems(getFinishedBeerGames());

    evaluateButton.setOnAction(this::evaluateButtonClicked);
    evaluateButton.disableProperty().bindBidirectional(viewModel.getGameSelectedProperty());

    evaluateButton.setOnAction(this::evaluateButtonClicked);
    cancelButton.setOnAction(this::cancelButtonClicked);
    showGraphButton.setOnAction(this::showGraphButtonClicked);

    playedGames.getSelectionModel().selectedItemProperty()
        .addListener((obs, oldSelection, newSelection) -> {
          if (oldSelection == null) {
            evaluateButton.setDisable(false);
            showGraphButton.setDisable(false);
          }
        });
  }

  private void showGraphButtonClicked(ActionEvent actionEvent) {
    try {
      PlayedGame selectedItem = playedGames.getSelectionModel().getSelectedItem();
      GraphLinearGameViewModel graphLinearGameViewModel = new GraphLinearGameViewModel(
          selectedItem);
      switchScene(graphLinearGameSceneCreator, actionEvent, graphLinearGameViewModel);
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  private ObservableList<PlayedGame> getFinishedBeerGames() {
    return postGameProcessor.getPlayedBeerGames().stream()
        .collect(Collectors.toCollection(FXCollections::observableArrayList));
  }

  private void evaluateButtonClicked(ActionEvent event) {
    PlayedGame selectedItem = playedGames.getSelectionModel().getSelectedItem();

    try {
      BeerGame beerGame = postGameProcessor.getPlayedBeerGame(selectedItem);
      evaluateLinearGameViewModel.setBeerGame(beerGame);
      switchScene(evaluateLinearGame, event, evaluateLinearGameViewModel);
    } catch (PostGameProcessorException exception) {
      logger.error(exception.getMessage(), exception);
    }
  }

  private void cancelButtonClicked(ActionEvent event) {
    switchScene(mainMenuSceneCreator, event);
  }
}
