package nl.ica.asd.frontend.gui.playturn;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Timer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Named;
import nl.ica.asd.agenthandler.AgentHandler;
import nl.ica.asd.agenthandler.exceptions.AgentException;
import nl.ica.asd.frontend.gui.agentconfiguration.AgentConfigurationViewModel;
import nl.ica.asd.frontend.gui.converters.InputRestrictor;
import nl.ica.asd.frontend.gui.converters.PlayerLevelStringConverter;
import nl.ica.asd.frontend.gui.evaluate.graphs.linear.GraphLinearGameViewModel;
import nl.ica.asd.frontend.gui.playturn.steplevel.StepLevel;
import nl.ica.asd.frontend.gui.playturn.steplevel.StepLevelObserver;
import nl.ica.asd.frontend.gui.playturn.steplevel.StepLevelViewModel;
import nl.ica.asd.frontend.gui.scenecreator.SceneController;
import nl.ica.asd.frontend.gui.scenecreator.SceneCreator;
import nl.ica.asd.frontend.gui.scenecreator.SceneLoaderInitializable;
import nl.ica.asd.logic.domain.Action;
import nl.ica.asd.logic.domain.BeerGame;
import nl.ica.asd.logic.domain.Business;
import nl.ica.asd.logic.domain.GameVisibilityType;
import nl.ica.asd.logic.domain.PlayerStatus;
import nl.ica.asd.logic.domain.actions.OrderAction;
import nl.ica.asd.logic.domain.builders.PlayerBuilder;
import nl.ica.asd.logic.gamemanager.DefaultGameManager;
import nl.ica.asd.logic.gamemanager.GameManager;
import nl.ica.asd.logic.postgameprocessor.dto.PlayedGame;
import nl.ica.asd.logic.watchers.BusinessrulesChangedWatcher;
import nl.ica.asd.logic.watchers.GameFinishedWatcher;
import nl.ica.asd.logic.watchers.NextRoundStartedWatcher;
import nl.ica.asd.logic.watchers.OrderPlacedWatcher;
import nl.ica.asd.logic.watchers.events.BusinessrulesChangedEvent;
import nl.ica.asd.logic.watchers.events.Event;
import nl.ica.asd.logic.watchers.events.GameFinishedEvent;
import nl.ica.asd.logic.watchers.events.NextRoundStartedEvent;
import nl.ica.asd.logic.watchers.events.OrderPlacedEvent;
import nl.ica.asd.network.communication.interfaces.GameConnection;
import nl.ica.asd.network.communication.interfaces.SendGameStateUpdate;
import nl.ica.asd.network.communication.interfaces.SendPlayerListUpdate;
import nl.ica.asd.network.playerlobby.PlayerLobby;
import nl.ica.asd.storage.exception.UnknownGameException;
import nl.ica.asd.storage.gamedatastorage.GameDataAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Default
public class PlayTurnController extends SceneController<PlayTurnViewModel> implements
    SceneLoaderInitializable, NextRoundStartedWatcher, GameFinishedWatcher,
    BusinessrulesChangedWatcher, OrderPlacedWatcher, Initializable {

  private static final Logger logger = LoggerFactory.getLogger(PlayTurnController.class);

  @FXML
  private Pane playerPane;

  @FXML
  private Pane supplierPane;

  @FXML
  private Pane clientPane;

  @FXML
  private Button placeOrderButton;

  @FXML
  private TextField placeOrderField;

  @FXML
  private Label waitingForOtherPlayersLabel;

  @FXML
  private Label invalidOrderAmountLabel;

  @FXML
  private Label playerLevel;

  @FXML
  private Label senderLevel;

  @FXML
  private Label recipientLevel;

  @FXML
  private Label timeleftLabel;

  @FXML
  private Button configureAgentButton;

  @FXML
  private Label remainingTimeCounter;

  @FXML
  private Label currentRoundCounter;

  @FXML
  private Button toggleAgentButton;

  @FXML
  private Button leaveButton;

  @FXML
  private ListView<String> playerListView;

  @Inject
  private @Named("StepLevelSceneCreator")
  SceneCreator stepLevelSceneCreator;

  @Inject
  private @Named("MainMenuSceneCreator")
  SceneCreator mainMenuSceneCreator;

  @Inject
  private @Named("AgentConfigurationSceneCreator")
  SceneCreator agentConfigurationSceneCreator;

  @Inject
  private GameManager gameManager;

  @Inject
  private GameDataAccess gameDataAccess;

  @Inject
  private StepLevelViewModel incomingGoodsViewModel;

  @Inject
  private StepLevelViewModel stockViewModel;

  @Inject
  private StepLevelViewModel openOrdersViewModel;

  @Inject
  private StepLevelViewModel budgetViewModel;

  @Inject
  private StepLevelViewModel outgoingGoodsViewModel;

  @Inject
  private StepLevelViewModel incomingOrdersViewModel;

  @Inject
  private PlayerLobby playerLobby;

  @Inject
  private GameConnection gameConnection;

  @Inject
  private AgentHandler agentHandler;

  @Inject
  private SendGameStateUpdate sendGameStateUpdate;

  @Inject
  private SendPlayerListUpdate sendPlayerListUpdate;

  @Inject
  private @Named("EvaluateLinearGameSceneCreator")
  SceneCreator evaluateLinearGame;

  @Inject
  private @Named("GraphLinearGameSceneCreator")
  SceneCreator graphLinearGameSceneCreator;

  @Inject
  private InputRestrictor inputRestrictor;

  private ResourceBundle resources;

  private Timer timer;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    this.subscribeAll();
  }

  @Override
  public void sceneLoaderInitialize(URL location, ResourceBundle resources) {
    this.resources = resources;

    viewModel.setRound(1);

    leaveButton.setOnAction(event -> {

      //TODO delete all set agents etc. gameManager.
      try {
        gameConnection.leaveGame(playerLobby.getPlayerForSelf());
      } catch (IOException e) {
        logger.error(e.getMessage(), e);
      }

      switchScene(mainMenuSceneCreator, (Stage) clientPane.getScene().getWindow());
    });

    senderLevel.textProperty().bindBidirectional(viewModel.getSenderLevelProperty(),
        new PlayerLevelStringConverter(resources));
    playerLevel.textProperty().bindBidirectional(viewModel.getPlayerLevelProperty(),
        new PlayerLevelStringConverter(resources));
    recipientLevel.textProperty().bindBidirectional(viewModel.getRecipientLevelProperty(),
        new PlayerLevelStringConverter(resources));

    placeOrderField.styleProperty().bind(viewModel.getPlaceOrderStyleProperty());

    waitingForOtherPlayersLabel.visibleProperty()
        .bind(viewModel.getWaitingForOtherPlayersDisplayProperty());

    invalidOrderAmountLabel.visibleProperty()
        .bind(viewModel.getInvalidOrderAmountDisplayProperty());

    currentRoundCounter.textProperty()
        .bindBidirectional(viewModel.getRoundProperty(),
            new NumberStringConverter(resources.getLocale()));

    remainingTimeCounter.textProperty()
        .bind(viewModel.getTimerProperty().asString());

    placeOrderField.textProperty().bindBidirectional(viewModel.getPlaceOrderProperty());
    placeOrderField.disableProperty().bind(viewModel.getPlaceOrderDisabledProperty());
    placeOrderButton.disableProperty().bind(viewModel.getPlaceOrderDisabledProperty());

    inputRestrictor.restrictInput(placeOrderField, inputRestrictor.getONLYNUMERIC());

    incomingGoodsViewModel.setStep(StepLevel.INCOMINGGOODS);
    stockViewModel.setStep(StepLevel.STOCK);
    openOrdersViewModel.setStep(StepLevel.OPENORDERS);
    budgetViewModel.setStep(StepLevel.BUDGET);
    outgoingGoodsViewModel.setStep(StepLevel.OUTGOINGGOODS);
    incomingOrdersViewModel.setStep(StepLevel.INCOMINGORDERS);

    placeStepLevelInPane(supplierPane, incomingGoodsViewModel, 50, 0);
    placeStepLevelInPane(playerPane, stockViewModel, 100, 0);
    placeStepLevelInPane(playerPane, openOrdersViewModel, 100, 150);
    placeStepLevelInPane(playerPane, budgetViewModel, 100, 300);
    placeStepLevelInPane(clientPane, outgoingGoodsViewModel, 50, 0);
    placeStepLevelInPane(clientPane, incomingOrdersViewModel, 50, 150);

    Arrays.asList(
        new PlayTurnObserver(viewModel),
        new StepLevelObserver(incomingGoodsViewModel),
        new StepLevelObserver(stockViewModel),
        new StepLevelObserver(openOrdersViewModel),
        new StepLevelObserver(budgetViewModel),
        new StepLevelObserver(outgoingGoodsViewModel),
        new StepLevelObserver(incomingOrdersViewModel))
        .forEach(observer -> guiService.addObserver(observer));

    setupTimer();
    toggleAgentButton.textProperty().bind(viewModel.getToggleAgentButtonProperty());
    try {
      if (playerLobby.getPlayerForSelf().getPlayerStatus() != PlayerStatus.AGENT_ACTIVATED) {
        deactivateAgent();
      }
    } catch (IOException e) {
      logger.error("An error occured during retreiving playerstatus.");
    }

    guiService.updatePlayTurnState(gameManager.getPlayTurnStateForRound(1));

    updatePlayerList();
  }

  @Override
  protected void unload() {
    super.unload();
    timer.cancel();
  }

  private void setupTimer() {
    try {
      timer = new Timer();
      timer.scheduleAtFixedRate(viewModel, 1000L, 1000L);
      if (gameDataAccess.getBeerGame().getGameConfiguration().getTimeLimitPerRound() == 0) {
        remainingTimeCounter.setVisible(false);
        timeleftLabel.setVisible(false);
      } else {
        viewModel.setTimer(
            gameDataAccess.getBeerGame().getGameConfiguration().getTimeLimitPerRound() * 60);
        viewModel.getTimerProperty().addListener((observable, oldValue, newValue) -> {
          if (newValue.intValue() == 0 && !placeOrderButton.isDisabled()) {
            activateAgent();
          }
        });
      }
    } catch (UnknownGameException e) {
      logger.error(e.getMessage(), e);
    }

  }

  private void deactivateAgent() {
    try {
      sendPlayerListUpdate.sendPlayerListUpdate(
          Collections.singleton(PlayerBuilder.fromBasePlayer(playerLobby.getPlayerForSelf())
              .withPlayerStatus(PlayerStatus.CONNECTED_AND_ACTIVE).build()));
      viewModel.setToggleAgentProperty(resources.getString("playturn.activate_agent"));
      toggleAgentButton.setOnAction(event -> activateAgent());
      if (!viewModel.getWaitingForOtherPlayersDisplayProperty().get()) {
        viewModel.getPlaceOrderDisabledProperty().set(false);
      }
    } catch (IOException e) {
      logger.error("Unexpected exception while deactivating agent.", e);
    }
  }

  private void activateAgent() {
    try {
      sendPlayerListUpdate.sendPlayerListUpdate(
          Collections.singleton(PlayerBuilder.fromBasePlayer(playerLobby.getPlayerForSelf())
              .withPlayerStatus(PlayerStatus.AGENT_ACTIVATED).build()));
      updateOrderField();
    } catch (IOException | AgentException e) {
      logger.error("Unexpected exception while activating agent.", e);
    }
  }

  private void updateOrderFieldIfAgent() throws IOException, AgentException {
    if (playerLobby.getPlayerForSelf().getPlayerStatus() == PlayerStatus.AGENT_ACTIVATED) {
      updateOrderField();
    } else {
      viewModel.getPlaceOrderDisabledProperty().set(false);
      viewModel.getWaitingForOtherPlayersDisplayProperty().set(false);
    }
  }

  private void updateOrderField() throws AgentException {
    viewModel.setToggleAgentProperty(resources.getString("playturn.deactivate_agent"));
    toggleAgentButton.setOnAction(event -> deactivateAgent());
    viewModel.getPlaceOrderDisabledProperty().set(true);
    viewModel.getPlaceOrderProperty().set(
        String.valueOf(agentHandler.executeAgent(viewModel.getBusiness(), viewModel.getRound())));
  }

  @FXML
  private void handlePlaceOrderButton() {
    if (viewModel.isPlaceOrderAmountValid()) {
      viewModel.doWaitForNextRound(true);
      toggleAgentButton.setDisable(true);
      gameManager.placeOrder(viewModel.getBusinessUsername(), viewModel.getPlaceOrderAmount());
    }
  }

  @FXML
  private void handlePlaceOrderField() {
    viewModel.doPlaceOrderErrorIndicator(false);
  }

  @FXML
  private void handleConfigureAgentButton(ActionEvent action) {
    AgentConfigurationViewModel agentConfigurationViewModel = new AgentConfigurationViewModel(
        viewModel.getBusiness());

    final Stage agentStage = new Stage();
    agentStage.setScene(agentConfigurationSceneCreator.create(agentConfigurationViewModel));
    agentStage.initModality(Modality.APPLICATION_MODAL);
    agentStage.show();
  }

  private void placeStepLevelInPane(Pane pane,
      StepLevelViewModel viewModel, int offsetX, int offsetY) {
    Scene stepLevelScene = stepLevelSceneCreator.create(viewModel);

    Pane stepLevelNode = (Pane) stepLevelScene.getRoot();

    stepLevelNode.setLayoutX(offsetX);
    stepLevelNode.setLayoutY(offsetY);

    pane.getChildren().add(stepLevelNode);
  }

  @Override
  public void subscribeAll() {
    DefaultGameManager.subscribe(this);
  }

  @Override
  public void unSubscribeAll() {
    DefaultGameManager.unSubscribe(this);
  }

  @Override
  public <E extends Event> void onEvent(E event) {
    if (event instanceof BusinessrulesChangedEvent) {
      onBusinessrulesChanged((BusinessrulesChangedEvent) event);
    } else if (event instanceof NextRoundStartedEvent) {
      onNextRoundStarted((NextRoundStartedEvent) event);
    } else if (event instanceof GameFinishedEvent) {
      onGameFinished((GameFinishedEvent) event);
    } else if (event instanceof OrderPlacedEvent) {
      onOrderPlaced((OrderPlacedEvent) event);
    }
  }

  public void onNextRoundStarted(NextRoundStartedEvent event) {
    guiService.updatePlayTurnState(event.getPlayTurnState());
    toggleAgentButton.setDisable(false);
    try {
      updateOrderFieldIfAgent();
    } catch (AgentException | IOException e) {
      logger.error(e.getMessage(), e);
    }
  }

  public void onBusinessrulesChanged(BusinessrulesChangedEvent event) {
    try {
      updateOrderFieldIfAgent();
    } catch (IOException | AgentException e) {
      logger.error("Error during updating order field.");
    }
  }

  @Override
  public void onGameFinished(GameFinishedEvent event) {
    final BeerGame beerGame = event.getBeerGame();
    final PlayedGame selectedItem = new PlayedGame(beerGame.getName(), beerGame.getDateTime());
    final GraphLinearGameViewModel graphLinearGameViewModel = new GraphLinearGameViewModel(
        selectedItem);
    switchScene(graphLinearGameSceneCreator, (Stage) currentRoundCounter.getScene().getWindow(),
        graphLinearGameViewModel);
  }

  @Override
  public void onOrderPlaced(OrderPlacedEvent event) {
    updatePlayerList();
  }

  private void updatePlayerList() {
    playerListView.getItems().clear();

    try {
      GameVisibilityType visibility = gameDataAccess.getBeerGame().getGameConfiguration()
          .getGameVisibilityType();
      BeerGame beerGame = gameDataAccess.getBeerGame();
      int round = beerGame.getCurrentRound();

      beerGame.getBusinesses().stream().sorted().forEachOrdered(business -> {
        String item;
        String name = business.getBusinessType().getIcon() + " " + (business.getPlayer() != null ? business.getPlayer().getUsername()
            : business.getBusinessType().getBusinessName());
        Optional<Integer> order = getOrderAmount(business, round);
        if (order.isPresent()) {
          if (visibility == GameVisibilityType.VISIBLE) {
            item = name + ": " + order.get();
          } else {
            item = name + ": " + resources.getString("playturn.ready") + "!";
          }
        } else {
          item = name + ": " + resources.getString("playturn.waiting_for_order");
        }

        Optional<Integer> previousOrder = getOrderAmount(business, round - 1);
        if (visibility == GameVisibilityType.VISIBLE && previousOrder.isPresent()) {
          item += " (" + resources.getString("playturn.previous_round") + ": " + previousOrder.get()
              + ")";
        }
        playerListView.getItems().add(item);
      });
    } catch (UnknownGameException e) {
      logger.error("Unexpected exception when updating player list.", e);
    }
  }

  private Optional<Integer> getOrderAmount(Business business, int round) {
    List<Action> actions = business.getActionsPerRound().get(round);
    if (actions != null) {
      return actions.stream()
          .filter(action -> action instanceof OrderAction)
          .filter(action -> business.getUUID().equals(action.getSender()))
          .findFirst()
          .map(Action::getAmount);
    } else {
      return Optional.empty();
    }
  }
}