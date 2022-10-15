package nl.ica.asd.logic.gamemanager.ordermanager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.inject.Inject;
import nl.ica.asd.agenthandler.exceptions.AgentException;
import nl.ica.asd.logic.domain.Action;
import nl.ica.asd.logic.domain.BeerGame;
import nl.ica.asd.logic.domain.Business;
import nl.ica.asd.logic.domain.GameState;
import nl.ica.asd.logic.domain.actions.OrderAction;
import nl.ica.asd.logic.domain.businesses.RetailBusiness;
import nl.ica.asd.logic.gamemanager.DefaultGameManager;
import nl.ica.asd.logic.gamemanager.gamestatemanager.GameStateManagerFactory;
import nl.ica.asd.logic.watchers.OrderPlacedWatcher;
import nl.ica.asd.logic.watchers.Watcher;
import nl.ica.asd.logic.watchers.events.OrderPlacedEvent;
import nl.ica.asd.network.communication.interfaces.SendOrder;
import nl.ica.asd.storage.exception.UnknownException;
import nl.ica.asd.storage.exception.UnknownGameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultOrderManager extends DefaultGameManager implements OrderManager {

  private static final Logger logger = LoggerFactory.getLogger(DefaultOrderManager.class);

  @Inject
  private SendOrder sendOrder;
  @Inject
  private GameStateManagerFactory gameStateManagerFactory;

  @Override
  public void placeOrder(String username, int amount) {
    try {
      gameDataAccess.getPlayerList().stream()
          .filter(player -> player.getUsername().equals(username))
          .findFirst()
          .ifPresent(player -> {
            try {
              final BeerGame beerGame = gameDataAccess.getBeerGame();
              final Set<Business> businesses = new HashSet<>(beerGame.getBusinesses());
              businesses.stream()
                  .filter(
                      business -> business.getUUID().equals(player.getBusinessUUIDOptional().get()))
                  .findFirst()
                  .ifPresent(
                      business -> {
                        sendOrder.sendOrder(new OrderAction(amount, business.getUUID(),
                            null));
                        logger.info(String.format("Send order self: %d", amount));
                      });
            } catch (UnknownGameException e) {
              logger.error(String.format(BEERGAME_NOT_FOUND_MSG, e.getMessage()));
            }
          });
    } catch (UnknownGameException e) {
      logger.error(e.getMessage());
    }
  }

  @Override
  public void saveCurrentOrderActions(List<? extends Action> actions) throws UnknownGameException {
    if (actions != null && !actions.isEmpty()) {

      actions.forEach(action -> System.out.println(
          String.format("Received order: %d Sender: %s", action.getAmount(), action.getSender())));

      gameDataAccess.saveCurrentActions(actions, gameDataAccess.getCurrentRound());
      checkOrderProgress();
    }
    onEvent(new OrderPlacedEvent());
  }

  @Override
  public Map<Integer, List<Action>> getAllActionsBusiness(UUID uuid) {
    try {
      return gameDataAccess.getAllActionsPerBusiness(uuid);
    } catch (UnknownException e) {
      logger.error(String.format("Cannot get actions per business %s.", e.getMessage()));
    }
    return null;
  }

  @Override
  public void saveActions(Map<Integer, List<Action>> businessActionsMap) {
    try {
      final List<Business> businesses = gameDataAccess.getBusinesses();
      businessActionsMap
          .forEach((round, actions) -> actions.forEach(action -> businesses.forEach(business -> {
            try {
              gameDataAccess.saveCurrentActions(actions, round);
            } catch (UnknownGameException e) {
              logger.error(String.format(BEERGAME_NOT_FOUND_MSG, e.getMessage()));
            }
          })));
    } catch (UnknownGameException e) {
      logger.error(String.format(BEERGAME_NOT_FOUND_MSG, e.getMessage()));
    }
  }

  @Override
  public void processAgentOrders()
      throws UnknownGameException, AgentException {

    System.out
        .println(String.format("ProcessAgentOrders Round: %d", gameDataAccess.getCurrentRound()));
    logger.info(String.format("ProcessAgentOrders Round: %d", gameDataAccess.getCurrentRound()));

    final int currentRound = gameDataAccess.getCurrentRound() - 1;
    final List<Business> businessesWithAgentToHandle = gameDataAccess.getBusinesses().stream()
        .filter(
            business -> business.getPlayer() == null || business.getPlayer().getPlayerStatus()
                .isPlayedByAgent())
        .collect(Collectors.toList());

    List<OrderAction> orderActions = new ArrayList<>();

    for (Business business : businessesWithAgentToHandle) {
      final int nOrder = agentHandler.executeAgent(business, currentRound);
      final OrderAction orderAction = new OrderAction(nOrder, business.getUUID(), null);

      orderActions.add(orderAction);
    }
    gameDataAccess.saveCurrentActions(orderActions, gameDataAccess.getCurrentRound());
  }

  @Override
  public void processRetailOrders() throws UnknownGameException {
    BeerGame beerGame = gameDataAccess.getBeerGame();

    if (beerGame.getCurrentRound() > 1) {
      final Date date = beerGame.getDateTime();

      List<RetailBusiness> businesses = beerGame.getBusinesses().stream()
          .filter(x -> x instanceof RetailBusiness).map(x -> (RetailBusiness) x)
          .collect(Collectors.toList());

      List<OrderAction> orderActions = new ArrayList<>();

      int currentRound = gameDataAccess.getCurrentRound();
      int maxRound = gameDataAccess.getBeerGame().getGameConfiguration().getMaxRounds();

      businesses.forEach(business -> {

        Random random = new Random(date.getTime() + business.getUUID().getMostSignificantBits());

        int maxOrder = 6;
        double startPercentage = 25.0;
        boolean startingPhase = (currentRound / maxRound) * 100 < startPercentage;

        if (!startingPhase) {
          maxOrder = 7;
        }

        int order = (int) Math.floor(maxOrder / 2.0);
        for (int x = 0; x < currentRound; x++) {

          int tmpNewOrder = random.nextInt(maxOrder);

          order = newOrder(order, tmpNewOrder, 3, 1, maxOrder);
          System.out.println("Retail demand: " + order);
          //TODO: remove, currently for debugging purposes
        }

        orderActions.add(
            new OrderAction(order, business.getCustomerUUID(), business.getUUID()));
      });

      gameDataAccess.saveCurrentActions(orderActions, gameDataAccess.getCurrentRound());
    }
  }

  private int diff(int x, int y) {
    int tmp = x - y;

    return (tmp < 0) ? tmp * -1 : tmp;
  }

  private int newOrder(int oldO, int newO, int maxDiff, int minDiff, int maxOrder) {

    int diff = diff(oldO, newO);

    if (diff > minDiff && diff < maxDiff) {
      return newO;
    } else {

      if (newO > oldO) {
        return Math.min(oldO + maxDiff, maxOrder);
      } else {
        return Math.max(oldO - minDiff, 0);
      }
    }
  }

  public void checkOrderProgress() {
    try {
      final BeerGame beerGame = gameDataAccess.getBeerGame();
      final int currentRound = beerGame.getCurrentRound();
      final List<Business> businesses = beerGame.getBusinesses();

      if (businesses.isEmpty()) {
        return;
      }

      int nOrder = 0;
      for (Business business : businesses) {
        if (business.getPlayer() == null || business.getPlayer().getPlayerStatus().isPlayedByAgent()
            ||
            (business.getActionsPerRound().containsKey(currentRound) &&
                business.getActionsPerRound().get(currentRound).stream().anyMatch(
                    action -> action instanceof OrderAction &&
                        action.getSender().equals(business.getUUID()) &&
                        action.getReceiver() == null))) {
          nOrder++;
        }
      }

      int nBusiness = businesses.size();

      if (nOrder == nBusiness) {
        checkBeerGameState(beerGame);
      }
    } catch (UnknownGameException e) {
      logger.error(String.format(BEERGAME_NOT_FOUND_MSG, e.getMessage()));
    }
  }

  private void checkBeerGameState(BeerGame beerGame) {
    if (beerGame.getCurrentRound() > beerGame.getGameConfiguration().getMaxRounds()) {
      sendGameStateUpdate.sendGameStateUpdate(GameState.FINISHED);
    } else {
      gameStateManagerFactory.getGameStateManager(GameState.STARTED)
          .updateGameState(GameState.STARTED);
    }
  }

  protected boolean isOwnWatcher(Watcher watcher) {
    return watcher instanceof OrderPlacedWatcher;
  }
}
