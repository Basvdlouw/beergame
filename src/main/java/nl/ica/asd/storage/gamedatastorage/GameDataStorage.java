package nl.ica.asd.storage.gamedatastorage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import nl.ica.asd.logic.domain.Action;
import nl.ica.asd.logic.domain.Agent;
import nl.ica.asd.logic.domain.BeerGame;
import nl.ica.asd.logic.domain.Business;
import nl.ica.asd.logic.domain.BusinessRules;
import nl.ica.asd.logic.domain.BusinessType;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.logic.domain.builders.AgentBuilder;
import nl.ica.asd.logic.domain.builders.BeerGameBuilder;
import nl.ica.asd.logic.domain.builders.BusinessBuilder;
import nl.ica.asd.logic.domain.builders.FactoryBusinessBuilder;
import nl.ica.asd.logic.domain.builders.RegionalWarehouseBusinessBuilder;
import nl.ica.asd.logic.domain.builders.RetailBusinessBuilder;
import nl.ica.asd.logic.domain.builders.WholesaleBusinessBuilder;
import nl.ica.asd.logic.domain.businesses.FactoryBusiness;
import nl.ica.asd.logic.domain.businesses.RegionalWarehouseBusiness;
import nl.ica.asd.logic.domain.businesses.RetailBusiness;
import nl.ica.asd.logic.domain.businesses.WholesaleBusiness;
import nl.ica.asd.storage.domain.GameConfiguration;
import nl.ica.asd.storage.domain.GameData;
import nl.ica.asd.storage.exception.UnknownBusinessException;
import nl.ica.asd.storage.exception.UnknownException;
import nl.ica.asd.storage.exception.UnknownGameException;
import nl.ica.asd.util.GenericObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("GameDataAccess")
@ApplicationScoped
public class GameDataStorage implements GameDataAccess {

  private GenericObjectMapper objectMapper = GenericObjectMapper.getInstance();

  private static final String FILE_NOT_FOUND_ERROR = "Game does not exist on local system %s";
  private static final String DEFAULT_PATH = "default" + File.separator;
  private static final String DEFAULT_CONFIG_FILE = DEFAULT_PATH + "game-data.json";

  private static final String DATA_PATH = "data" + File.separator;
  private static final String BEERGAMES_PATH = DATA_PATH + "beergames" + File.separator;

  private static final Logger logger = LoggerFactory.getLogger(GameDataStorage.class);

  private static BeerGame currentBeerGame = null;

  @Override
  public boolean hasCurrentGame() {
    return currentBeerGame != null;
  }

  private void checkCurrentGame() throws UnknownGameException {
    if (!hasCurrentGame()) {
      throw new UnknownGameException("No game set as current game");
    }
  }

  @Override
  public GameData getDefaultGameData() throws IOException {
    return objectMapper.readValue(this.getResourceInputStream(DEFAULT_CONFIG_FILE), GameData.class);
  }

  @Override
  public void setCurrentGame(BeerGame beerGame) throws UnknownGameException {
    Objects.requireNonNull(beerGame, "Current game can not be set to null.");

    final String pathString = this
        .getFilePath(BEERGAMES_PATH, getFileName(beerGame.getName(), beerGame.getDateTime()));

    final Supplier<UnknownGameException> unknownGameExceptionSupplier = () -> new UnknownGameException(
        String.format(FILE_NOT_FOUND_ERROR, pathString));

    try {
      final File file = Optional
          .of(new File(URLDecoder.decode(pathString, StandardCharsets.UTF_8.toString())))
          .orElseThrow(unknownGameExceptionSupplier);

      if (!file.exists()) {
        throw new UnknownGameException(
            String.format(FILE_NOT_FOUND_ERROR, pathString));
      }
    } catch (NullPointerException | UnsupportedEncodingException e) {
      throw new UnknownGameException(
          String.format(FILE_NOT_FOUND_ERROR, pathString));
    }

    currentBeerGame = beerGame;
  }

  @Override
  public BeerGame getBeerGame() throws UnknownGameException {
    checkCurrentGame();
    return currentBeerGame;
  }

  @Override
  public void saveBeerGameSettings(BeerGame beerGame) throws UnknownGameException {
    if (beerGame == null) {
      throw new UnknownGameException("Beergame cannot be null");
    }

    final String pathString = this
        .getFilePath(BEERGAMES_PATH, getFileName(beerGame.getName(), beerGame.getDateTime()));

    try {
      objectMapper.writeValue(new File(pathString), beerGame);
    } catch (IOException e) {
      logger.error(String
          .format("BeerGame could not be saved. System gave IOException: %s", e.getMessage()));
    }
  }

  @Override
  public void savePlayerList(Set<Player> playerList) throws UnknownGameException {
    checkCurrentGame();

    BeerGame beerGame = this.getBeerGame();
    if (beerGame.getGameConfiguration().getMaxPlayers() >= playerList.size()) {
      final List<Business> businesses = linkPlayerWithBusiness(playerList);
      beerGame = BeerGameBuilder.fromBaseBeerGame(beerGame)
          .withBusinesses(businesses)
          .withPlayers(playerList)
          .build();
      saveBeerGameSettings(beerGame);
      setCurrentGame(beerGame);
    }
  }

