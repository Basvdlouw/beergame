package nl.ica.asd.logic.statecalculator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;
import nl.ica.asd.logic.domain.Action;
import nl.ica.asd.logic.domain.BeerGame;
import nl.ica.asd.logic.domain.Business;
import nl.ica.asd.logic.domain.BusinessType;
import nl.ica.asd.logic.domain.actions.DeliveryAction;
import nl.ica.asd.logic.domain.actions.OrderAction;
import nl.ica.asd.logic.gamemanager.ordermanager.BusinessOrderStatus;
import nl.ica.asd.logic.gamemanager.ordermanager.OrderCalculator;
import nl.ica.asd.storage.domain.GamePrices;
import nl.ica.asd.storage.exception.UnknownBusinessException;
import nl.ica.asd.storage.exception.UnknownGameException;
import nl.ica.asd.storage.gameconfig.GameConfig;
import nl.ica.asd.storage.gamedatastorage.GameDataAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("StateCalculator")
public class DefaultStateCalculator implements StateCalculator {

  @Named("GameDataAccess")
  private final GameDataAccess gameDataAccess;
  @Named("GameConfig")
  private final GameConfig gameConfig;
  private static final Logger logger = LoggerFactory.getLogger(DefaultStateCalculator.class);

  private final OrderCalculator orderCalculator;

  @Inject
  public DefaultStateCalculator(GameDataAccess gameDataAccess, GameConfig gameConfig, OrderCalculator orderCalculator) {
    this.gameDataAccess = gameDataAccess;
    this.gameConfig = gameConfig;
    this.orderCalculator = orderCalculator;
  }

  @Override
  public BusinessState getBusinessStateForBusiness(UUID businessUuid, int round)
      throws UnknownBusinessException, UnknownGameException {
    return calculateBusinessState(gameDataAccess.getBusinessByUUID(businessUuid), round);
  }

  @Override
  public Map<UUID, BusinessState> getBusinessStatesForRound(int round)
      throws UnknownBusinessException, UnknownGameException {
    final BeerGame beerGame = this.getBeerGame();
    final Map<UUID, BusinessState> businessStateMap = new HashMap<>();
    beerGame.getBusinesses()
        .forEach(business -> withCallable(
            () -> businessStateMap.put(business.getUUID(), calculateBusinessState(business, round)))
        );

    return businessStateMap;
  }

  @Override
  public Map<Integer, BusinessState> getBusinessStatesForPlayer(String username)
      throws UnknownBusinessException, UnknownGameException {
    final Map<Integer, BusinessState> businessStateMap = new HashMap<>();
    gameDataAccess.getPlayerList()
        .stream()
        .filter(player -> player.getUsername().equals(username))
        .map(
            player -> withCallable(
                () -> gameDataAccess.getBusinessByUUID(player.getBusinessUUIDOptional().get())))
        .findFirst()
        .ifPresent(business -> {
          try {
            final int currentRound = getBeerGame().getCurrentRound();
            fillBusinessMap(businessStateMap, business, currentRound, 1);
          } catch (UnknownGameException e) {
            logger.error(e.getMessage());
          }
        });
    return businessStateMap;
  }

  private void fillBusinessMap(Map<Integer, BusinessState> businessStateMap, Business business,
      int currentRound, int round) throws UnknownGameException {

    businessStateMap.put(round, calculateBusinessState(business, round));
    if (currentRound <= round) {
      return;
    }
    fillBusinessMap(businessStateMap, business, currentRound, round + 1);
  }

  @Override
  public Map<Integer, Map<UUID, BusinessState>> getAllBusinessStates()
      throws UnknownBusinessException, UnknownGameException {
    final Map<Integer, Map<UUID, BusinessState>> businessStatesPerRoundMap = new HashMap<>();
    final int currentRound = getBeerGame().getCurrentRound();
    for (int round = 1; round <= currentRound; round++) {
      businessStatesPerRoundMap.put(round, getBusinessStatesForRound(round));
    }
    return businessStatesPerRoundMap;
  }

  private BusinessState calculateBusinessState(Business business, int round)
      throws UnknownGameException {
    BusinessState businessState = BusinessStateBuilder.aBusinessState()
        .withBudget(business.getBudget())
        .withStock(business.getStock())
        .build();

    Map<Integer, List<Action>> actions = business.getActionsPerRound();
    for (int i = 0; i <= round && i <= actions.size(); i++) {
      int currentRound = i;

      List<Action> allRoundActions = business.getAllActionsUntillRound(currentRound - 1);

      int totalInventory = orderCalculator.calculateBusinessInventoryWithOpenOrders(
          allRoundActions,
          business.getUUID(),
          business.getStock());

      int stock = totalInventory >= 0 ? totalInventory : 0;
      int openOrders = totalInventory < 0 ? Math.abs(totalInventory) : 0;

      int incomingOrders = actions.get(currentRound).stream()
          .filter(x -> x instanceof OrderAction && x.getReceiver().equals(business.getUUID()))
          .mapToInt(Action::getAmount)
          .sum();

      int incomingGoods = actions.get(currentRound).stream()
          .filter(x -> x instanceof DeliveryAction && x.getReceiver().equals(business.getUUID()))
          .mapToInt(Action::getAmount)
          .sum();

      int outgoingOrders = actions.get(currentRound).stream()
          .filter(x -> x instanceof OrderAction && x.getSender().equals(business.getUUID()))
          .mapToInt(Action::getAmount)
          .sum();

      int outgoingGoods = actions.get(currentRound).stream()
          .filter(x -> x instanceof DeliveryAction && x.getSender().equals(business.getUUID()))
          .mapToInt(Action::getAmount)
          .sum();

      int budget = calculateBudget(
          business.getBusinessType(),
          businessState.getBudget(),
          outgoingGoods,
          incomingGoods,
          stock,
          openOrders);

      businessState = BusinessStateBuilder.aBusinessState()
          .withStock(stock)
          .withOpenOrders(openOrders)
          .withIncomingGoods(incomingGoods)
          .withOutgoingGoods(outgoingGoods)
          .withIncomingOrders(incomingOrders)
          .withOutgoingOrders(outgoingOrders)
          .withBudget(budget)
          .withTotalOutgoingOrders(businessState.getTotalOutgoingOrders() + outgoingOrders)
          .withTotalIncomingGoods(businessState.getTotalIncomingGoods() + incomingGoods)
          .build();
    }

    return businessState;
  }

  private BeerGame getBeerGame() throws UnknownGameException {
    if (!gameDataAccess.hasCurrentGame()) {
      logger.error("getBusinessStateForBusiness is called when no game has been set");
    }
    return gameDataAccess.getBeerGame();
  }

  private int calculateBudget(BusinessType businessType, int currentBudget, int outgoingGoods, int incomingGoods, int stock, int openOrders) {
    final GamePrices gamePrices = gameConfig.getGamePricesForBusiness(businessType);
    currentBudget += gamePrices.getOutgoingGoodsPrice() * outgoingGoods;
    currentBudget -= gamePrices.getIncomingGoodsPrice() * incomingGoods;
    currentBudget -= gamePrices.getStockPrice() * stock;
    currentBudget -= gamePrices.getOpenOrdersPrice() * openOrders;

    return currentBudget;
  }

  private static <T> T withCallable(Callable<T> callable) {
    try {
      return callable.call();
    } catch (Exception e) {
      logger.error("Statecalculator error:", e);
    }
    return null;
  }
}
