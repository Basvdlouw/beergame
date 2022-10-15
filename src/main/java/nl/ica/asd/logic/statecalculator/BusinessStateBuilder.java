package nl.ica.asd.logic.statecalculator;

public final class BusinessStateBuilder {

  private int stock;
  private int openOrders;
  private int outgoingGoods;
  private int incomingGoods;
  private int incomingOrders;
  private int budget;
  private int outgoingOrders;
  private int totalOutgoingOrders;
  private int totalIncomingGoods;

  private BusinessStateBuilder() {
  }

  public static BusinessStateBuilder aBusinessState() {
    return new BusinessStateBuilder();
  }

  public static BusinessStateBuilder fromBaseBusinessState(BusinessState businessState) {
    return new BusinessStateBuilder()
        .withStock(businessState.getStock())
        .withOpenOrders(businessState.getOpenOrders())
        .withOutgoingGoods(businessState.getOutgoingGoods())
        .withIncomingGoods(businessState.getIncomingGoods())
        .withIncomingOrders(businessState.getIncomingOrders())
        .withOutgoingOrders(businessState.getOutgoingOrders())
        .withBudget(businessState.getBudget())
        .withTotalIncomingGoods(businessState.getTotalIncomingGoods())
        .withTotalOutgoingOrders(businessState.getTotalOutgoingOrders());
  }

  public BusinessStateBuilder withOutgoingOrders(int outgoingOrders) {
    this.outgoingOrders = outgoingOrders;
    return this;
  }

  public BusinessStateBuilder withStock(int stock) {
    this.stock = stock;
    return this;
  }

  public BusinessStateBuilder withOpenOrders(int openOrders) {
    this.openOrders = openOrders;
    return this;
  }

  public BusinessStateBuilder withOutgoingGoods(int outgoingGoods) {
    this.outgoingGoods = outgoingGoods;
    return this;
  }

  public BusinessStateBuilder withIncomingGoods(int incomingGoods) {
    this.incomingGoods = incomingGoods;
    return this;
  }

  public BusinessStateBuilder withIncomingOrders(int incomingOrders) {
    this.incomingOrders = incomingOrders;
    return this;
  }

  public BusinessStateBuilder withBudget(int budget) {
    this.budget = budget;
    return this;
  }

  public BusinessStateBuilder withTotalOutgoingOrders(int totalOutgoingOrders) {
    this.totalOutgoingOrders = totalOutgoingOrders;
    return this;
  }

  public BusinessStateBuilder withTotalIncomingGoods(int totalIncomingGoods) {
    this.totalIncomingGoods = totalIncomingGoods;
    return this;
  }

  public BusinessState build() {
    return new BusinessState(stock, openOrders, outgoingGoods, incomingGoods, incomingOrders,
        budget, outgoingOrders, totalOutgoingOrders, totalIncomingGoods);
  }
}
