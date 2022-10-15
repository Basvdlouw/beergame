package nl.ica.asd.logic.postgameprocessor.dto;

import java.util.UUID;

public class BusinessRoundState {

  private final UUID businessUuid;
  private final int incomingOrders;
  private final int outgoingOrders;
  private final int incomingGoods;
  private final int outgoingGoods;
  private final int stock;
  private final int openOrders;
  private final double budget;

  public BusinessRoundState(UUID businessUuid, int incomingOrders, int outgoingOrders,
      int incomingGoods, int outgoingGoods, int stock, int openOrders, double budget) {
    this.businessUuid = businessUuid;
    this.incomingOrders = incomingOrders;
    this.outgoingOrders = outgoingOrders;
    this.incomingGoods = incomingGoods;
    this.outgoingGoods = outgoingGoods;
    this.stock = stock;
    this.openOrders = openOrders;
    this.budget = budget;
  }

  public UUID getBusinessUuid() {
    return businessUuid;
  }

  public int getIncomingOrders() {
    return incomingOrders;
  }

  public int getOutgoingOrders() {
    return outgoingOrders;
  }

  public int getIncomingGoods() {
    return incomingGoods;
  }

  public int getOutgoingGoods() {
    return outgoingGoods;
  }

  public int getStock() {
    return stock;
  }

  public int getOpenOrders() {
    return openOrders;
  }

  public double getBudget() {
    return budget;
  }
}
