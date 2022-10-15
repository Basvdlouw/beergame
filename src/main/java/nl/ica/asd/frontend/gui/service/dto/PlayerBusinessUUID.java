package nl.ica.asd.frontend.gui.service.dto;

import java.util.UUID;

public class PlayerBusinessUUID {

  private String username;
  private String businessType;
  private UUID uuid;

  public PlayerBusinessUUID(String username, String businessType, UUID uuid) {
    this.username = username;
    this.businessType = businessType;
    this.uuid = uuid;
  }


  public UUID getUUID() {
    return this.uuid;
  }

  public String getUsername() {
    return this.username;
  }

  public String getBusinessType() {
    return this.businessType;
  }

}
