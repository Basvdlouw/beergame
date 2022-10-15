package nl.ica.asd.frontend.gui.playerlobby;

import java.io.IOException;
import java.net.URL;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;
import javax.inject.Inject;
import javax.inject.Named;
import nl.ica.asd.frontend.gui.playturn.PlayTurnViewModel;
import nl.ica.asd.frontend.gui.scenecreator.SceneController;
import nl.ica.asd.frontend.gui.scenecreator.SceneCreator;
import nl.ica.asd.frontend.gui.service.dto.AvailableBusiness;
import nl.ica.asd.frontend.gui.service.dto.ConnectedPlayerWithBusiness;
import nl.ica.asd.logic.domain.Business;
import nl.ica.asd.logic.domain.BusinessType;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.logic.domain.builders.PlayerBuilder;
import nl.ica.asd.logic.gamemanager.DefaultGameManager;
import nl.ica.asd.logic.gamemanager.GameManager;
import nl.ica.asd.logic.watchers.NextRoundStartedWatcher;
import nl.ica.asd.logic.watchers.events.Event;
import nl.ica.asd.logic.watchers.events.NextRoundStartedEvent;
import nl.ica.asd.network.communication.interfaces.PublicIPAddress;
import nl.ica.asd.network.communication.interfaces.SendGameStateUpdate;
import nl.ica.asd.network.communication.interfaces.SendPlayerListUpdate;
import nl.ica.asd.network.failurehandler.LeaderElection;
import nl.ica.asd.network.failurehandler.exceptions.NoLeaderException;
import nl.ica.asd.network.playerlobby.ConnectedPlayers;
import nl.ica.asd.network.playerlobby.PlayerBusiness;
import nl.ica.asd.storage.applicationdatastorage.ApplicationDataAccess;
import nl.ica.asd.storage.exception.UnknownException;
import nl.ica.asd.storage.exception.UnknownGameException;
import nl.ica.asd.storage.gamedatastorage.GameDataAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerLobbyController extends SceneController implements Initializable,
    NextRoundStartedWatcher {

  private static final Logger logger = LoggerFactory.getLogger(PlayerLobbyController.class);

  private BusinessType chosenBusinessType = BusinessType.NOT_SET;

  @Inject
  private @Named("MainMenuSceneCreator")
  SceneCreator mainMenuSceneCreator;

  @Inject
  private @Named("PlayTurnSceneCreator")
  SceneCreator playTurnSceneCreator;

  @Inject
  private @Named("GameManager")
  GameManager gameManager;

  @Inject
  private @Named("GameDataAccess")
  GameDataAccess gameDataAccess;

  @Inject
  private ConnectedPlayers connectedPlayers;

  @Inject
  private LeaderElection leaderElection;

  @Inject
  private PlayerBusiness playerBusiness;

  @Inject
  private PublicIPAddress publicIPAddress;

  @Inject
  private ApplicationDataAccess appDataAccess;

  @Inject
  private SendPlayerListUpdate sendplayerlistupdate;

  @Inject
  private SendGameStateUpdate sendGameStateUpdate;

  @Inject
  private PlayerLobbyViewModel playerLobbyViewModel;

  @FXML
  private Button chooseFactoryLevelGameButton;

  @FXML
  private Button chooseRegionalWarehouseLevelGameButton;

  @FXML
  private Button chooseWholesaleLevelGameButton;

  @FXML
  private Button chooseRetailerLevelButton;

  @FXML
  private Button confirmLevelButton;

  @FXML
  private Button copyIPButton;

  @FXML
  private Button startGameButton;

  @FXML
  private TextField ipAdressField;

  @FXML
  private TableView<ConnectedPlayerWithBusiness> connectedPlayerWithBusinesses;

  @FXML
  private TableView<AvailableBusiness> chosenBusinesses;

  @FXML
  private TableColumn<ConnectedPlayerWithBusiness, String> playerName;

  @FXML
  private TableColumn<ConnectedPlayerWithBusiness, String> playerUUID;

  @FXML
  private TableColumn<AvailableBusiness, String> businessUUID;

  @FXML
  private TableColumn<AvailableBusiness, String> businessAmount;

  @FXML
  private TableColumn<AvailableBusiness, String> businessAvailable;

  @FXML
  private Label errorMessage;

  private Map<BusinessType, Button> levelButtonMap;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    bindProperties();
    setColumns();
    try {
      setVariables();
    } catch (IOException e) {
      e.printStackTrace();
    }

    levelButtonMap = new EnumMap<>(BusinessType.class);
    levelButtonMap.put(BusinessType.FACTORY, chooseFactoryLevelGameButton);
    levelButtonMap.put(BusinessType.REGIONAL_WAREHOUSE, chooseRegionalWarehouseLevelGameButton);
    levelButtonMap.put(BusinessType.WHOLESALE, chooseWholesaleLevelGameButton);
    levelButtonMap.put(BusinessType.RETAIL, chooseRetailerLevelButton);

    this.subscribeAll();

    guiService.addObserver(new PlayerLobbyObserver(playerLobbyViewModel));

    updateLevelButtonStyles();
  }

  private void bindProperties() {
    //bindProperties ip address & error
    ipAdressField.textProperty().bind(playerLobbyViewModel.getIpAddressProperty());
    errorMessage.textProperty().bind(playerLobbyViewModel.getErrorMessageProperty());
    //bindProperties tables
    connectedPlayerWithBusinesses.itemsProperty()
        .bind(playerLobbyViewModel.getConnectedPlayersWithBusiness());
    chosenBusinesses.itemsProperty().bind(playerLobbyViewModel.getAvailableBusiness());
    playerLobbyViewModel.getAvailableBusiness().addListener(ab -> {
      disableConfirmIfUnavailable();
      updateLevelButtonStyles();
    });
  }

  private void setColumns() {
    playerName.setCellValueFactory(new PropertyValueFactory<>("username"));
    playerUUID.setCellValueFactory(new PropertyValueFactory<>("businessName"));
    businessUUID.setCellValueFactory(new PropertyValueFactory<>("businessName"));
    businessAmount.setCellValueFactory(new PropertyValueFactory<>("available"));
    businessAvailable.setCellValueFactory(new PropertyValueFactory<>("ready"));
  }

  private void setVariables() throws IOException {
    startGameButton.setVisible(false);
    Player leader = null;
    try {
      leader = leaderElection.getLeader();
    } catch (NoLeaderException e) {
      logger.error("There is no leader found.", e);
    }
    if (leader == connectedPlayers.getPlayerForSelf()) {
      startGameButton.setVisible(true);
    } else {
      startGameButton.setVisible(false);
    }

    try {
      playerLobbyViewModel.setIpAddressProperty(connectedPlayers.getPlayerForSelf().getIP());
    } catch (IOException exception) {
      logger
          .error("Failed to get Player for myself and will now return to main menu",
              exception.getCause());
    }

    try {
      playerLobbyViewModel
          .setConnectedPlayersWithBusiness(playerBusiness.getConnectedPlayersWithBusiness());
      playerLobbyViewModel.setAvailableBusiness(playerBusiness.getAvailableBusinesses());
    } catch (UnknownGameException exception) {
      logger.error(
          "Unable to fetch currently connected players with businesses and available businesses on initialization.",
          exception);
    }
  }

  @FXML
  private void handleChooseFactoryLevelGameButton(ActionEvent action) {
    handleLevelChoice(BusinessType.FACTORY);
  }

  @FXML
  private void handleChooseRegionalWarehouseLevelGameButton(ActionEvent action) {
    handleLevelChoice(BusinessType.REGIONAL_WAREHOUSE);
  }

  @FXML
  private void handleChooseWholesaleLevelGameButton(ActionEvent action) {
    handleLevelChoice(BusinessType.WHOLESALE);
  }

  @FXML
  private void handleChooseRetailerLevelButton(ActionEvent action) {
    handleLevelChoice(BusinessType.RETAIL);
  }

  @FXML
  private void handleCopyButton(ActionEvent action) {
    final Clipboard clipboard = Clipboard.getSystemClipboard();
    final ClipboardContent content = new ClipboardContent();
    content.putString(playerLobbyViewModel.getIpAddressProperty().getValue());
    clipboard.setContent(content);
  }

  @FXML
  private void handleConfirmButton(ActionEvent action) {
    if (!chosenBusinessType.equals(BusinessType.NOT_SET)) {
      handleLevelConfirmation(chosenBusinessType);
    }
  }

  @FXML
  private void handleStartGameButton(ActionEvent action) {
    if (connectedPlayers.allPlayersHaveABusiness()) {
      try {
        sendGameStateUpdate.sendGameStateUpdate(gameDataAccess.getBeerGame().getGameState());
      } catch (UnknownGameException e) {
        logger.error(e.getMessage(), e);
      }
    }
  }

  private void handleLevelChoice(BusinessType businessType) {
    chosenBusinessType = businessType;
    confirmLevelButton.setVisible(true);
    updateLevelButtonStyles();
  }

  private void handleLevelConfirmation(BusinessType chosenLevel) {

    try {
      Set<Player> players = new HashSet<>();
      players.add(PlayerBuilder.fromBasePlayer(connectedPlayers.getPlayerForSelf())
          .withBusinessType(chosenLevel)
          .build());
      sendplayerlistupdate.sendPlayerListUpdate(players);
      confirmLevelButton.setVisible(false);

    } catch (IOException e) {
      logger.error(e.getMessage());
    }
  }

  @Override
  public void onNextRoundStarted(NextRoundStartedEvent event) {
    try {
      final String username = appDataAccess.getUsername();
      final Business business = event.getBusinesses().stream()
          .filter(tmpBusiness -> tmpBusiness.getPlayer() != null && tmpBusiness.getPlayer()
              .getUsername().equals(username)).findFirst()
          .orElseThrow(
              () -> new UnknownException("Username changed during game creation"));

      PlayTurnViewModel playTurnViewModel = new PlayTurnViewModel(business);
      switchScene(playTurnSceneCreator, (Stage) errorMessage.getScene().getWindow(),
          playTurnViewModel);
    } catch (UnknownException e) {
      logger.error(e.getMessage());
    }
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
    onNextRoundStarted((NextRoundStartedEvent) event);
  }

  private void updateLevelButtonStyles() {
    BusinessType confirmedBusiness = getConfirmedBusinessForSelf();
    levelButtonMap.forEach((businessType, button) -> {
      String style = "";
      if (businessType == confirmedBusiness) {
        style = "-fx-background-color: linear-gradient(to bottom, #7ee0a6, #00ff7f)";
      } else if (businessType == chosenBusinessType) {
        style = "-fx-background-color: linear-gradient(to bottom, #7ea6e0, #007fff)";
      }
      playerLobbyViewModel.getAvailableBusiness()
          .getValue()
          .forEach(business -> levelButtonMap.get(business.getBusinessType())
              .setDisable(business.getTakenBusinesses() >= business.getTotalBusinesses()));
      button.setStyle(style);
    });
  }

  private BusinessType getConfirmedBusinessForSelf() {
    try {
      Player player = connectedPlayers.getPlayerForSelf();
      return player.getBusinessType();
    } catch (IOException e) {
      return BusinessType.NOT_SET;
    }
  }

  private void disableConfirmIfUnavailable() {
    if (isLevelFull(chosenBusinessType)) {
      chosenBusinessType = BusinessType.NOT_SET;
      confirmLevelButton.setVisible(false);
    }
  }

  private boolean isLevelFull(BusinessType businessType) {
    return playerLobbyViewModel.getAvailableBusiness()
        .getValue()
        .stream()
        .anyMatch(ab -> ab.getBusinessType() == businessType &&
            ab.getTakenBusinesses() >= ab.getTotalBusinesses());
  }
}