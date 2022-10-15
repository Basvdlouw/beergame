package nl.ica.asd.network.playerlobby;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import nl.ica.asd.frontend.gui.service.GuiService;
import nl.ica.asd.frontend.gui.service.dto.AvailableBusiness;
import nl.ica.asd.frontend.gui.service.dto.ConnectedPlayerWithBusiness;
import nl.ica.asd.frontend.gui.service.dto.PlayerLobbyState;
import nl.ica.asd.logic.domain.BusinessType;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.logic.domain.PlayerStatus;
import nl.ica.asd.logic.domain.SupplyChainType;
import nl.ica.asd.logic.domain.builders.PlayerBuilder;
import nl.ica.asd.logic.gamemanager.GameManager;
import nl.ica.asd.network.communication.clientserver.Server;
import nl.ica.asd.network.communication.gameinitialisation.DefaultIPAddressProvider;
import nl.ica.asd.network.communication.interfaces.SendPlayerListUpdate;
import nl.ica.asd.network.failurehandler.leader.SendPlayerUpdateCallback;
import nl.ica.asd.network.playerlobby.exceptions.PlayerNotFoundException;
import nl.ica.asd.network.playerlobby.exceptions.UsernameAlreadyInUseException;
import nl.ica.asd.storage.applicationdatastorage.ApplicationDataAccess;
import nl.ica.asd.storage.domain.GameConfiguration;
import nl.ica.asd.storage.exception.UnknownGameException;
import nl.ica.asd.storage.gameconfig.GameConfig;
import nl.ica.asd.storage.gamedatastorage.GameDataAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("PlayerLobby")
@ApplicationScoped
public class PlayerLobby implements ConnectedPlayers, PlayerBusiness {

  private static final Logger logger = LoggerFactory.getLogger(PlayerLobby.class);

  @Inject
  private GameDataAccess gameDataAccess;
  @Inject
  private GameManager gameManager;
  @Inject
  private ApplicationDataAccess applicationDataAccess;
  @Inject
  private DefaultIPAddressProvider defaultIPAddressProvider;
  @Inject
  private GuiService guiService;
  @Inject
  private SendPlayerListUpdate sendPlayerListUpdate;

  private final Map<Player, Long> playerTimestamps;
  private Player self;

  //TODO remove this constructor when tests are fixed
  public PlayerLobby(
      GameManager gameManager,
      GameDataAccess gameDataAccess,
      ApplicationDataAccess applicationDataAccess,
      DefaultIPAddressProvider defaultIPAddressProvider,
      GuiService guiService
  ) {
    this.playerTimestamps = new HashMap<>();
    this.gameManager = gameManager;
    this.gameDataAccess = gameDataAccess;
    this.applicationDataAccess = applicationDataAccess;
    this.defaultIPAddressProvider = defaultIPAddressProvider;
    this.guiService = guiService;
  }

  @Inject
  public PlayerLobby() {
    this.playerTimestamps = new HashMap<>();
  }

  public Set<Player> getPlayersRefreshedWithinLast(long millis) {
    long minTimestamp = System.currentTimeMillis() - millis;

    Set<Player> players = new TreeSet<>();
    playerTimestamps.forEach((player, timestamp) -> {
      if (timestamp > minTimestamp) {
        players.add(player);
      }
    });
    return players;
  }

  public Set<Player> getPlayersNotRefreshedInLast(long millis) {
    Set<Player> activePlayers = getPlayersRefreshedWithinLast(millis);
    return getAllPlayers().stream()
        .filter(player -> !activePlayers.contains(player)).collect(
            Collectors.toSet());
  }

  @Override
  public void refreshPlayerTimestamp(Player player, SendPlayerUpdateCallback callback)
      throws PlayerNotFoundException {
    validatePlayerInGame(player);

    findPlayerWithUsername(player.getUsername()).ifPresent(playerInLobby -> {
      if (!playerInLobby.getPlayerStatus().equals(player.getPlayerStatus())) {
        callback.sendPlayerUpdate(
            PlayerBuilder.fromBasePlayer(playerInLobby)
                .withPlayerStatus(player.getPlayerStatus())
                .build());
      }
    });

    playerTimestamps.put(player, System.currentTimeMillis());
  }

  private Optional<Player> findPlayerWithUsername(String username) {
    return getAllPlayers().stream().filter((player -> player.getUsername().equals(username)))
        .findFirst();
  }

  public Set<Player> getPlayersWithStatuses(PlayerStatus... playerStatuses) {
    return playerTimestamps
        //Get all players stream
        .keySet()
        .stream()
        //Filter all the players with one of the above mentioned player statuses
        .filter(player ->
            Arrays.asList(playerStatuses).contains(player.getPlayerStatus()))
        .collect(Collectors.toSet());
  }

  public Set<Player> getPlayersWhoNeedToBePlayedByAgents() {
    return getPlayersWithStatuses(
        PlayerStatus.AGENT_ACTIVATED,
        PlayerStatus.LEFT_ON_PURPOSE,
        PlayerStatus.LEFT_BY_ACCIDENT);
  }

  @Override
  public Set<Player> getPlayersWhoAreConnected() {
    return getPlayersWithStatuses(PlayerStatus.CONNECTED_AND_ACTIVE, PlayerStatus.AGENT_ACTIVATED);
  }

  @Override
  public Set<Player> getAllPlayers() {
    return new HashSet<>(playerTimestamps.keySet());
  }

  @Override
  public void updatePlayers(Player... players)
      throws PlayerNotFoundException, UnknownGameException {

    for (Player player : players) {
      validatePlayerInGame(player);

      final long previousTimestamp = playerTimestamps.get(player);
      playerTimestamps.remove(player);
      playerTimestamps.put(player, previousTimestamp);

      if (player.equals(self)) {
        setPlayerForSelf(player);
      }
    }
    commitPlayerChange();
  }

