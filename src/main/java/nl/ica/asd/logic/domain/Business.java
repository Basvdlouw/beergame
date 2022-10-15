package nl.ica.asd.logic.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import nl.ica.asd.logic.domain.businesses.FactoryBusiness;
import nl.ica.asd.logic.domain.businesses.RegionalWarehouseBusiness;
import nl.ica.asd.logic.domain.businesses.RetailBusiness;
import nl.ica.asd.logic.domain.businesses.WholesaleBusiness;

@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@businessType")
@JsonSubTypes({
    @Type(value = FactoryBusiness.class, name = "FactoryBusiness"),
    @Type(value = RegionalWarehouseBusiness.class, name = "RegionalWarehouseBusiness"),
    @Type(value = WholesaleBusiness.class, name = "WholesaleBusiness"),
    @Type(value = RetailBusiness.class, name = "RetailBusiness")
})
public abstract class Business implements Comparable<Business> {

  private final int budget;
  private final int stock;
  private final Map<Integer, List<Action>> actionsPerRound;
  private final Agent agent;
  private final UUID uuid;
  private final Player player;
  private final BusinessType businessType;

  @JsonCreator
  public Business(
      @JsonProperty("budget") int budget,
      @JsonProperty("stock") int stock,
      @JsonProperty("actionsPerRound") Map<Integer, List<Action>> actionsPerRound,
      @JsonProperty("agent") Agent agent,
      @JsonProperty("uuid") UUID uuid,
      @JsonProperty("player") Player player,
      @JsonProperty("businessType") BusinessType businessType
  ) {
    this.budget = budget;
    this.stock = stock;
    this.actionsPerRound = actionsPerRound == null ? new HashMap<>() : actionsPerRound;
    this.agent = agent;
    this.uuid = uuid;
    this.player = player;
    this.businessType = businessType;
  }

  @JsonGetter("budget")
  public int getBudget() {
    return budget;
  }

  @JsonGetter("stock")
  public int getStock() {
    return stock;
  }

  @JsonGetter("actionsPerRound")
  public Map<Integer, List<Action>> getActionsPerRound() {
    return actionsPerRound;
  }

  @JsonGetter("agent")
  public Agent getAgent() {
    return agent;
  }

  @JsonGetter("uuid")
  public UUID getUUID() {
    return uuid;
  }

  @JsonGetter("player")
  public Player getPlayer() {
    return player;
  }

  @JsonGetter("businessType")
  public BusinessType getBusinessType() {
    return businessType;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj == null || obj.getClass() != this.getClass()) {
      return false;
    }

    final Business business = (Business) obj;
    return uuid.equals(business.getUUID());
  }

  public List<Action> getAllActionsUntillRound(int round) {
    return actionsPerRound.entrySet().stream()
        .filter(entrySet -> entrySet.getKey() <= round)
        .map(Entry::getValue)
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid);
  }

  @Override
  public String toString() {
    return "Business{" +
        "budget=" + budget +
        ", stock=" + stock +
        ", actionsPerRound=" + actionsPerRound +
        ", agent=" + agent +
        ", uuid=" + uuid +
        ", player=" + player +
        ", businessType=" + businessType +
        '}';
  }

  @Override
  public int compareTo(Business business) {
    int result = businessType.compareTo(business.getBusinessType());
    return result != 0 ? result : uuid.compareTo(business.getUUID());
  }
}