  private List<Business> linkPlayerWithBusiness(Set<Player> players) {
    final List<Business> businesses = currentBeerGame.getBusinesses();
    final List<Business> resultBusinesses = new ArrayList<>();
    for (Business business : businesses) {
      if (business.getPlayer() == null) {
        resultBusinesses.add(business);
        continue;
      }
      for (Player player : players) {
        if (player.equals(business.getPlayer())
            || player.getBusinessUUID() == business.getPlayer().getBusinessUUID()) {
          resultBusinesses.add(
              business.getBusinessType()
                  .getBuilderFromBusiness(business)
                  .withPlayer(player)
                  .build());
        }
      }
    }
    return resultBusinesses;
  }

  public Set<Player> getPlayerList() throws UnknownGameException {
    final BeerGame beerGame = this.getBeerGame();
    return beerGame != null ? beerGame.getPlayers() : Collections.emptySet();
  }

  @Override
  public void saveCurrentActions(List<? extends Action> actions, int round)
      throws UnknownGameException {

    final BeerGame beerGame = this.getBeerGame();

    if (beerGame.getCurrentRound() != round) {
      logger.error("Round number is invalid, orders could not be saved");
      return;
    }

    final List<Business> businesses = beerGame.getBusinesses();
    final List<Business> tmpBusinesses = new ArrayList<>();

    actions.forEach(action -> {
      try {
        if (action.getReceiver() != null) {
          final Business businessReceiver = this.getBusinessByUUID(action.getReceiver());
          processBusinessAction(round, tmpBusinesses, action, businessReceiver);
        }

        if (action.getSender() != null) {
          final Business businessSender = this.getBusinessByUUID(action.getSender());
          processBusinessAction(round, tmpBusinesses, action, businessSender);
        }
      } catch (UnknownGameException | UnknownBusinessException e) {
        logger.error(e.getMessage());
      }
    });

    if (businesses.size() != tmpBusinesses.size()) {
      businesses.removeAll(tmpBusinesses);
      tmpBusinesses.addAll(businesses);
    }

    final BeerGame newBeerGame = BeerGameBuilder.fromBaseBeerGame(beerGame)
        .withBusinesses(tmpBusinesses).build();

    saveBeerGameSettings(newBeerGame);
    this.setCurrentGame(newBeerGame);
  }


  private void processBusinessAction(int round, List<Business> tmpBusinesses, Action action,
      Business business) {
    Map<Integer, List<Action>> actionsPerRound = business.getActionsPerRound();

    if (actionsPerRound.containsKey(round) && !actionsPerRound.get(round).contains(action)) {
      actionsPerRound.get(round).add(action);
    } else if (!actionsPerRound.containsKey(round)) {
      try {
        actionsPerRound.put(round, new ArrayList<>(Collections.singletonList(action)));
      } catch (NullPointerException e) {
        logger.error(String
            .format("Unable to save actions: %s", e.getMessage()));
      }
    }

    final BusinessBuilder businessBuilder = business.getBusinessType().getBuilder();

    @SuppressWarnings("unchecked") final Business tmpBusiness = businessBuilder
        .fromBase(business)
        .withActionsPerRound(actionsPerRound)
        .build();

    if (!tmpBusinesses.contains(tmpBusiness)) {
      tmpBusinesses.add(tmpBusiness);
    }
  }

  @Override
  public Map<Integer, List<Action>> getAllActionsPerBusiness(UUID uuid)
      throws UnknownBusinessException, UnknownGameException {
    final BeerGame beerGame = currentBeerGame;

    if (beerGame.getBusinesses() == null || beerGame.getBusinesses().isEmpty()) {
      throw new UnknownBusinessException("There are no businesses initialized yet.");
    }

    return beerGame.getBusinesses()
        .stream()
        .filter(business -> business.getUUID() != null && business.getUUID().equals(uuid))
        .map(Business::getActionsPerRound)
        .findFirst()
        .orElseThrow(() -> new UnknownBusinessException("The business has no actions."));
  }


  @Override
  public String getBusinessRules(Business business) throws UnknownGameException {
    if (business == null) {
      logger.error("player is null and businessRules could not be loaded");
      return null;
    }

    final Agent agent = this.getBeerGame()
        .getBusinesses()
        .stream()
        .filter(b -> b.getUUID().equals(business.getUUID()))
        .findFirst()
        .map(Business::getAgent)
        .orElse(null);

    if (agent == null) {
      logger.error("businessRules could not be loaded");
      return null;
    }
    return agent.getBusinessRules();
  }

