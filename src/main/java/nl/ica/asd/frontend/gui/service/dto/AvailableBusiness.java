package nl.ica.asd.frontend.gui.service.dto;

import nl.ica.asd.logic.domain.BusinessType;

public class AvailableBusiness {

  private final BusinessType businessType;
  private final int totalBusinesses;
  private final int takenBusinesses;
  private final Boolean ready;

  public AvailableBusiness(BusinessType businessType, int totalBusinesses, int takenBusinesses,
      Boolean ready) {
    this.businessType = businessType;
    this.totalBusinesses = totalBusinesses;
    this.takenBusinesses = takenBusinesses;
    this.ready = ready;
  }

  public String getBusinessName() {
    return businessType.getBusinessName();
  }

  public BusinessType getBusinessType() {
    return businessType;
  }

  public int getTakenBusinesses() {
    return takenBusinesses;
  }

  public int getTotalBusinesses() {
    return totalBusinesses;
  }

  public String getAvailable() {
    return takenBusinesses + "/" + totalBusinesses;
  }

  public Boolean getReady() {
    return ready;
  }
}
