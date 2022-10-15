package nl.ica.asd.logic.gamemanager.ordermanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.inject.Inject;
import nl.ica.asd.logic.domain.Action;
import nl.ica.asd.logic.domain.Business;
import nl.ica.asd.logic.domain.BusinessType;
import nl.ica.asd.logic.domain.actions.DeliveryAction;
import nl.ica.asd.logic.domain.actions.OrderAction;
import nl.ica.asd.logic.domain.builders.ActionBuilder;
import nl.ica.asd.logic.domain.businesses.FactoryBusiness;
import nl.ica.asd.logic.domain.businesses.RegionalWarehouseBusiness;
import nl.ica.asd.logic.domain.businesses.RetailBusiness;
import nl.ica.asd.logic.domain.businesses.WholesaleBusiness;
import nl.ica.asd.storage.exception.UnknownBusinessException;
import nl.ica.asd.storage.exception.UnknownGameException;
import nl.ica.asd.storage.gamedatastorage.GameDataAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultOrderCalculator implements OrderCalculator{

  private static final Logger logger = LoggerFactory.getLogger(DefaultOrderCalculator.class);
  private static final String BEERGAME_NOT_FOUND_MSG = "Beer game could not be found: %s";

  @Inject
  private GameDataAccess gameDataAccess;

  public List<OrderAction> splitOrderAction(OrderAction action) {
    try {
      final Business sender = this.gameDataAccess.getBusinessByUUID(action.getSender());

      final List<Business> businesses = getBusinessesAboveBusiness(sender);
      final List<OrderAction> orderActions = new ArrayList<>();

      final int[] actualOrders = splitOrderEven(action.getAmount(), businesses.size());

      for (int i = 0; i < actualOrders.length; i++) {
        final Business tmp = businesses.get(i);

        if (tmp != null) {
          orderActions.add(new OrderAction(actualOrders[i], action.getSender(),
              sender.equals(tmp)
                  ? ((FactoryBusiness) tmp).getSupplierUUID()
                  : tmp.getUUID()));
        }
      }

      return orderActions;
    } catch (UnknownBusinessException | UnknownGameException e) {
      logger.error(e.getMessage());
    }
    return new ArrayList<>();
  }

  private List<Business> getBusinessesAboveBusiness(Business business) {
    final List<Business> businesses = new ArrayList<>();

    if (business instanceof WholesaleBusiness) {
      businesses.addAll(((WholesaleBusiness) business).getRegionalWarehouseBusinesses());
    } else if (business instanceof RegionalWarehouseBusiness) {
      businesses.addAll(((RegionalWarehouseBusiness) business).getFactoryBusinesses());
    } else if (business instanceof RetailBusiness) {
      businesses.addAll(((RetailBusiness) business).getWholesaleBusinesses());
    }
    else if (business instanceof FactoryBusiness) {
      businesses.add(business);
    }

    return businesses;
  }

  private int[] splitOrderEven(int order, int nBusinesses) {
    final int[] orders = new int[nBusinesses];
    splitRecursively(orders, order, nBusinesses);
    return orders;
  }

  private void splitRecursively(int[] orders, int orderAmount, int businessesLeft) {
    if (businessesLeft <= 0) {
      return;
    }

    final int size = (orderAmount + businessesLeft - 1) / businessesLeft;
    orders[orders.length - businessesLeft] = size;
    splitRecursively(orders, orderAmount - size, businessesLeft - 1);
  }


  public List<DeliveryAction> calculateDeliveryActionsFirstInFirstOut(Business business, int round, boolean ignoreStock) {

    List<Action> actions = business.getAllActionsUntillRound(round);

    int stockWithIncomingGoods = ignoreStock
        ? Integer.MAX_VALUE
        : calculateDefaultBusinessInventory(actions, business.getUUID(), business.getStock());

    if (stockWithIncomingGoods <= 0) {
      return new ArrayList<>();
    }

    List<BusinessOrderStatus> orderStatuses = calculateOrderStatuses(actions, business.getUUID());

    Collections.sort(orderStatuses,
        Comparator.comparingInt(BusinessOrderStatus::getUncompletedOrderAmount));

    final List<DeliveryAction> deliveryActions = new ArrayList<>();

    if (!orderStatuses.isEmpty()) {
      int maxAmountToSend = stockWithIncomingGoods / orderStatuses.size();

      int inventorySender = stockWithIncomingGoods % orderStatuses.size();

      for (BusinessOrderStatus orderStatus : orderStatuses) {
        if (inventorySender < 0) {
          break;
        }

        inventorySender += maxAmountToSend;

        int amountToSend = Math.min(orderStatus.getUncompletedOrderAmount(), inventorySender);

        System.out.println(String
            .format("%s sends %d to %s", business.getUUID(), amountToSend, orderStatus.getBusinessUUID()));

        deliveryActions
            .add(new DeliveryAction(amountToSend, business.getUUID(), orderStatus.getBusinessUUID()));

        inventorySender -= amountToSend;
      }
    }

    return deliveryActions;
  }

  public int calculateBusinessInventoryWithOpenOrders(List<Action> actions, UUID businessUUID, int initialStock) {
    int openOrders = calculateOrderStatuses(actions, businessUUID).stream()
        .filter(x -> !x.isCompleted())
        .mapToInt(BusinessOrderStatus::getUncompletedOrderAmount)
        .sum();

    int defaultInventory = calculateDefaultBusinessInventory(actions, businessUUID, initialStock);

    return defaultInventory - openOrders;
  }

  private int calculateDefaultBusinessInventory(List<Action> actions, UUID businessUUID,
      int initialStock) {
    return initialStock + actions.stream()
        .filter(action -> action instanceof DeliveryAction)
        .mapToInt(action -> action.getReceiver().equals(businessUUID)
            ? action.getAmount()
            : -action.getAmount())
        .sum();
  }

  private List<BusinessOrderStatus> calculateOrderStatuses(List<Action> actions, UUID businessUUID) {
    Map<UUID, List<Action>> businessActions = actions.stream().filter(action ->
        action instanceof OrderAction && action.getReceiver().equals(businessUUID) ||
            action instanceof DeliveryAction && action.getSender().equals(businessUUID)
    ).collect(Collectors.groupingBy(action ->
            action instanceof OrderAction
                ? action.getSender()
                : action.getReceiver(),
        Collectors.toList()));

    return businessActions.entrySet().stream().map(set -> {
      UUID otherBusinessUUID = set.getKey();
      int orderAmount = set.getValue().stream()
          .filter(x -> x instanceof OrderAction)
          .mapToInt(Action::getAmount)
          .sum();
      int receivedAmount = set.getValue().stream()
          .filter(x -> x instanceof DeliveryAction)
          .mapToInt(Action::getAmount)
          .sum();

      return new BusinessOrderStatus(otherBusinessUUID, orderAmount, receivedAmount);
    }).collect(Collectors.toList());
  }
//
////  List<DeliveryAction> calculateDeliveryActionsEvenly(List<OrderAction> orderActions) {
////    List<DeliveryAction> deliveryActions = new ArrayList<>();
////    try {
////      final List<Business> businesses = gameDataAccess.getBusinesses();
////      for (Business business : businesses) {
////        final List<OrderAction> actionsForBusiness = getOrderActionsForBusiness(
////            business, orderActions);
////        final int totalAmountDemanded = actionsForBusiness.stream()
////            .mapToInt(Action::getAmount)
////            .sum();
////        if (totalAmountDemanded >= 0) {
////          if (totalAmountDemanded <= business.getStock()) {
////            for (Action orderAction : actionsForBusiness) {
////              deliveryActions.add(
////                  new DeliveryAction(
////                      orderAction.getAmount(), orderAction.getReceiver(), orderAction.getSender()));
////            }
////          } else {
////            final Map<Business, Double> demandPercentagePerBusiness = calculateDemandPercentagePerBusiness(
////                totalAmountDemanded, actionsForBusiness);
////            deliveryActions.addAll(calculateDeliveryActionsByPercentage(
////                demandPercentagePerBusiness, business));
////
////            final int remainingAmount =
////                business.getStock() - deliveryActions.stream().mapToInt(Action::getAmount).sum();
////            deliveryActions = divideRemaining(remainingAmount, deliveryActions);
////          }
////        }
////      }
////    } catch (UnknownGameException e) {
////      logger.error(String.format(BEERGAME_NOT_FOUND_MSG, e.getMessage()));
////    }
////    return deliveryActions;
////  }
//
//  private List<DeliveryAction> calculateDeliveryActionsByPercentage(
//      Map<Business, Double> demandPercentagePerBusiness, Business business) {
//    final int totalAmountAvailable = business.getStock();
//    final List<DeliveryAction> deliveryActions = new ArrayList<>();
//    for (Map.Entry<Business, Double> entry : demandPercentagePerBusiness.entrySet()) {
//      final int amount = (int) Math.floor(totalAmountAvailable * entry.getValue());
//      deliveryActions.add(new DeliveryAction(amount, business.getUUID(),
//          entry.getKey() != null ? entry.getKey().getUUID() : null));
//    }
//    return deliveryActions;
//  }
//
//  private Map<Business, Double> calculateDemandPercentagePerBusiness(int totalAmountDemanded,
//      List<OrderAction> actionsForBusiness) {
//    final Map<Business, Double> demandPercentagePerBusiness = new HashMap<>();
//    for (Action actionForBusiness : actionsForBusiness) {
//      final double percentage = (double) actionForBusiness.getAmount() / totalAmountDemanded;
//      try {
//        Business business = gameDataAccess.getBusinessByUUID(actionForBusiness.getSender());
//
//        demandPercentagePerBusiness.put(business, percentage);
//      } catch (UnknownBusinessException | UnknownGameException e) {
//        logger.error(e.getMessage());
//      }
//    }
//    return demandPercentagePerBusiness;
//  }
//
//  private List<OrderAction> getOrderActionsForBusiness(Business business,
//      List<OrderAction> orderActions) {
//    return orderActions.stream()
//        .filter(
//            orderAction -> business.getUUID().equals(orderAction.getReceiver()) ||
//                (orderAction.getReceiver() == null
//                    && business.getBusinessType() == BusinessType.RETAIL)
//        )
//        .collect(Collectors.toList());
//  }
//
//  private List<DeliveryAction> divideRemaining(int remainingAmount,
//      List<DeliveryAction> deliveryActions) {
//    if (remainingAmount > 0) {
//      final List<DeliveryAction> newDeliveryActions = new ArrayList<>();
//      for (DeliveryAction deliveryAction : deliveryActions) {
//        if (remainingAmount > 0) {
//          deliveryAction = ActionBuilder.fromBaseAction(deliveryAction)
//              .withAmount(deliveryAction.getAmount() + 1).build();
//          remainingAmount -= 1;
//        }
//        newDeliveryActions.add(deliveryAction);
//      }
//      return divideRemaining(remainingAmount, newDeliveryActions);
//    }
//    return deliveryActions;
//  }
}
