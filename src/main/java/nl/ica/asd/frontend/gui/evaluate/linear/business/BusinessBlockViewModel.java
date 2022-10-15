package nl.ica.asd.frontend.gui.evaluate.linear.business;

import java.util.UUID;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import nl.ica.asd.logic.domain.Business;

public class BusinessBlockViewModel {

  private Business business;

  private IntegerProperty incomingOrdersProperty = new SimpleIntegerProperty(0);
  private IntegerProperty placedOrdersProperty = new SimpleIntegerProperty(0);
  private IntegerProperty outgoingGoodsProperty = new SimpleIntegerProperty(0);
  private IntegerProperty incomingGoodsProperty = new SimpleIntegerProperty(0);
  private IntegerProperty openOrdersProperty = new SimpleIntegerProperty(0);
  private DoubleProperty budgetProperty = new SimpleDoubleProperty(0);
  private IntegerProperty stockProperty = new SimpleIntegerProperty(0);

  public BusinessBlockViewModel(Business business) {
    this.business = business;
  }

  public IntegerProperty getOutgoingGoodsProperty() {
    return outgoingGoodsProperty;
  }

  public IntegerProperty getPlacedOrdersProperty() {
    return placedOrdersProperty;
  }

  public IntegerProperty getIncomingGoodsProperty() {
    return incomingGoodsProperty;
  }

  public IntegerProperty getIncomingOrdersProperty() {
    return incomingOrdersProperty;
  }

  public IntegerProperty getOpenOrdersroperty() {
    return openOrdersProperty;
  }

  public DoubleProperty getBudgetProperty() {
    return budgetProperty;
  }

  public IntegerProperty getStockProperty() {
    return stockProperty;
  }


  public UUID getBusinessId() {
    return business.getUUID();
  }

  public Business getBusiness() {
    return business;
  }

  public void setOutgoingGoods(int outgoingGoods) {
    outgoingGoodsProperty.set(outgoingGoods);
  }

  public void setPlacedOrders(int placedOrders) {
    placedOrdersProperty.set(placedOrders);
  }

  public void setIncomingOrders(int incomingOrders) {
    incomingOrdersProperty.set(incomingOrders);
  }

  public void setIncomingGoods(int incomingGoods) {
    incomingGoodsProperty.set(incomingGoods);
  }

  public void setOpenOrdersProperty(int openOrders) {
    openOrdersProperty.set(openOrders);
  }

  public void setBudgetProperty(double budget) {
    budgetProperty.set(budget);
  }

  public void setStockProperty(int stock) {
    stockProperty.set(stock);
  }
}