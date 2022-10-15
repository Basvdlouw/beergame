package nl.ica.asd.logic.statecalculator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BusinessState {

  private final int stock;
  private final int openOrders;
  private final int outgoingGoods;
  private final int incomingGoods;
  private final int incomingOrders;
  private final int budget;
  private final int outgoingOrders;

  private final int totalOutgoingOrders;
  private final int totalIncomingGoods;

  public BusinessState(@JsonProperty("stock") int stock, @JsonProperty("openOrders") int openOrders,
      @JsonProperty("outgoingGoods") int outgoingGoods,
      @JsonProperty("incomingGoods") int incomingGoods,
      @JsonProperty("incomingOrders") int incomingOrders, @JsonProperty("budget") int budget,
      @JsonProperty("outgoingOrders") int outgoingOrders,
      @JsonProperty("totalOutgoingOrders") int totalOutgoingOrders,
      @JsonProperty("totalIncomingGoods") int totalIncomingGoods) {
    this.stock = stock;
    this.openOrders = openOrders;
    this.outgoingGoods = outgoingGoods;
    this.incomingGoods = incomingGoods;
    this.incomingOrders = incomingOrders;
    this.budget = budget;
    this.outgoingOrders = outgoingOrders;
    this.totalOutgoingOrders = totalOutgoingOrders;
    this.totalIncomingGoods = totalIncomingGoods;
  }

  public int getStock() {
    return stock;
  }

  public int getOpenOrders() {
    return openOrders;
  }

  public int getOutgoingGoods() {
    return outgoingGoods;
  }

  public int getIncomingGoods() {
    return incomingGoods;
  }

  public int getIncomingOrders() {
    return incomingOrders;
  }

  public int getBudget() {
    return budget;
  }

  public int getOutgoingOrders() {
    return outgoingOrders;
  }

  public int getTotalOutgoingOrders() {
    return totalOutgoingOrders;
  }

  public int getTotalIncomingGoods() {
    return totalIncomingGoods;
  }

  @JsonIgnore
  public int getRemainingIncomingGoods() {
    return totalOutgoingOrders - totalIncomingGoods;
  }
}
