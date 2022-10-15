package nl.ica.asd.frontend.gui.evaluate.businessgraphs;

import java.net.URL;
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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javax.inject.Inject;
import javax.inject.Named;
import nl.ica.asd.frontend.gui.converters.PlayerLevelStringConverter;
import nl.ica.asd.frontend.gui.evaluate.gameselect.EvaluateGameSelectViewModel;
import nl.ica.asd.frontend.gui.evaluate.graphs.linear.GraphLinearGameViewModel;
import nl.ica.asd.frontend.gui.scenecreator.SceneController;
import nl.ica.asd.frontend.gui.scenecreator.SceneCreator;
import nl.ica.asd.frontend.gui.scenecreator.SceneLoaderInitializable;
import nl.ica.asd.frontend.gui.service.dto.PlayerBusinessUUID;
import nl.ica.asd.logic.domain.BeerGame;
import nl.ica.asd.logic.domain.Business;
import nl.ica.asd.logic.postgameprocessor.PostGameProcessor;
import nl.ica.asd.logic.postgameprocessor.PostGameProcessorException;
import nl.ica.asd.logic.postgameprocessor.dto.BusinessRoundState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BusinessGraphsController extends SceneController<BusinessGraphsViewModel> implements
    SceneLoaderInitializable {

  @Inject
  private @Named("BusinessChartsSceneCreator")
  SceneCreator chartsCreator;

  @Inject
  private PostGameProcessor postGameProcessor;

  @Inject
  private @Named("GraphLinearGameSceneCreator")
  SceneCreator graphLinearGameSceneCreator;

  @Inject
  private @Named("EvaluateGameSelectSceneCreator")
  SceneCreator evaluateGameSelectSceneCreator;

  private PlayerLevelStringConverter levelConverter;

  @FXML
  private AnchorPane chartPane;

  @FXML
  private TableView<PlayerBusinessUUID> playerTable;

  @FXML
  private TableColumn<PlayerBusinessUUID, String> username;

  @FXML
  private TableColumn<PlayerBusinessUUID, String> playerLevel;

  @FXML
  private ScrollPane scrollPane;

  @FXML
  private Button gameSelectButton;

  @FXML
  private Button bullwhipGraph;

  private Logger logger = LoggerFactory.getLogger(BusinessGraphsController.class);

  private LineChart<Number, Number> budgetChart;

  private LineChart<Number, Number> stockChart;

  private BeerGame beerGame;

  private ResourceBundle resources;

  @Override
  public void sceneLoaderInitialize(URL location, ResourceBundle resources) {
    this.resources = resources;
    levelConverter = new PlayerLevelStringConverter(resources);
    try {
      beerGame = postGameProcessor.getPlayedBeerGame(viewModel.getPlayedGame());
    } catch (PostGameProcessorException e) {
      logger.error(e.getMessage());
    }
    playerTable.itemsProperty().bind(viewModel.getPlayerListProperty());
    chartPane.prefWidthProperty().bind(scrollPane.widthProperty());

    username.setCellValueFactory(new PropertyValueFactory<>("username"));
    playerLevel.setCellValueFactory(new PropertyValueFactory<>("businessType"));

    addBudgetChart(resources);
    addStockChart(resources);

    loadPlayers(beerGame.getBusinesses());

    playerTable.getSelectionModel().selectedItemProperty()
        .addListener((obs, oldSelection, newSelection) -> updateGraphs(newSelection));
    gameSelectButton.setOnAction(this::gameSelectButtonClicked);
    bullwhipGraph.setOnAction(this::bullwhipGraph);

  }

  private void bullwhipGraph(ActionEvent actionEvent) {
    try {
      GraphLinearGameViewModel graphLinearGameViewModel = new GraphLinearGameViewModel(
          viewModel.getPlayedGame());
      switchScene(graphLinearGameSceneCreator, actionEvent, graphLinearGameViewModel);
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  private void gameSelectButtonClicked(ActionEvent actionEvent) {
    switchScene(evaluateGameSelectSceneCreator, actionEvent, new EvaluateGameSelectViewModel());
  }

  private void updateGraphs(PlayerBusinessUUID newSelection) {
    try {
      Business selectedBusiness = postGameProcessor
          .getBeerGameBusiness(newSelection.getUUID());
      drawBudgetChart(selectedBusiness);
      drawStockChart(selectedBusiness);
    } catch (Exception e) {
      logger.error(e.getMessage());
    }

  }

  private void loadPlayers(List<Business> businesses) {
    List<PlayerBusinessUUID> tableItems = businesses.stream()
        .map(b -> new PlayerBusinessUUID(getPlayerUsername(b), getLevelTranslation(b),
            b.getUUID()))
        .collect(Collectors.toList());
    viewModel.setPlayerListProperty(tableItems);

    if (!tableItems.isEmpty()) {
      updateGraphs(tableItems.stream().findFirst().orElseGet(null));
    }
  }

  private String getPlayerUsername(Business business) {
    if (business.getPlayer() == null) {
      return business.getBusinessType().toString();
    } else {
      return business.getPlayer().getUsername();
    }
  }

  private String getLevelTranslation(Business b) {
    return levelConverter.fromBusiness(b);
  }


  private void addBudgetChart(ResourceBundle resources) {
    budgetChart = loadChartTemplate();
    budgetChart.getXAxis().setLabel(resources.getString("businessgraph.weeks"));
    budgetChart.getYAxis().setLabel(resources.getString("businessgraph.budget"));
    chartPane.getChildren().add(budgetChart);
    setChartAnchors(budgetChart, 0.0);

  }

  private void addStockChart(ResourceBundle resources) {
    stockChart = loadChartTemplate();
    stockChart.getXAxis().setLabel(resources.getString("businessgraph.weeks"));
    stockChart.getYAxis().setLabel(resources.getString("businessgraph.stockAndIncomingOrders"));
    stockChart.setLegendVisible(true);
    chartPane.getChildren().add(stockChart);
    setChartAnchors(stockChart, 0.0);
  }

  private void setChartAnchors(LineChart<Number, Number> c, double margin) {
    AnchorPane.setLeftAnchor(c, margin);
    AnchorPane.setRightAnchor(c, margin);
  }

  private void drawBudgetChart(Business business) {

    XYChart.Series<Number, Number> series = new XYChart.Series();
    try {
      int maxRounds = beerGame
          .getCurrentRound(); //Game is finished so the currentRound set is the last one.
      for (int i = 0; i < maxRounds; i++) {
        BusinessRoundState state = postGameProcessor.getBeerGameTurn(i).stream()
            .filter(s -> s.getBusinessUuid().equals(business.getUUID()))
            .findFirst()
            .orElseGet(null);
        series.getData().add(new XYChart.Data<>(i+1, state.getBudget()));
      }
      //Add the new data
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
    budgetChart.getData().clear(); //Clear the graph
    budgetChart.getData().add(series);
  }

  private void drawStockChart(Business business) {

    XYChart.Series<Number, Number> stockSeries = new XYChart.Series();
    XYChart.Series<Number, Number> incOrdersSeries = new XYChart.Series();
    XYChart.Series<Number, Number> incGoodsSeries = new XYChart.Series();
    stockSeries.setName(resources.getString("businessgraph.stock"));
    incOrdersSeries.setName(resources.getString("steplevel.incoming_orders"));
    incGoodsSeries.setName(resources.getString("steplevel.incoming_goods"));
    try {
      int maxRounds = beerGame
          .getCurrentRound(); //Game is finished so the currentRound set is the last one.
      for (int i = 0; i < maxRounds; i++) {
        BusinessRoundState state = postGameProcessor.getBeerGameTurn(i).stream()
            .filter(s -> s.getBusinessUuid().equals(business.getUUID()))
            .findFirst()
            .orElseGet(null);
        stockSeries.getData().add(new XYChart.Data<>(i+1, state.getStock() - state.getOpenOrders()));
        incOrdersSeries.getData().add(new XYChart.Data<>(i+1, state.getIncomingOrders()));
        incGoodsSeries.getData().add(new XYChart.Data<>(i+1, state.getIncomingGoods()));
      }

    } catch (Exception e) {
      logger.error(e.getMessage());
    }
    stockChart.getData().clear(); //Clear the graph
    stockChart.getData().add(stockSeries); //Add the new data
    stockChart.getData().add(incOrdersSeries); //Add the new data
    stockChart.getData().add(incGoodsSeries); //Add the new data
  }

  private LineChart<Number, Number> loadChartTemplate() {
    LineChart<Number, Number> chart = (LineChart<Number, Number>) chartsCreator.create().getRoot();
    ObservableList charts = chartPane.getChildren();
    AtomicReference<Double> yPosition = new AtomicReference<>((double) 0);
    charts.forEach(child -> {
      double height = yPosition.get();
      height += (((LineChart<Number, Number>) child).getPrefHeight());
      yPosition.set(height);
    });
    chart.setLayoutY(yPosition.get());
    chart.setLegendVisible(false);
    ((NumberAxis)chart.getXAxis()).forceZeroInRangeProperty().setValue(false);
    ((NumberAxis)chart.getXAxis()).setLowerBound(1);
    ((NumberAxis)chart.getXAxis()).setUpperBound(beerGame.getCurrentRound());
    ((NumberAxis)chart.getXAxis()).setTickUnit(1);
    chart.getXAxis().setAutoRanging(false);
    return chart;
  }
}

