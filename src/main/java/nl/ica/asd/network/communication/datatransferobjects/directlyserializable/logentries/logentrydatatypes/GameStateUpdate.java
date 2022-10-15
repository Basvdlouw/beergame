package nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.logentrydatatypes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.UUID;
import nl.ica.asd.logic.domain.GameState;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.LogEntry;

@JsonPropertyOrder({"logIndex", "electionTerm", "updateType", "gameState"})
public class GameStateUpdate extends LogEntry {

  private GameState gameState;

  @JsonCreator
  public GameStateUpdate(
      @JsonProperty("logIndex") int logIndex,
      @JsonProperty("electionTerm") int electionTerm,
      @JsonProperty("updateType") UpdateType updateType,
      @JsonProperty("gameState") GameState gameState,
      @JsonProperty("queueIdentifier") UUID queueIdentifier
  ) {
    super(logIndex, electionTerm, updateType, queueIdentifier);
    this.gameState = gameState;
  }

  @Override
  public LogEntry cloneWithNewLogIndexAndElectionTerm(int logIndex, int electionTerm) {
    return new GameStateUpdate(
        logIndex,
        electionTerm,
        this.getUpdateType(),
        this.gameState,
        this.getQueueIdentifier()
    );
  }

  @JsonGetter
  public GameState getGameState() {
    return gameState;
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
