package nl.ica.asd.network.communication.datatransferobjects.directlyserializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.network.communication.datatransferobjects.DataTransferObject;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.LogEntry;

@JsonPropertyOrder({"electionTerm", "leader", "previousLogIndex", "previousLogElectionTerm",
    "logEntries", "leaderCommit"})
public class AppendEntry extends DataTransferObject {

  private final int electionTerm;
  private final Player leader;
  private final int previousLogIndex;
  private final int previousLogElectionTerm;
  private final List<LogEntry> logEntries;
  private final int leaderCommit;

  @JsonCreator
  public AppendEntry(
      @JsonProperty("electionTerm") int electionTerm,
      @JsonProperty("leader") Player leader,
      @JsonProperty("previousLogIndex") int previousLogIndex,
      @JsonProperty("previousLogElectionTerm") int previousLogElectionTerm,
      @JsonProperty("logEntries") List<LogEntry> logEntries,
      @JsonProperty("leaderCommit") int leaderCommit
  ) {
    this.electionTerm = electionTerm;
    this.leader = leader;
    this.previousLogIndex = previousLogIndex;
    this.previousLogElectionTerm = previousLogElectionTerm;
    this.logEntries = logEntries;
    this.leaderCommit = leaderCommit;
  }

  @JsonGetter("electionTerm")
  public int getElectionTerm() {
    return electionTerm;
  }

  @JsonGetter("leader")
  public Player getLeader() {
    return leader;
  }

  @JsonGetter("previousLogIndex")
  public int getPreviousLogIndex() {
    return previousLogIndex;
  }

  @JsonGetter("previousLogElectionTerm")
  public int getPreviousLogElectionTerm() {
    return previousLogElectionTerm;
  }

  @JsonGetter("logEntries")
  public List<LogEntry> getLogEntries() {
    return logEntries;
  }

  @JsonGetter("leaderCommit")
  public int getLeaderCommit() {
    return leaderCommit;
  }

  @Override
  public String toString() {
    return "AppendEntry{" +
        "electionTerm=" + electionTerm +
        ", leader=" + leader +
        ", previousLogIndex=" + previousLogIndex +
        ", previousLogElectionTerm=" + previousLogElectionTerm +
        ", logEntries=" + logEntries +
        ", leaderCommit=" + leaderCommit +
        '}';
  }
}
