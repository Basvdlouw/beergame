package nl.ica.asd.network.communication.datatransferobjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import nl.ica.asd.logic.domain.Player;

@JsonPropertyOrder({"sender", "payload", "receiver"})
public class DataTransferMessage {

  private final Player sender;
  private final DataTransferObject payload;
  private final Player receiver;

  @JsonCreator
  public DataTransferMessage(
      @JsonProperty("sender") Player sender,
      @JsonProperty("payload") DataTransferObject payload,
      @JsonProperty("receiver") Player receiver
  ) {
    this.sender = sender;
    this.payload = payload;
    this.receiver = receiver;
  }

  @JsonGetter("sender")
  public Player getSender() {
    return sender;
  }

  @JsonGetter("payload")
  public DataTransferObject getPayload() {
    return payload;
  }

  @JsonGetter("receiver")
  public Player getReceiver() {
    return receiver;
  }
}
