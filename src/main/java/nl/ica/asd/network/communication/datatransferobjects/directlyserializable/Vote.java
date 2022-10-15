package nl.ica.asd.network.communication.datatransferobjects.directlyserializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import nl.ica.asd.network.communication.datatransferobjects.DataTransferObject;


@JsonPropertyOrder({"electionTerm", "voteGranted"})
public class Vote extends DataTransferObject {

  private final int electionTerm;
  private final boolean voteGranted;

  @JsonCreator
  public Vote(
      @JsonProperty("electionTerm") int electionTerm,
      @JsonProperty("voteGranted") boolean voteGranted
  ) {
    this.electionTerm = electionTerm;
    this.voteGranted = voteGranted;
  }

  @JsonGetter("electionTerm")
  public int getElectionTerm() {
    return electionTerm;
  }

  @JsonGetter("voteGranted")
  public boolean isVoteGranted() {
    return voteGranted;
  }
}
