package nl.ica.asd.storage.domain;

public class GamePrices {

  private final int incomingGoodsPrice;
  private final int outgoingGoodsPrice;
  private final int stockPrice;
  private final int openOrdersPrice;

  public GamePrices(int incomingGoodsPrice, int outgoingGoodsPrice, int stockPrice,
      int openOrdersPrice) {
    this.incomingGoodsPrice = incomingGoodsPrice;
    this.outgoingGoodsPrice = outgoingGoodsPrice;
    this.stockPrice = stockPrice;
    this.openOrdersPrice = openOrdersPrice;
  }

  public int getIncomingGoodsPrice() {
    return incomingGoodsPrice;
  }

  public int getOutgoingGoodsPrice() {
    return outgoingGoodsPrice;
  }

  public int getStockPrice() {
    return stockPrice;
  }

  public int getOpenOrdersPrice() {
    return openOrdersPrice;
  }
}
