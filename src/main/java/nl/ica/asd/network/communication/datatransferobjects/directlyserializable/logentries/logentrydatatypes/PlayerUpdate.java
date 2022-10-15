package nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.logentrydatatypes;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.UUID;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.LogEntry;

@JsonPropertyOrder({"logIndex", "electionTerm", "updateType", "player"})
public class PlayerUpdate extends LogEntry {

  private final Player player;

  @JsonCreator
  public PlayerUpdate(
      @JsonProperty("logIndex") int logIndex,
      @JsonProperty("electionTerm") int electionTerm,
      @JsonProperty("updateType") UpdateType updateType,
      @JsonProperty("player") Player player,
      @JsonProperty("queueIdentifier") UUID queueIdentifier
  ) {
    super(logIndex, electionTerm, updateType, queueIdentifier);
    this.player = player;
  }

  @Override
  public LogEntry cloneWithNewLogIndexAndElectionTerm(int logIndex, int electionTerm) {
    return new PlayerUpdate(
        logIndex,
        electionTerm,
        this.getUpdateType(),
        this.player,
        this.getQueueIdentifier()
    );
  }

  @JsonGetter("player")
  public Player getPlayer() {
    return player;
  }

  @Override
  public boolean equals(Object o) {
    return super.equals(o);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }
}
