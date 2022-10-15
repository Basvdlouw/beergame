package nl.ica.asd.logic.domain.builders;

import java.util.List;
import nl.ica.asd.logic.domain.businesses.FactoryBusiness;
import nl.ica.asd.logic.domain.businesses.RegionalWarehouseBusiness;

public final class RegionalWarehouseBusinessBuilder extends
    BusinessBuilder<RegionalWarehouseBusinessBuilder, RegionalWarehouseBusiness> {

  private List<FactoryBusiness> factoryBusinesses;

  private RegionalWarehouseBusinessBuilder() {
  }

  public static RegionalWarehouseBusinessBuilder aRegionalWarehouseBusiness() {
    return new RegionalWarehouseBusinessBuilder();
  }

  public static RegionalWarehouseBusinessBuilder fromBaseBusiness(
      RegionalWarehouseBusiness regionalWarehouseBusiness) {
    return new RegionalWarehouseBusinessBuilder()
        .withActionsPerRound(regionalWarehouseBusiness.getActionsPerRound())
        .withAgent(regionalWarehouseBusiness.getAgent())
        .withBudget(regionalWarehouseBusiness.getBudget())
        .withStock(regionalWarehouseBusiness.getStock())
        .withPlayer(regionalWarehouseBusiness.getPlayer())
        .withUuid(regionalWarehouseBusiness.getUUID())
        .withFactoryBusinesses(regionalWarehouseBusiness.getFactoryBusinesses());
  }

  @Override
  public RegionalWarehouseBusinessBuilder fromBase(RegionalWarehouseBusiness business) {
    return RegionalWarehouseBusinessBuilder.fromBaseBusiness(business);
  }

  @Override
  public RegionalWarehouseBusinessBuilder self() {
    return this;
  }

  public RegionalWarehouseBusinessBuilder withFactoryBusinesses(
      List<FactoryBusiness> factoryBusinesses) {
    this.factoryBusinesses = factoryBusinesses;
    return this;
  }

  public RegionalWarehouseBusiness build() {
    return new RegionalWarehouseBusiness(budget, stock, actionsPerRound, agent, uuid, player,
        factoryBusinesses);
  }
}
