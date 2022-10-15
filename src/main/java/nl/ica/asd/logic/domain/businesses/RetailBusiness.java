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

@JsonPropertyOrder({"budget", "stock", "actionsPerRound", "agent", "uuid", "player", "customerUUID",
    "wholesaleBusinesses"})
public class RetailBusiness extends Business {

  private final UUID customerUUID;

  private final List<WholesaleBusiness> wholesaleBusinesses;

  @JsonCreator
  public RetailBusiness(
      @JsonProperty("budget") int budget,
      @JsonProperty("stock") int stock,
      @JsonProperty("actionsPerRound") Map<Integer, List<Action>> actionsPerRound,
      @JsonProperty("agent") Agent agent,
      @JsonProperty("uuid") UUID uuid,
      @JsonProperty("player") Player player,
      @JsonProperty("wholesaleBusinesses") List<WholesaleBusiness> wholesaleBusinesses,
      @JsonProperty("customerUUID") UUID customerUUID
  ) {
    super(budget, stock, actionsPerRound, agent, uuid, player, BusinessType.RETAIL);
    this.wholesaleBusinesses = wholesaleBusinesses;
    this.customerUUID = customerUUID;
  }

  @JsonGetter("customerUUID")
  public UUID getCustomerUUID() {
    return customerUUID;
  }

  @JsonGetter("wholesaleBusinesses")
  public List<WholesaleBusiness> getWholesaleBusinesses() {
    return wholesaleBusinesses;
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
    return "RetailBusiness{" +
        "wholesaleBusinesses=" + wholesaleBusinesses +
        "} " + super.toString();
  }
}
