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
    "factoryBusinesses"})
public class RegionalWarehouseBusiness extends Business {

  private final List<FactoryBusiness> factoryBusinesses;

  @JsonCreator
  public RegionalWarehouseBusiness(
      @JsonProperty("budget") int budget,
      @JsonProperty("stock") int stock,
      @JsonProperty("actionsPerRound") Map<Integer, List<Action>> actionsPerRound,
      @JsonProperty("agent") Agent agent,
      @JsonProperty("uuid") UUID uuid,
      @JsonProperty("player") Player player,
      @JsonProperty("factoryBusinesses") List<FactoryBusiness> factoryBusinesses
  ) {
    super(budget, stock, actionsPerRound, agent, uuid, player, BusinessType.REGIONAL_WAREHOUSE);
    this.factoryBusinesses = factoryBusinesses;
  }

  @JsonGetter("factoryBusinesses")
  public List<FactoryBusiness> getFactoryBusinesses() {
    return factoryBusinesses;
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
    return "RegionalWarehouseBusiness{" +
        "factoryBusinesses=" + factoryBusinesses +
        "} " + super.toString();
  }
}