  @Override
  public void resetPlayers(Set<Player> playerLobby) throws UnknownGameException {
    this.playerTimestamps.clear();
    for (Player player : playerLobby) {
      this.playerTimestamps.put(player, System.currentTimeMillis());
      try {
        if (getPlayerForSelf().equals(player)) {
          setPlayerForSelf(player);
        }
      } catch (IOException e) {
        logger.error("Failed to retrieve player for self", e);
      }
    }
    commitPlayerChange();
  }

  @Override
  public void playerConnected(Player player)
      throws UsernameAlreadyInUseException, UnknownGameException {
    if (playerTimestamps.containsKey(player)) {
      throw new UsernameAlreadyInUseException(player.getUsername());
    }
    playerTimestamps.put(player, System.currentTimeMillis());
    commitPlayerChange();
  }

  @Override
  public void setPlayerForSelf(Player player) {
    self = player;
  }

  @Override
  public Player getPlayerForSelf() throws IOException {
    if (self == null) {
      self = new Player(
          applicationDataAccess.getUsername(),
          null,
          BusinessType.NOT_SET,
          PlayerStatus.CONNECTED_AND_ACTIVE,
          defaultIPAddressProvider.getIPAddress(),
          Server.DEFAULT_PORT
      );
    }
    return self;
  }

  private void validatePlayerInGame(Player playerToCheck)
      throws PlayerNotFoundException {
    //Stream with method reference to check if the current player exists
    if (getAllPlayers()
        .stream()
        .noneMatch(playerToCheck::equals)) {
      throw new PlayerNotFoundException(playerToCheck);
    }
  }

  @Override
  public Set<UUID> getOccupiedBusinesses() {
    return getAllPlayers()
        .stream()
        .map(Player::getBusinessUUIDOptional)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toSet());
  }

  private void commitPlayerChangeToManager() {
    gameManager.setPlayerList(new HashSet<>(getAllPlayers()));
  }

  private void commitPlayerChange() throws UnknownGameException {
    leaveBusinessIfFull();
    updateGui();
    commitPlayerChangeToManager();
  }

  private void leaveBusinessIfFull() {
    // This fixes that multiple players can occupy the same slot when the connection is slow.
    try {
      Player player = getPlayerForSelf();
      if (getAvailableBusinesses().stream()
          .anyMatch(ab -> ab.getBusinessType() == player.getBusinessType() &&
              ab.getTakenBusinesses() > ab.getTotalBusinesses())) {
        setPlayerLevel(player, BusinessType.NOT_SET);
      }
    } catch (IOException | UnknownGameException e) {
      logger.error("Failed to check for full business.", e);
    }
  }

  private void setPlayerLevel(Player player, BusinessType chosenLevel) {
    Set<Player> players = new HashSet<>();
    players.add(PlayerBuilder.fromBasePlayer(player)
        .withBusinessType(chosenLevel)
        .build());
    sendPlayerListUpdate.sendPlayerListUpdate(players);
  }

  private void updateGui() throws UnknownGameException {
    List<ConnectedPlayerWithBusiness> connectedPlayerWithBusinessList = getConnectedPlayersWithBusiness();
    List<AvailableBusiness> availableBusinessLevelList = getAvailableBusinesses();

    guiService.updatePlayerLobbyState(
        new PlayerLobbyState(connectedPlayerWithBusinessList, availableBusinessLevelList));
  }

  @Override
  public List<ConnectedPlayerWithBusiness> getConnectedPlayersWithBusiness() {
    List<ConnectedPlayerWithBusiness> connectedPlayerWithBusinessList = new ArrayList<>();
    for (Player player : playerTimestamps.keySet()) {
      BusinessType businessType = player.getBusinessType();
      connectedPlayerWithBusinessList
          .add(new ConnectedPlayerWithBusiness(player.getUsername(), businessType));
    }
    return connectedPlayerWithBusinessList;
  }

  @Override
  public List<AvailableBusiness> getAvailableBusinesses() throws UnknownGameException {
    List<AvailableBusiness> availableBusinessLevelList = new ArrayList<>();
    for (BusinessType businessType : BusinessType.values()) {
      if (businessType == BusinessType.NOT_SET) {
        continue;
      }
      int takenBusinesses = 0;
      for (Player player : playerTimestamps.keySet()) {
        if (player.getBusinessType() == businessType) {
          takenBusinesses++;
        }
      }
      int totalBusinessesAvailable = gameDataAccess.getAmountOfBusinessesAvailable(businessType);
      GameConfiguration gameConfiguration = gameDataAccess.getBeerGame().getGameConfiguration();
      boolean isReady = takenBusinesses == totalBusinessesAvailable
          || gameConfiguration.getSupplyChainType() == SupplyChainType.NETWORK
          && takenBusinesses >= 1;
      availableBusinessLevelList.add(
          new AvailableBusiness(businessType, totalBusinessesAvailable, takenBusinesses, isReady));
    }
    return availableBusinessLevelList;
  }

  @Override
  public boolean allPlayersHaveABusiness() {
    return
        playerTimestamps.entrySet().stream()
            .allMatch(map -> Objects.nonNull(map.getKey().getBusinessType())
                && map.getKey().getBusinessType() != BusinessType.NOT_SET);
  }

  private void commitChangeToDataAccess() throws UnknownGameException {
    gameDataAccess.savePlayerList(getAllPlayers());
  }

  @Override
  public boolean isFull(GameConfig gameConfig) {
    return getPlayersWhoAreConnected().size() >= gameConfig.getGameData().getSettings()
        .getMaxPlayers();
  }

  public void reset(){
    playerTimestamps.clear();
    self = null;
    gameDataAccess.reset();
  }
}
