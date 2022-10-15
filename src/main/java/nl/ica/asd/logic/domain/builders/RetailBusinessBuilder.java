package nl.ica.asd.logic.domain.builders;

import java.util.List;
import java.util.UUID;
import nl.ica.asd.logic.domain.businesses.RetailBusiness;
import nl.ica.asd.logic.domain.businesses.WholesaleBusiness;

public final class RetailBusinessBuilder extends
    BusinessBuilder<RetailBusinessBuilder, RetailBusiness> {

  private List<WholesaleBusiness> wholesaleBusinesses;

  private UUID customerUUID = UUID.randomUUID();

  private RetailBusinessBuilder() {
  }

  public static RetailBusinessBuilder aRetailBusiness() {
    return new RetailBusinessBuilder();
  }

  public static RetailBusinessBuilder fromBaseBusiness(RetailBusiness regionalWarehouseBusiness) {
    return new RetailBusinessBuilder()
        .withActionsPerRound(regionalWarehouseBusiness.getActionsPerRound())
        .withAgent(regionalWarehouseBusiness.getAgent())
        .withBudget(regionalWarehouseBusiness.getBudget())
        .withStock(regionalWarehouseBusiness.getStock())
        .withPlayer(regionalWarehouseBusiness.getPlayer())
        .withUuid(regionalWarehouseBusiness.getUUID())
        .withWholesaleBusinesses(regionalWarehouseBusiness.getWholesaleBusinesses())
        .withCustomerUUID(regionalWarehouseBusiness.getCustomerUUID());
  }

  @Override
  public RetailBusinessBuilder fromBase(RetailBusiness business) {
    return RetailBusinessBuilder.fromBaseBusiness(business);
  }

  @Override
  public RetailBusinessBuilder self() {
    return this;
  }

  public RetailBusinessBuilder withWholesaleBusinesses(
      List<WholesaleBusiness> wholesaleBusinesses) {
    this.wholesaleBusinesses = wholesaleBusinesses;
    return this;
  }

  public RetailBusinessBuilder withCustomerUUID(UUID customerUUID) {
    this.customerUUID = customerUUID;
    return this;
  }

  public RetailBusiness build() {
    return new RetailBusiness(budget, stock, actionsPerRound, agent, uuid, player,
        wholesaleBusinesses, customerUUID);
  }
}
