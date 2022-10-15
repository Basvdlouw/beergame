package nl.ica.asd.logic.domain.builders;

import java.util.UUID;
import nl.ica.asd.logic.domain.BusinessType;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.logic.domain.PlayerStatus;

public class PlayerBuilder {

  private String username = "";
  private UUID businessUUID;
  private BusinessType businessType;
  private PlayerStatus playerStatus = PlayerStatus.CONNECTED_AND_ACTIVE;
  private String ip = "";
  private int port = 0;

  public static PlayerBuilder aPlayer() {
    return new PlayerBuilder();
  }

  public static PlayerBuilder fromBasePlayer(Player player) {
    return new PlayerBuilder().withBusinessUUID(player.getBusinessUUIDOptional().orElse(null))
        .withIp(player.getIP()).withPlayerStatus(player.getPlayerStatus())
        .withPort(player.getPort()).withBusinessType(player.getBusinessType())
        .withUsername(player.getUsername());
  }

  public PlayerBuilder withUsername(String username) {
    this.username = username;
    return this;
  }

  public PlayerBuilder withBusinessUUID(UUID businessUUID) {
    this.businessUUID = businessUUID;
    return this;
  }

  public PlayerBuilder withBusinessType(BusinessType businessType) {
    this.businessType = businessType;
    return this;
  }

  public PlayerBuilder withPlayerStatus(PlayerStatus playerStatus) {
    this.playerStatus = playerStatus;
    return this;
  }

  public PlayerBuilder withIp(String ip) {
    this.ip = ip;
    return this;
  }

  public PlayerBuilder withPort(int port) {
    this.port = port;
    return this;
  }

  public Player build() {
    return new Player(username, businessUUID, businessType, playerStatus, ip, port);
  }
}
