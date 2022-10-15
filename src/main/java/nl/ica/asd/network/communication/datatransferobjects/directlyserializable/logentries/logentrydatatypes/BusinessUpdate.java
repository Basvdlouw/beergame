package nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.logentrydatatypes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.UUID;
import nl.ica.asd.logic.domain.BusinessRules;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.LogEntry;

@JsonPropertyOrder({"logIndex", "electionTerm", "updateType", "businessRules"})
public class BusinessUpdate extends LogEntry {

  private BusinessRules businessRules;

  @JsonCreator
  public BusinessUpdate(
      @JsonProperty("logIndex") int logIndex,
      @JsonProperty("electionTerm") int electionTerm,
      @JsonProperty("updateType") UpdateType updateType,
      @JsonProperty("businessRules") BusinessRules businessRules,
      @JsonProperty("queueIdentifier") UUID queueIdentifier
  ) {
    super(logIndex, electionTerm, updateType, queueIdentifier);
    this.businessRules = businessRules;
  }

  @Override
  public LogEntry cloneWithNewLogIndexAndElectionTerm(int logIndex, int electionTerm) {
    return new BusinessUpdate(
        logIndex,
        electionTerm,
        this.getUpdateType(),
        this.businessRules,
        this.getQueueIdentifier()
    );
  }

  @JsonGetter("businessRules")
  public BusinessRules getBusinessRules() {
    return businessRules;
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