  @Override
  public void saveBusinessRules(BusinessRules businessRules)
      throws UnknownException {
    final BeerGame beerGame = this.getBeerGame();

    final Business oldBusiness = beerGame.getBusinesses().stream()
        .filter(business -> business.getUUID().equals(businessRules.getBusiness().getUUID()))
        .findFirst()
        .orElseThrow(() -> new UnknownBusinessException(
            String.format("Business with: %s not found", businessRules.getBusiness().getUUID())));

    List<Business> businesses = beerGame.getBusinesses();

    final Agent agent = AgentBuilder.fromBaseAgent(oldBusiness.getAgent())
        .withBusinessRules(businessRules.getScript()).build();

    Business newBusiness = null;

    if (oldBusiness instanceof FactoryBusiness) {
      newBusiness = FactoryBusinessBuilder
          .fromBaseBusiness((FactoryBusiness) oldBusiness).withAgent(agent).build();
    } else if (oldBusiness instanceof RegionalWarehouseBusiness) {
      newBusiness = RegionalWarehouseBusinessBuilder
          .fromBaseBusiness((RegionalWarehouseBusiness) oldBusiness).withAgent(agent)
          .build();
    } else if (oldBusiness instanceof WholesaleBusiness) {
      newBusiness = WholesaleBusinessBuilder
          .fromBaseBusiness((WholesaleBusiness) oldBusiness).withAgent(agent).build();
    } else if (oldBusiness instanceof RetailBusiness) {
      newBusiness = RetailBusinessBuilder
          .fromBaseBusiness((RetailBusiness) oldBusiness).withAgent(agent).build();
    }

    if (newBusiness != null) {
      businesses.remove(oldBusiness);
      businesses.add(newBusiness);
    }

    this.saveBeerGameSettings(BeerGameBuilder.fromBaseBeerGame(beerGame)
        .withBusinesses(businesses).build());

  }

  @Override
  public Set<BeerGame> getAvailableGames() {
    try (Stream<Path> pathStream = Files.list(Paths.get(BEERGAMES_PATH))) {
      return pathStream
          .filter(Files::isRegularFile)
          .map(fileString -> {
            try {
              return objectMapper
                  .readValue(fileString.toFile(), BeerGame.class);
            } catch (IOException e) {
              logger.warn("one of the beerGames is not a valid beerGame", e);
            }
            return null;
          })
          // TODO: Move finished check to evaluation screen, or add a method to the interface to get the last unfinished game.
          .filter(Objects::nonNull /*&& beerGame.getGameState() == GameState.FINISHED*/)
          .collect(Collectors.toSet());
    } catch (IOException e) {
      logger
          .warn(String.format("%s is not a valid path or does not contain files", BEERGAMES_PATH));
    }
    return Collections.emptySet();
  }

  private String getFileName(String gameName, Date date) {
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);
    return gameName.trim().concat(timeStamp);
  }

  @Override
  public List<Business> getBusinesses() throws UnknownGameException {
    return getBeerGame().getBusinesses();
  }

  @Override
  public int getCurrentRound() throws UnknownGameException {
    return getBeerGame().getCurrentRound();
  }

  @Override
  public BusinessType getBusinessType(UUID businessUUID)
      throws UnknownBusinessException, UnknownGameException {
    for (Business business : getBeerGame().getBusinesses()) {
      if (business.getUUID().equals(businessUUID)) {
        return business.getBusinessType();
      }
    }
    throw new UnknownBusinessException("Business was not found");
  }

  @Override
  public int getAmountOfBusinessesAvailable(BusinessType businessType) throws UnknownGameException {
    BeerGame beerGame = getBeerGame();
    GameConfiguration config = beerGame.getGameConfiguration();
    switch (config.getSupplyChainType()) {
      case LINEAR:
        return 1;
      case PYRAMID:
        return (int) Math.round(Math.pow(2, (double) businessType.getIndexInChain() - 1));
      default:
        return config.getMaxPlayers() > 3 ? config.getMaxPlayers() - 3 : 1;
    }
  }

  @Override
  public Business getBusinessByUUID(UUID uuid)
      throws UnknownGameException, UnknownBusinessException {
    return this.getBeerGame().getBusinesses().stream()
        .filter(business -> business.getUUID().equals(uuid) ||
            business instanceof RetailBusiness && ((RetailBusiness) business).getCustomerUUID()
                .equals(uuid) ||
            business instanceof FactoryBusiness && ((FactoryBusiness) business).getSupplierUUID()
                .equals(uuid))
        .findFirst()
        .orElseThrow(() -> new UnknownBusinessException("Unknown business"));
  }

  private String getFilePath(String path, String fileName) {
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(path);

    try {
      final MessageDigest md = MessageDigest.getInstance("SHA-512");
      final byte[] bytes = md.digest(fileName.getBytes(StandardCharsets.UTF_8));
      for (byte aByte : bytes) {
        stringBuilder.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
      }
    } catch (NoSuchAlgorithmException e) {
      logger.error(e.getMessage());
    }

    stringBuilder.append(".json");

    return stringBuilder.toString();
  }

  private InputStream getResourceInputStream(String path) {
    return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
  }

  public void reset() {
    currentBeerGame = null;
  }
}
