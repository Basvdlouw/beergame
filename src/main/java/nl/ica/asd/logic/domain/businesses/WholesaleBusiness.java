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

@JsonPropertyOrder({"budget", "stock", "actionsPerRound", "agent", "uuid", "player",
    "regionalWarehouseBusinesses"})
public class WholesaleBusiness extends Business {

  private final List<RegionalWarehouseBusiness> regionalWarehouseBusinesses;

  @JsonCreator
  public WholesaleBusiness(
      @JsonProperty("budget") int budget,
      @JsonProperty("stock") int stock,
      @JsonProperty("actionsPerRound") Map<Integer, List<Action>> actionsPerRound,
      @JsonProperty("agent") Agent agent,
      @JsonProperty("uuid") UUID uuid,
      @JsonProperty("player") Player player,
      @JsonProperty("regionalWarehouseBusinesses") List<RegionalWarehouseBusiness> regionalWarehouseBusinesses
  ) {
    super(budget, stock, actionsPerRound, agent, uuid, player, BusinessType.WHOLESALE);
    this.regionalWarehouseBusinesses = regionalWarehouseBusinesses;
  }

  @JsonGetter("regionalWarehouseBusinesses")
  public List<RegionalWarehouseBusiness> getRegionalWarehouseBusinesses() {
    return regionalWarehouseBusinesses;
  }

  @Override
  public boolean equals(Object o) {
    return super.equals(o);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public String toString() {
    return "WholesaleBusiness{" +
        "regionalWarehouseBusinesses=" + regionalWarehouseBusinesses +
        "} " + super.toString();
  }
}



