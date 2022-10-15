package nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import java.util.Objects;
import java.util.UUID;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.logentrydatatypes.BusinessUpdate;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.logentrydatatypes.GameStateUpdate;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.logentrydatatypes.OrderUpdate;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.logentrydatatypes.PlayerUpdate;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.logentrydatatypes.UpdateType;

@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@logEntryType")
@JsonSubTypes({
    @Type(value = BusinessUpdate.class, name = "BusinessUpdate"),
    @Type(value = GameStateUpdate.class, name = "GameStateUpdate"),
    @Type(value = OrderUpdate.class, name = "OrderUpdate"),
    @Type(value = PlayerUpdate.class, name = "PlayerUpdate")
})
public abstract class LogEntry {

  private final int logIndex;
  private final int electionTerm;
  private final UpdateType updateType;
  private final UUID queueIdentifier;

  @JsonCreator
  public LogEntry(
      @JsonProperty("logIndex") int logIndex,
      @JsonProperty("electionTerm") int electionTerm,
      @JsonProperty("updateType") UpdateType updateType,
      @JsonProperty("queueIdentifier") UUID queueIdentifier
  ) {
    this.logIndex = logIndex;
    this.electionTerm = electionTerm;
    this.updateType = updateType;
    this.queueIdentifier = queueIdentifier;
  }

  public abstract LogEntry cloneWithNewLogIndexAndElectionTerm(int logIndex, int electionTerm);

  @JsonGetter("logIndex")
  public int getLogIndex() {
    return logIndex;
  }

  @JsonGetter("electionTerm")
  public int getElectionTerm() {
    return electionTerm;
  }

  @JsonGetter("updateType")
  public UpdateType getUpdateType() {
    return updateType;
  }

  @JsonGetter("queueIdentifier")
  public UUID getQueueIdentifier() {
    return queueIdentifier;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LogEntry logEntry = (LogEntry) o;
    return logIndex == logEntry.logIndex &&
        electionTerm == logEntry.electionTerm;
  }

  @Override
  public int hashCode() {
    return Objects.hash(logIndex, electionTerm);
  }

  @Override
  public String toString() {
    return "LogEntry{" +
        "logIndex=" + logIndex +
        ", electionTerm=" + electionTerm +
        ", type=" + this.getClass() +
        "}";
  }
}
