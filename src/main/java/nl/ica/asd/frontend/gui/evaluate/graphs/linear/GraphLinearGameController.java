package nl.ica.asd.frontend.gui.evaluate.graphs.linear;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javax.inject.Inject;
import javax.inject.Named;
import nl.ica.asd.frontend.gui.converters.PlayerLevelStringConverter;
import nl.ica.asd.frontend.gui.evaluate.businessgraphs.BusinessGraphsViewModel;
import nl.ica.asd.frontend.gui.evaluate.gameselect.EvaluateGameSelectViewModel;
import nl.ica.asd.frontend.gui.scenecreator.SceneController;
import nl.ica.asd.frontend.gui.scenecreator.SceneCreator;
import nl.ica.asd.frontend.gui.scenecreator.SceneLoaderInitializable;
import nl.ica.asd.frontend.gui.service.dto.BusinessWithState;
import nl.ica.asd.logic.domain.BeerGame;
import nl.ica.asd.logic.domain.Business;
import nl.ica.asd.logic.domain.businesses.FactoryBusiness;
import nl.ica.asd.logic.domain.businesses.RegionalWarehouseBusiness;
import nl.ica.asd.logic.domain.businesses.RetailBusiness;
import nl.ica.asd.logic.domain.businesses.WholesaleBusiness;
import nl.ica.asd.logic.postgameprocessor.PostGameProcessor;
import nl.ica.asd.logic.postgameprocessor.PostGameProcessorException;
import nl.ica.asd.logic.postgameprocessor.dto.BusinessRoundState;
import nl.ica.asd.storage.gamedatastorage.GameDataAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphLinearGameController extends SceneController<GraphLinearGameViewModel> implements
    SceneLoaderInitializable {

  @Inject
  private @Named("EvaluateGameSelectSceneCreator")
  SceneCreator evaluateGameSelectSceneCreator;

  @Inject
  private @Named("LineChartSceneCreator")
  SceneCreator lineChartSceneCreator;

  @Inject
  private @Named("BusinessGraphsSceneCreator")
  SceneCreator businessGraphsSceneCreator;

  @Inject
  private @Named("GameDataAccess")
  GameDataAccess gameDataAccess;

  @Inject
  private PostGameProcessor postGameProcessor;

  private PlayerLevelStringConverter levelConverter;

  @FXML
  private Pane chartPane;

  @FXML
  private Pane bar;

  @FXML
  private Button backButton;

  @FXML
  private Button businessSpecificChartButton;

  private BeerGame beerGame;

  private LineChart linearGameChart;

  private Logger logger = LoggerFactory.getLogger(GraphLinearGameController.class);

  @Override
  public void sceneLoaderInitialize(URL location, ResourceBundle resources) {
    try {
      beerGame = postGameProcessor.getPlayedBeerGame(viewModel.getPlayedGame());
      levelConverter = new PlayerLevelStringConverter(resources);

      addLinearGameChart(resources);

      drawLinearGameChart();
    } catch (PostGameProcessorException e) {
      logger.error(e.getMessage(), e);
    }

    backButton.setOnAction(this::backButtonClicked);
    businessSpecificChartButton.setOnAction(this::businessSpecificChartButtonClicked);
  }

  private void addLinearGameChart(ResourceBundle resources) {
    linearGameChart = loadChartTemplate();
    linearGameChart.getXAxis().setLabel(resources.getString("graphlineargame.weeks"));
    linearGameChart.getYAxis().setLabel(resources.getString("graphlineargame.inventory"));

    chartPane.getChildren().add(linearGameChart);
  }

  private String getLevelTranslation(Business b) {
    String result = "";
    if (b instanceof FactoryBusiness) {
      result = levelConverter.toString("FACTORY");
    } else if (b instanceof RegionalWarehouseBusiness) {
      result = levelConverter.toString("WAREHOUSE");
    } else if (b instanceof RetailBusiness) {
      result = levelConverter.toString("RETAIL");
    } else if (b instanceof WholesaleBusiness) {
      result = levelConverter.toString("WHOLESALE");
    }
    return result;
  }

  private LineChart<Number, Number> loadChartTemplate() {
    LineChart<Number, Number> chart = (LineChart<Number, Number>) lineChartSceneCreator.create()
        .getRoot();
    ObservableList charts = chartPane.getChildren();
    AtomicReference<Double> yPosition = new AtomicReference<>((double) 0);
    charts.forEach(child -> {
      double height = yPosition.get();
      height += (((LineChart<Number, Number>) child).getPrefHeight());
      yPosition.set(height);
    });
    chart.setLayoutY(yPosition.get());
    ((NumberAxis)chart.getXAxis()).forceZeroInRangeProperty().setValue(false);
    ((NumberAxis)chart.getXAxis()).setLowerBound(1);
    ((NumberAxis)chart.getXAxis()).setUpperBound(beerGame.getCurrentRound());
    ((NumberAxis)chart.getXAxis()).setTickUnit(1);
    chart.getXAxis().setAutoRanging(false);
    return chart;
  }

  private void drawLinearGameChart() {
    try {
      int allRounds = beerGame.getCurrentRound();
      List<BusinessWithState> states = new ArrayList<>();
      for (int i = 0; i < allRounds; i++) {
        List<BusinessRoundState> beerGameTurn = postGameProcessor.getBeerGameTurn(i);
        int turnNumber = i;
        ArrayList<BusinessWithState> businessStates = beerGameTurn.stream()
            .map(turn -> new BusinessWithState(turn.getBusinessUuid(), turn, turnNumber))
            .collect(Collectors.toCollection(ArrayList::new));
        states.addAll(businessStates);
      }
      beerGame.getBusinesses().stream().forEach(b -> {
        List<BusinessWithState> businessStates = states.stream()
            .filter(s -> s.getUuid().equals(b.getUUID())).collect(
                Collectors.toCollection(ArrayList::new));
        drawBusinessLine(b, businessStates);
      });
    } catch (PostGameProcessorException e) {
      logger.error(e.getMessage(), e);
    }

  }

  private void drawBusinessLine(Business business, List<BusinessWithState> states) {
      XYChart.Series<Number, Number> series = new XYChart.Series();
      series.setName(getLevelTranslation(business));
    states.forEach(state ->
        series.getData().add(new XYChart.Data<>(state.getRound() + 1,
            state.getState().getStock() - state.getState().getOpenOrders()))
    );
      linearGameChart.getData().add(series);
  }

  private void backButtonClicked(ActionEvent event) {
    switchScene(evaluateGameSelectSceneCreator, event, new EvaluateGameSelectViewModel());
  }

  private void businessSpecificChartButtonClicked(ActionEvent actionEvent) {
    BusinessGraphsViewModel businessGraphsViewModel = new BusinessGraphsViewModel(
        viewModel.getPlayedGame());
    switchScene(businessGraphsSceneCreator, actionEvent, businessGraphsViewModel);
  }

}
