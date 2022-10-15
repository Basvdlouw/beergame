package nl.ica.asd.logic.gamemanager.gamestatemanager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;
import nl.ica.asd.agenthandler.exceptions.AgentException;
import nl.ica.asd.logic.domain.Action;
import nl.ica.asd.logic.domain.BeerGame;
import nl.ica.asd.logic.domain.Business;
import nl.ica.asd.logic.domain.GameState;
import nl.ica.asd.logic.domain.actions.DeliveryAction;
import nl.ica.asd.logic.domain.actions.OrderAction;
import nl.ica.asd.logic.domain.builders.BeerGameBuilder;
import nl.ica.asd.logic.domain.businesses.FactoryBusiness;
import nl.ica.asd.logic.domain.businesses.RetailBusiness;
import nl.ica.asd.logic.gamemanager.ordermanager.OrderCalculator;
import nl.ica.asd.logic.statecalculator.BusinessState;
import nl.ica.asd.logic.watchers.NextRoundStartedWatcher;
import nl.ica.asd.logic.watchers.Watcher;
import nl.ica.asd.logic.watchers.events.NextRoundStartedEvent;
import nl.ica.asd.storage.exception.UnknownBusinessException;
import nl.ica.asd.storage.exception.UnknownGameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("StartedManager")
public class StartedGameStateManager extends DefaultGameStateManager {

  private static final Logger logger = LoggerFactory.getLogger(StartedGameStateManager.class);

  @Inject
  private OrderCalculator orderCalculator;

  @Override
  protected void nextGameState()
      throws UnknownGameException, AgentException, UnknownBusinessException {

    orderManager.processAgentOrders();

    final BeerGame beerGame = gameDataAccess.getBeerGame();

    final List<Business> businesses = beerGame.getBusinesses();

    final int currentRound = beerGame.getCurrentRound();

    // Do iets met retail customer order ding.

    //Deliveries
    for (int i = 0; i < businesses.size(); i++) {
      final Business business = businesses.get(i);
      final List<Action> actions = business.getActionsPerRound().get(currentRound);

      //Get orders
      final List<OrderAction> orders = actions.stream()
          .filter(action -> action instanceof OrderAction && action.getSender().equals(business.getUUID()) && action.getReceiver() == null)
          .map(action -> (OrderAction) action)
          .collect(Collectors.toList());

      business.getActionsPerRound().get(currentRound).removeIf(action -> action instanceof OrderAction && action.getSender().equals(business.getUUID()) && action.getReceiver() == null);

      orders.forEach(orderAction -> {
        final List<OrderAction> splittedOrderActions = orderCalculator.splitOrderAction(orderAction);

        business.getActionsPerRound().get(currentRound).addAll(splittedOrderActions);

        splittedOrderActions.forEach(splittedOrderAction -> {
          try {
            Business receiverBusiness = gameDataAccess.getBusinessByUUID(splittedOrderAction.getReceiver());

            if (business instanceof FactoryBusiness && business.equals(receiverBusiness)) {
              DeliveryAction deliveryAction = new DeliveryAction(splittedOrderAction.getAmount(), splittedOrderAction.getReceiver(), splittedOrderAction.getSender());

              if (receiverBusiness.getActionsPerRound().containsKey(currentRound + 2)) {
                receiverBusiness.getActionsPerRound().get(currentRound + 2).add(deliveryAction);
              }
              else {
                List<Action> newActions = new ArrayList<>();
                newActions.add(deliveryAction);
                receiverBusiness.getActionsPerRound().put(currentRound + 2, newActions);
              }
            }
            else {
              if (receiverBusiness.getActionsPerRound().containsKey(currentRound + 1)) {
                receiverBusiness.getActionsPerRound().get(currentRound + 1).add(splittedOrderAction);
              }
              else {
                List<Action> newActions = new ArrayList<>();
                newActions.add(splittedOrderAction);
                receiverBusiness.getActionsPerRound().put(currentRound + 1, newActions);
              }
            }
          } catch (UnknownBusinessException | UnknownGameException exception) {
            logger.error(exception.getMessage(), exception);
            exception.printStackTrace();
          }
        });
      });
      List<DeliveryAction> deliveryActions = orderCalculator.calculateDeliveryActionsFirstInFirstOut(business, currentRound, false);

      deliveryActions.forEach(deliveryAction -> {
        business.getActionsPerRound().get(currentRound).add(deliveryAction);

        try {
          Business receiverBusiness = gameDataAccess.getBusinessByUUID(deliveryAction.getReceiver());

          if (!(business instanceof RetailBusiness && business.equals(receiverBusiness))) {
            if (receiverBusiness.getActionsPerRound().containsKey(currentRound + 1)) {
              receiverBusiness.getActionsPerRound().get(currentRound + 1).add(deliveryAction);
            } else {
              List<Action> newActions = new ArrayList<>();
              newActions.add(deliveryAction);
              receiverBusiness.getActionsPerRound().put(currentRound + 1, newActions);
            }
          }
        } catch (Exception exception) {
          logger.error(exception.getMessage(), exception);
          exception.printStackTrace();
        }
      });
    }

    final BeerGame newGame = BeerGameBuilder
        .fromBaseBeerGame(beerGame)
        .withCurrentRound(currentRound + 1)
        .withStartTimeCurrentRound(new Date())
        .build();

    gameDataAccess.saveBeerGameSettings(newGame);
    gameDataAccess.setCurrentGame(newGame);

    if (currentRound > newGame.getGameConfiguration().getMaxRounds()) {
      sendGameStateUpdate.sendGameStateUpdate(GameState.FINISHED);
      return;
    }

    final Map<UUID, BusinessState> businessStates = stateCalculator
        .getBusinessStatesForRound(newGame.getCurrentRound() - 1);
    final boolean bankruptBusinessPresent = businessStates.values().stream()
        .anyMatch(businessState -> businessState.getBudget() < 0);
    if (bankruptBusinessPresent) {
      sendGameStateUpdate.sendGameStateUpdate(GameState.FINISHED);
    } else {
      orderManager.processRetailOrders();
      final NextRoundStartedEvent event = new NextRoundStartedEvent(newGame.getBusinesses(),
          getPlayTurnStateForCurrentRound());
      onEvent(event);
      orderManager.checkOrderProgress();
    }
  }

  @Override
  protected boolean isOwnWatcher(Watcher watcher) {
    return watcher instanceof NextRoundStartedWatcher;
  }
}
