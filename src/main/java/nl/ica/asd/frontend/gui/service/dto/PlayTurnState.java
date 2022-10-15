package nl.ica.asd.frontend.gui.service.dto;

public class PlayTurnState {

  public enum PlayTurnLevel {
    FACTORY, REGIONALWAREHOUSE, WHOLESALE, RETAIL
  }

  private final PlayTurnLevel playerLevel;

  private final int currentRound;

  private final int maxRound;

  private final int currentTime;

  private final int incomingGoods;

  private final int stock;

  private final int openOrders;

  private final int budget;

  private final int outgoingGoods;

  private final int incomingOrders;

  public PlayTurnState(PlayTurnLevel playerLevel, int currentRound, int maxRound, int currentTime,
      int incomingGoods,
      int stock,
      int openOrders, int budget, int outgoingGoods, int incomingOrders) {
    this.playerLevel = playerLevel;
    this.currentRound = currentRound;
    this.maxRound = maxRound;
    this.currentTime = currentTime;
    this.incomingGoods = incomingGoods;
    this.stock = stock;
    this.openOrders = openOrders;
    this.budget = budget;
    this.outgoingGoods = outgoingGoods;
    this.incomingOrders = incomingOrders;
  }

  public PlayTurnLevel getPlayerLevel() {
    return playerLevel;
  }

  public int getCurrentRound() {
    return currentRound;
  }

  public int getMaxRound() {
    return maxRound;
  }

  public int getCurrentTime() {
    return currentTime > 0 ? currentTime : 0;
  }

  public int getIncomingGoods() {
    return incomingGoods;
  }

  public int getStock() {
    return stock;
  }

  public int getOpenOrders() {
    return openOrders;
  }

  public int getBudget() {
    return budget;
  }

  public int getOutgoingGoods() {
    return outgoingGoods;
  }

  public int getIncomingOrders() {
    return incomingOrders;
  }

  public int calculateOutgoingGoods() {
    int realStock = stock + incomingGoods;

    if (realStock <= 0) {
      return 0;
    }

    return Math.min(incomingOrders + openOrders, realStock);
  }

}
