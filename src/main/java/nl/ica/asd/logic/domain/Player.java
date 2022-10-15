package nl.ica.asd.logic.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@JsonPropertyOrder({"username", "businessUUID", "businessType", "playerStatus", "ip", "port"})
public class Player implements Comparable<Player> {

  private final String username;
  private final UUID businessUUID;
  private final BusinessType businessType;
  private final PlayerStatus playerStatus;
  private final String ip;
  private final int port;

  @JsonCreator
  public Player(
      @JsonProperty("username") String username,
      @JsonProperty("businessUUID") UUID businessUUID,
      @JsonProperty("businessType") BusinessType businessType,
      @JsonProperty("playerStatus") PlayerStatus playerStatus,
      @JsonProperty("ip") String ip,
      @JsonProperty("port") int port
  ) {
    this.username = username;
    this.businessUUID = businessUUID;
    this.businessType = businessType;
    this.playerStatus = playerStatus;
    this.ip = ip;
    this.port = port;
  }

  @JsonGetter("username")
  public String getUsername() {
    return username;
  }

  @JsonGetter("businessUUID")
  public UUID getBusinessUUID() {
    return businessUUID;
  }

  @JsonIgnore
  public Optional<UUID> getBusinessUUIDOptional() {
    return Optional.ofNullable(businessUUID);
  }

  @JsonGetter("businessType")
  public BusinessType getBusinessType() {
    return businessType;
  }

  @JsonGetter("playerStatus")
  public PlayerStatus getPlayerStatus() {
    return playerStatus;
  }

  @JsonGetter("ip")
  public String getIP() {
    return ip;
  }

  @JsonGetter("port")
  public int getPort() {
    return port;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Player player = (Player) o;
    return username.equals(player.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username);
  }

  @Override
  public String toString() {
    return "Player{" +
        "username='" + username + '\'' +
        ", businessUUID=" + businessUUID +
        ", businessType=" + businessType +
        ", playerStatus=" + playerStatus +
        ", ip='" + ip + '\'' +
        ", port=" + port +
        '}';
  }

  @Override
  public int compareTo(Player o) {
    return username.compareTo(o.username);
  }
}