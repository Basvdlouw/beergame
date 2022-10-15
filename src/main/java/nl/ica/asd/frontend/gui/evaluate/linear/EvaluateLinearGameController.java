package nl.ica.asd.frontend.gui.evaluate.linear;

import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javax.inject.Inject;
import javax.inject.Named;
import nl.ica.asd.frontend.gui.evaluate.gameselect.EvaluateGameSelectViewModel;
import nl.ica.asd.frontend.gui.evaluate.linear.business.BusinessBlockObserver;
import nl.ica.asd.frontend.gui.evaluate.linear.business.BusinessBlockViewModel;
import nl.ica.asd.frontend.gui.scenecreator.SceneController;
import nl.ica.asd.frontend.gui.scenecreator.SceneCreator;
import nl.ica.asd.frontend.gui.scenecreator.SceneLoaderInitializable;
import nl.ica.asd.logic.domain.Business;
import nl.ica.asd.logic.domain.businesses.RetailBusiness;
import nl.ica.asd.logic.postgameprocessor.PostGameProcessor;
import nl.ica.asd.logic.postgameprocessor.PostGameProcessorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EvaluateLinearGameController extends
    SceneController<EvaluateLinearGameViewModel> implements
    SceneLoaderInitializable {

  @Inject
  private @Named("BusinessBlockSceneCreator")
  SceneCreator businessBlockSceneCreator;

  @Inject
  private @Named("EvaluateGameSelectSceneCreator")
  SceneCreator evaluateGameSelectSceneCreator;

  @Inject
  private PostGameProcessor postGameProcessor;

  @FXML
  private AnchorPane anchorPane;

  @FXML
  private Button doneButton;

  @FXML
  private Button nextRound;

  @FXML
  private Button previousRound;

  @FXML
  private Label roundLabel;

  @FXML
  private Label currentRoundLabel;

  @FXML
  private Label maxRoundsLabel;

  private static final Logger logger = LoggerFactory.getLogger(EvaluateLinearGameController.class);

  private static final int BUSINESS_LAYOUT_OFFSET_X = 20;
  private static final int BUSINESS_LAYOUT_OFFSET_Y = 175;
  private static final int BUSINESS_LAYOUT_MARGIN_X = 5;

  @Override
  public void sceneLoaderInitialize(URL location, ResourceBundle resources) {

    roundLabel.textProperty().bind(viewModel.getRoundPropertyLabel().asString());
    currentRoundLabel.textProperty().bind(viewModel.getRoundPropertyLabel().asString());
    maxRoundsLabel.textProperty().bind(viewModel.getPlayedRoundsLabel().asString());

    doneButton.setOnAction(this::doneButtonClicked);
    nextRound.setOnAction(event -> nextRoundButtonClicked());
    previousRound.setOnAction(event -> previousRoundButtonClicked());

    Collections.sort(viewModel.getBeerGame().getBusinesses(),
        Comparator.comparingInt(a -> a.getBusinessType().getIndexInChain()));

    Collections.reverse(viewModel.getBeerGame().getBusinesses());

    viewModel.getBeerGame().getBusinesses().stream().map(this::drawBusinessAndGetViewModel)
        .map(BusinessBlockObserver::new)
        .forEach(guiService::addObserver);
    updateRound();
  }

  private BusinessBlockViewModel drawBusinessAndGetViewModel(Business business) {
    BusinessBlockViewModel viewModel = new BusinessBlockViewModel(business);
    Scene scene = businessBlockSceneCreator.create(viewModel);

    AnchorPane businessBlock = (AnchorPane) scene.getRoot();
    int count = anchorPane.getChildren().size();
    double width = count * businessBlock.prefWidthProperty().doubleValue() + (count
        * BUSINESS_LAYOUT_MARGIN_X);
    businessBlock.setLayoutX(width + BUSINESS_LAYOUT_OFFSET_X);
    businessBlock.setLayoutY(BUSINESS_LAYOUT_OFFSET_Y);

    anchorPane.getChildren().add(businessBlock);

    return viewModel;
  }

  private void doneButtonClicked(ActionEvent actionEvent) {
    switchScene(evaluateGameSelectSceneCreator, actionEvent, new EvaluateGameSelectViewModel());
  }

  private void nextRoundButtonClicked() {
    viewModel.incrementRound();
    updateRound();
  }

  private void previousRoundButtonClicked() {
    viewModel.decrementRound();
    updateRound();
  }

  private void updateRound() {
      try {
        postGameProcessor.getBeerGameTurn(viewModel.getRound().get())
            .forEach(guiService::updateEvaluateGameBusiness);
      } catch (PostGameProcessorException e) {
        logger.error(e.getMessage());
      }
  }
}
