package nl.ica.asd.network.communication.interfaces;

import nl.ica.asd.logic.domain.actions.OrderAction;

public interface SendOrder {

  void sendOrder(OrderAction order);

}
