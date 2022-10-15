package nl.ica.asd.network.communication.datatransferobjects.directlyserializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.network.communication.datatransferobjects.DataTransferObject;

public class InitialConnectionRequest extends DataTransferObject {

  private final Player playerConnecting;

  @JsonCreator
  public InitialConnectionRequest(
      @JsonProperty("player") Player player
  ) {
    this.playerConnecting = player;
  }

  @JsonGetter("player")
  public Player getPlayer() {
    return playerConnecting;
  }
}
