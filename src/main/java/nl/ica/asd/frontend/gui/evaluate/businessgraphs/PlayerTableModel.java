package nl.ica.asd.frontend.gui.evaluate.businessgraphs;

import java.util.UUID;

public class PlayerTableModel {

  private String username;
  private String playerLevel;
  private UUID uuid;

  public PlayerTableModel(String username, String playerLevel, UUID uuid) {
    this.username = username;
    this.playerLevel = playerLevel;
    this.uuid = uuid;
  }

  public UUID getUUID() {
    return uuid;
  }

  public String getUsername() {
    return username;
  }

  public String getPlayerLevel() {
    return playerLevel;
  }
}
