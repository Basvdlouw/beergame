package nl.ica.asd.network.communication.datatransferobjects.directlyserializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Set;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.network.communication.datatransferobjects.DataTransferObject;
import nl.ica.asd.network.communication.gameinitialisation.ConnectionResult;
import nl.ica.asd.storage.domain.GameData;

@JsonPropertyOrder({"connectedPlayers", "gameData", "success"})
public class InitialConnectionResponse extends DataTransferObject {

  private final Set<Player> connectedPlayers;
  private final GameData gameData;
  private final ConnectionResult connectionResult;

  @JsonCreator
  public InitialConnectionResponse(
      @JsonProperty("connectedPlayers") Set<Player> connectedPlayers,
      @JsonProperty("gameData") GameData gameData,
      @JsonProperty("connectionResult") ConnectionResult connectionResult
  ) {
    this.connectedPlayers = connectedPlayers;
    this.gameData = gameData;
    this.connectionResult = connectionResult;
  }

  @JsonGetter("connectedPlayers")
  public Set<Player> getPlayers() {
    return connectedPlayers;
  }

  @JsonGetter("gameData")
  public GameData getGameData() {
    return gameData;
  }

  @JsonGetter("connectionResult")
  public ConnectionResult getConnectionResult() {
    return connectionResult;
  }
}
