package nl.ica.asd.logic.gamemanager.pyramideutil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.inject.Inject;
import nl.ica.asd.Main;
import nl.ica.asd.agenthandler.AgentHandler;
import nl.ica.asd.agenthandler.exceptions.AgentException;
import nl.ica.asd.logic.domain.Agent;
import nl.ica.asd.logic.domain.Business;
import nl.ica.asd.logic.domain.BusinessType;
import nl.ica.asd.logic.domain.builders.AgentBuilder;
import nl.ica.asd.logic.domain.builders.FactoryBusinessBuilder;
import nl.ica.asd.logic.domain.builders.RegionalWarehouseBusinessBuilder;
import nl.ica.asd.logic.domain.builders.RetailBusinessBuilder;
import nl.ica.asd.logic.domain.builders.WholesaleBusinessBuilder;
import nl.ica.asd.logic.domain.businesses.FactoryBusiness;
import nl.ica.asd.logic.domain.businesses.RegionalWarehouseBusiness;
import nl.ica.asd.logic.domain.businesses.RetailBusiness;
import nl.ica.asd.logic.domain.businesses.WholesaleBusiness;
import nl.ica.asd.logic.statecalculator.BusinessState;

public class BusinessesPyramidFactory {

  @Inject
  private AgentHandler agentHandler;

  public Business getBusiness(BusinessType businessType, List<Business> businesses,
      Map<Class<? extends Business>, BusinessState> initialBusinessStates, BinaryTreeNode root) {

    this.agentHandler = Main.getContainer().instance().select(AgentHandler.class).get();
    try {
      String defaultBusinessRules = agentHandler.getDefaultBusinessRules();

    if (businessType == null) {
      return null;
    }

    if (businessType.equals(BusinessType.FACTORY)) {

      return returnFactoryWithPlayerOrAgent(businesses, initialBusinessStates, defaultBusinessRules);
    }
    if (businessType.equals(BusinessType.REGIONAL_WAREHOUSE)) {

      return returnRegionalWarehouseWithPlayerOrAgent(businesses, initialBusinessStates, root, defaultBusinessRules);
    }

    if (businessType.equals(BusinessType.WHOLESALE)) {
      return returnWholeSaleWithPlayerOrAgent(businesses, initialBusinessStates, root, defaultBusinessRules);
    }

    if (businessType.equals(BusinessType.RETAIL)) {
      return returnRetailWithPlayerOrAgent(businesses, initialBusinessStates, root, defaultBusinessRules);
    }

    } catch (AgentException e) {
      e.printStackTrace();
    }
    return null;
  }

  private Business returnRetailWithPlayerOrAgent(List<Business> businesses,
      Map<Class<? extends Business>, BusinessState> initialBusinessStates, BinaryTreeNode root,
      String defaultBusinessRules) {
    RetailBusiness retailBusiness =
        (RetailBusiness) businesses
            .stream()
            .filter(b -> b instanceof RetailBusiness)
            .findAny()
            .orElseGet(() -> {
              Agent agent = createAgent(defaultBusinessRules);
              List<WholesaleBusiness> wholesaleBusinesses = new ArrayList<>();
              wholesaleBusinesses.add((WholesaleBusiness) root.getValue());
              return RetailBusinessBuilder
                  .aRetailBusiness()
                  .withAgent(agent)
                  .withBusinessState(initialBusinessStates.get(RetailBusiness.class))
                  .withUuid(UUID.randomUUID())
                  .withWholesaleBusinesses(wholesaleBusinesses)
                  .build();
            });
    if (retailBusiness.getPlayer() != null) {
      retailBusiness = RetailBusinessBuilder.fromBaseBusiness(retailBusiness)
          .withWholesaleBusinesses(new ArrayList<>()).build();
      retailBusiness.getWholesaleBusinesses().add((WholesaleBusiness) root.getValue());
    }
    return retailBusiness;
  }

  private Business returnWholeSaleWithPlayerOrAgent(List<Business> businesses,
      Map<Class<? extends Business>, BusinessState> initialBusinessStates, BinaryTreeNode root,
      String defaultBusinessRules) {
    WholesaleBusiness wholesaleBusiness =
        (WholesaleBusiness) businesses
            .stream()
            .filter(b -> b instanceof WholesaleBusiness)
            .findAny()
            .orElseGet(() -> {
              Agent agent = createAgent(defaultBusinessRules);
              List<RegionalWarehouseBusiness> regionalWarehouseBusinesses = new ArrayList<>();
              regionalWarehouseBusinesses.add((RegionalWarehouseBusiness) root.getValue());
              return WholesaleBusinessBuilder
                  .aWholesaleBusiness()
                  .withAgent(agent)
                  .withBusinessState(initialBusinessStates.get(WholesaleBusiness.class))
                  .withUuid(UUID.randomUUID())
                  .withRegionalWarehouseBusinesses(regionalWarehouseBusinesses)
                  .build();
            });
    if (wholesaleBusiness.getPlayer() != null) {
      wholesaleBusiness = WholesaleBusinessBuilder.fromBaseBusiness(wholesaleBusiness)
          .withRegionalWarehouseBusinesses(new ArrayList<>()).build();
      wholesaleBusiness.getRegionalWarehouseBusinesses()
          .add((RegionalWarehouseBusiness) root.getValue());
    }
    return wholesaleBusiness;
  }

  private Business returnRegionalWarehouseWithPlayerOrAgent(List<Business> businesses,
      Map<Class<? extends Business>, BusinessState> initialBusinessStates, BinaryTreeNode root,
      String defaultBusinessRules) {
    RegionalWarehouseBusiness regionalWarehouseBusiness =
        (RegionalWarehouseBusiness) businesses
            .stream()
            .filter(b -> b instanceof RegionalWarehouseBusiness)
            .findAny()
            .orElseGet(() -> {
              Agent agent = createAgent(defaultBusinessRules);
              List<FactoryBusiness> factoryBusinesses = new ArrayList<>();
              factoryBusinesses.add((FactoryBusiness) root.getValue());
              return RegionalWarehouseBusinessBuilder
                  .aRegionalWarehouseBusiness()
                  .withAgent(agent)
                  .withBusinessState(initialBusinessStates.get(RegionalWarehouseBusiness.class))
                  .withUuid(UUID.randomUUID())
                  .withFactoryBusinesses(factoryBusinesses)
                  .build();
            });
    if (regionalWarehouseBusiness.getPlayer() != null) {
      regionalWarehouseBusiness = RegionalWarehouseBusinessBuilder
          .fromBaseBusiness(regionalWarehouseBusiness).withFactoryBusinesses(new ArrayList<>())
          .build();
      regionalWarehouseBusiness.getFactoryBusinesses().add((FactoryBusiness) root.getValue());
    }
    return regionalWarehouseBusiness;
  }

  private Business returnFactoryWithPlayerOrAgent(List<Business> businesses,
      Map<Class<? extends Business>, BusinessState> initialBusinessStates,
      String defaultBusinessRules) {
    return
        businesses.stream()
            .filter(b -> b instanceof FactoryBusiness)
            .findAny()
            .orElseGet(() -> {
              Agent agent = createAgent(defaultBusinessRules);
              return FactoryBusinessBuilder
                  .aFactoryBusiness()
                  .withAgent(agent)
                  .withBusinessState(initialBusinessStates.get(FactoryBusiness.class))
                  .withUuid(UUID.randomUUID())
                  .build();
            });
  }

  private Agent createAgent(String rules) {
    return AgentBuilder.anAgent().withBusinessRules(rules).build();
  }

}
