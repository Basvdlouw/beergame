package nl.ica.asd.logic.gamemanager.ordermanager;

import java.util.UUID;

public class BusinessOrderStatus {
  private final UUID businessUUID;

  private final int totalOrderAmount;

  private final int completedOrderAmount;

  public BusinessOrderStatus(UUID businessUUID, int totalOrderAmount, int completedOrderAmount) {
    this.businessUUID = businessUUID;
    this.totalOrderAmount = totalOrderAmount;
    this.completedOrderAmount = completedOrderAmount;
  }

  public UUID getBusinessUUID() {
    return businessUUID;
  }

  public int getTotalOrderAmount() {
    return totalOrderAmount;
  }

  public int getCompletedOrderAmount() {
    return completedOrderAmount;
  }

  public boolean isCompleted() {
    return totalOrderAmount == completedOrderAmount;
  }

  public int getUncompletedOrderAmount() {
    return totalOrderAmount - completedOrderAmount;
  }
}