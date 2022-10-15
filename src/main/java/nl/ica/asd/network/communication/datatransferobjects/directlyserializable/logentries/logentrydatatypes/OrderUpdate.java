package nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.logentrydatatypes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.UUID;
import nl.ica.asd.logic.domain.actions.OrderAction;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.LogEntry;

@JsonPropertyOrder({"logIndex", "electionTerm", "updateType", "order"})
public class OrderUpdate extends LogEntry {

  private OrderAction order;

  @JsonCreator
  public OrderUpdate(
      @JsonProperty("logIndex") int logIndex,
      @JsonProperty("electionTerm") int electionTerm,
      @JsonProperty("updateType") UpdateType updateType,
      @JsonProperty("order") OrderAction order,
      @JsonProperty("queueIdentifier") UUID queueIdentifier
  ) {
    super(logIndex, electionTerm, updateType, queueIdentifier);
    this.order = order;
  }

  @Override
  public LogEntry cloneWithNewLogIndexAndElectionTerm(int logIndex, int electionTerm) {
    return new OrderUpdate(
        logIndex,
        electionTerm,
        this.getUpdateType(),
        this.order,
        this.getQueueIdentifier()
    );
  }

  @JsonGetter("order")
  public OrderAction getOrder() {
    return order;
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
