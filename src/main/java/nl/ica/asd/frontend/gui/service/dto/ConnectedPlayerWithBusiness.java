package nl.ica.asd.frontend.gui.service.dto;

import nl.ica.asd.logic.domain.BusinessType;

public class ConnectedPlayerWithBusiness {
  private final String username;
  private final BusinessType businessType;

  public ConnectedPlayerWithBusiness(String username, BusinessType businessType) {
    this.username = username;
    this.businessType = businessType;
  }

  public String getUsername() {
    return username;
  }

  public String getBusinessName() {
    return businessType.getBusinessName();
  }

  public BusinessType getBusinessType() {
    return businessType;
  }
}
