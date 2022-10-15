package nl.ica.asd.network.communication.datatransferobjects.directlyserializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import nl.ica.asd.network.communication.datatransferobjects.DataTransferObject;

@JsonPropertyOrder({"electionTerm", "success"})
public class AppendEntryResult extends DataTransferObject {

  private final int electionTerm;
  private final int newestReceivedLogIndex;
  private final boolean success;


  @JsonCreator
  public AppendEntryResult(
      @JsonProperty("electionTerm") int electionTerm,
      @JsonProperty("newestReceivedLogIndex") int newestReceivedLogIndex,
      @JsonProperty("success") boolean success
  ) {
    this.electionTerm = electionTerm;
    this.newestReceivedLogIndex = newestReceivedLogIndex;
    this.success = success;
  }

  @JsonGetter("electionTerm")
  public int getElectionTerm() {
    return electionTerm;
  }

  @JsonGetter("newestReceivedLogIndex")
  public int getNewestReceivedLogIndex() {
    return newestReceivedLogIndex;
  }

  @JsonGetter("success")
  public boolean isSuccess() {
    return success;
  }
}
