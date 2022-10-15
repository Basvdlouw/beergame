package nl.ica.asd.logic.domain.builders;

import java.util.UUID;
import nl.ica.asd.logic.domain.businesses.FactoryBusiness;

public final class FactoryBusinessBuilder extends
    BusinessBuilder<FactoryBusinessBuilder, FactoryBusiness> {

  private UUID supplierUUID = UUID.randomUUID();

  private FactoryBusinessBuilder() {
  }

  public static FactoryBusinessBuilder aFactoryBusiness() {
    return new FactoryBusinessBuilder();
  }

  public static FactoryBusinessBuilder fromBaseBusiness(FactoryBusiness factoryBusiness) {
    return new FactoryBusinessBuilder()
        .withActionsPerRound(factoryBusiness.getActionsPerRound())
        .withAgent(factoryBusiness.getAgent())
        .withBudget(factoryBusiness.getBudget())
        .withStock(factoryBusiness.getStock())
        .withPlayer(factoryBusiness.getPlayer())
        .withUuid(factoryBusiness.getUUID())
        .withSupplierUUID(factoryBusiness.getSupplierUUID());

  }

  @Override
  public FactoryBusinessBuilder fromBase(FactoryBusiness factoryBusiness) {
    return FactoryBusinessBuilder.fromBaseBusiness(factoryBusiness);
  }

  @Override
  public FactoryBusinessBuilder self() {
    return this;
  }

  public FactoryBusiness build() {
    return new FactoryBusiness(budget, stock, actionsPerRound, agent, uuid, player, supplierUUID);
  }

  public FactoryBusinessBuilder withSupplierUUID(UUID supplierUUID) {
    this.supplierUUID = supplierUUID;
    return self();
  }
}
