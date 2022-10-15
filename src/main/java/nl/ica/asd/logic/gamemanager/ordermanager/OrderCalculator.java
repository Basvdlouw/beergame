package nl.ica.asd.logic.gamemanager.ordermanager;

import java.util.List;
import java.util.UUID;
import nl.ica.asd.logic.domain.Action;
import nl.ica.asd.logic.domain.Business;
import nl.ica.asd.logic.domain.actions.DeliveryAction;
import nl.ica.asd.logic.domain.actions.OrderAction;

public interface OrderCalculator {
  List<OrderAction> splitOrderAction(OrderAction action);

  List<DeliveryAction> calculateDeliveryActionsFirstInFirstOut(Business business, int round, boolean ignoreStock);

  int calculateBusinessInventoryWithOpenOrders(List<Action> actions, UUID businessUUID, int initialStock);
}
