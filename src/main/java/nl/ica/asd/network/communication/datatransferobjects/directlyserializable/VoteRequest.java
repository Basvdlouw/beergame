package nl.ica.asd.network.communication.datatransferobjects.directlyserializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.network.communication.datatransferobjects.DataTransferObject;

@JsonPropertyOrder({"electionTerm", "candidate", "lastLogIndex", "lastLogElectionTerm"})
public class VoteRequest extends DataTransferObject {

  private final int electionTerm;
  private final Player candidate;
  private final int lastLogIndex;
  private final int lastLogElectionTerm;

  @JsonCreator
  public VoteRequest(
      @JsonProperty("electionTerm") int electionTerm,
      @JsonProperty("candidate") Player candidate,
      @JsonProperty("lastLogIndex") int lastLogIndex,
      @JsonProperty("lastLogElectionTerm") int lastLogElectionTerm
  ) {
    this.electionTerm = electionTerm;
    this.candidate = candidate;
    this.lastLogIndex = lastLogIndex;
    this.lastLogElectionTerm = lastLogElectionTerm;
  }

  @JsonGetter("electionTerm")
  public int getElectionTerm() {
    return electionTerm;
  }

  @JsonGetter("candidate")
  public Player getCandidate() {
    return candidate;
  }

  @JsonGetter("lastLogIndex")
  public int getLastLogIndex() {
    return lastLogIndex;
  }

  @JsonGetter("lastLogElectionTerm")
  public int getLastLogElectionTerm() {
    return lastLogElectionTerm;
  }
}
