package nl.ica.asd.logic.domain.businesses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import nl.ica.asd.logic.domain.Action;
import nl.ica.asd.logic.domain.Agent;
import nl.ica.asd.logic.domain.Business;
import nl.ica.asd.logic.domain.BusinessType;
import nl.ica.asd.logic.domain.Player;

@JsonPropertyOrder({"budget", "stock", "actionsPerRound", "agent", "uuid", "player", "factoryStep1",
    "factoryStep2"})
public class FactoryBusiness extends Business {

  private final UUID supplierUUID;

  @JsonCreator
  public FactoryBusiness(
      @JsonProperty("budget") int budget,
      @JsonProperty("stock") int stock,
      @JsonProperty("actionsPerRound") Map<Integer, List<Action>> actionsPerRound,
      @JsonProperty("agent") Agent agent,
      @JsonProperty("uuid") UUID uuid,
      @JsonProperty("player") Player player,
      @JsonProperty("supplierUUID") UUID supplierUUID
  ) {
    super(budget, stock, actionsPerRound, agent, uuid, player, BusinessType.FACTORY);
    this.supplierUUID = supplierUUID;
  }


  @JsonGetter("supplierUUID")
  public UUID getSupplierUUID() {
    return supplierUUID;
  }

  @Override
  public boolean equals(Object o) {
    return super.equals(o) && supplierUUID.equals(((FactoryBusiness) o).supplierUUID);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public String toString() {
    return "FactoryBusiness " + super.toString();
  }
}
