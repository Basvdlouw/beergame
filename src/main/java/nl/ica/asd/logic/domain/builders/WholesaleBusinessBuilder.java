package nl.ica.asd.logic.domain.builders;

import java.util.List;
import nl.ica.asd.logic.domain.businesses.RegionalWarehouseBusiness;
import nl.ica.asd.logic.domain.businesses.WholesaleBusiness;

public final class WholesaleBusinessBuilder extends
    BusinessBuilder<WholesaleBusinessBuilder, WholesaleBusiness> {

  private List<RegionalWarehouseBusiness> regionalWarehouseBusinesses;

  private WholesaleBusinessBuilder() {
  }

  public static WholesaleBusinessBuilder aWholesaleBusiness() {
    return new WholesaleBusinessBuilder();
  }

  public static WholesaleBusinessBuilder fromBaseBusiness(WholesaleBusiness wholesaleBusiness) {
    return new WholesaleBusinessBuilder()
        .withActionsPerRound(wholesaleBusiness.getActionsPerRound())
        .withAgent(wholesaleBusiness.getAgent())
        .withBudget(wholesaleBusiness.getBudget())
        .withStock(wholesaleBusiness.getStock())
        .withPlayer(wholesaleBusiness.getPlayer())
        .withUuid(wholesaleBusiness.getUUID())
        .withRegionalWarehouseBusinesses(wholesaleBusiness.getRegionalWarehouseBusinesses());
  }

  @Override
  public WholesaleBusinessBuilder fromBase(WholesaleBusiness business) {
    return WholesaleBusinessBuilder.fromBaseBusiness(business);
  }

  @Override
  public WholesaleBusinessBuilder self() {
    return this;
  }

  public WholesaleBusinessBuilder withRegionalWarehouseBusinesses(
      List<RegionalWarehouseBusiness> regionalWarehouseBusinesses) {
    this.regionalWarehouseBusinesses = regionalWarehouseBusinesses;
    return this;
  }

  public WholesaleBusiness build() {
    return new WholesaleBusiness(budget, stock, actionsPerRound, agent, uuid, player,
        regionalWarehouseBusinesses);
  }
}
