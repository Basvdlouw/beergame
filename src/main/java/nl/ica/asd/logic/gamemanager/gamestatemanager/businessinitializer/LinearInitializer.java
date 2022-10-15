package nl.ica.asd.logic.gamemanager.gamestatemanager.businessinitializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.inject.Inject;
import nl.ica.asd.Main;
import nl.ica.asd.agenthandler.AgentHandler;
import nl.ica.asd.agenthandler.exceptions.AgentException;
import nl.ica.asd.logic.domain.Action;
import nl.ica.asd.logic.domain.Agent;
import nl.ica.asd.logic.domain.BeerGame;
import nl.ica.asd.logic.domain.Business;
import nl.ica.asd.logic.domain.actions.DeliveryAction;
import nl.ica.asd.logic.domain.actions.OrderAction;
import nl.ica.asd.logic.domain.builders.FactoryBusinessBuilder;
import nl.ica.asd.logic.domain.builders.RegionalWarehouseBusinessBuilder;
import nl.ica.asd.logic.domain.builders.RetailBusinessBuilder;
import nl.ica.asd.logic.domain.builders.WholesaleBusinessBuilder;
import nl.ica.asd.logic.domain.businesses.FactoryBusiness;
import nl.ica.asd.logic.domain.businesses.RegionalWarehouseBusiness;
import nl.ica.asd.logic.domain.businesses.RetailBusiness;
import nl.ica.asd.logic.domain.businesses.WholesaleBusiness;
import nl.ica.asd.logic.statecalculator.BusinessState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LinearInitializer extends BusinessInitializer {

  private static final Logger logger = LoggerFactory.getLogger(LinearInitializer.class);

  @Inject
  private AgentHandler agentHandler;

  public LinearInitializer() {
    super();
  }

  public LinearInitializer init() {
    this.agentHandler = Main.getContainer().instance().select(AgentHandler.class).get();
    return this;
  }

  @Override
  public List<Business> initialize(BeerGame beerGame, List<Business> businesses) {
    final Map<Class<? extends Business>, BusinessState> initialBusinessStates =
        beerGame.getGameConfiguration().getInitialBusinessStates();

    try {
      String defaultBusinessRules = agentHandler.getDefaultBusinessRules();

      final FactoryBusiness factoryBusiness = (FactoryBusiness) businesses.stream()
          .filter(b -> b instanceof FactoryBusiness)
          .findAny()
          .orElseGet(() -> {
            Agent agent = createAgent(defaultBusinessRules);
            return FactoryBusinessBuilder
                .aFactoryBusiness()
                .withAgent(agent)
                .withBusinessState(initialBusinessStates.get(FactoryBusiness.class))
                .withUuid(generateBusinessUUID(beerGame))
                .build();
          });
      final RegionalWarehouseBusiness regionalWarehouseBusiness = (RegionalWarehouseBusiness) businesses
          .stream()
          .filter(b -> b instanceof RegionalWarehouseBusiness)
          .findAny()
          .orElseGet(() -> {
            Agent agent = createAgent(defaultBusinessRules);
            return RegionalWarehouseBusinessBuilder
                .aRegionalWarehouseBusiness()
                .withAgent(agent)
                .withBusinessState(initialBusinessStates.get(RegionalWarehouseBusiness.class))
                .withUuid(generateBusinessUUID(beerGame))
                .withFactoryBusinesses(Collections.singletonList(factoryBusiness))
                .build();
          });
      final WholesaleBusiness wholesaleBusiness = (WholesaleBusiness) businesses.stream()
          .filter(b -> b instanceof WholesaleBusiness)
          .findAny()
          .orElseGet(() -> {
            Agent agent = createAgent(defaultBusinessRules);
            return WholesaleBusinessBuilder
                .aWholesaleBusiness()
                .withAgent(agent)
                .withBusinessState(initialBusinessStates.get(WholesaleBusiness.class))
                .withUuid(generateBusinessUUID(beerGame))
                .withRegionalWarehouseBusinesses(
                    Collections.singletonList(regionalWarehouseBusiness))
                .build();
          });
      final RetailBusiness retailBusiness = (RetailBusiness) businesses.stream()
          .filter(b -> b instanceof RetailBusiness)
          .findAny()
          .orElseGet(() -> {
            Agent agent = createAgent(defaultBusinessRules);
            return RetailBusinessBuilder
                .aRetailBusiness()
                .withAgent(agent)
                .withBusinessState(initialBusinessStates.get(RetailBusiness.class))
                .withUuid(generateBusinessUUID(beerGame))
                .withWholesaleBusinesses(Collections.singletonList(wholesaleBusiness))
                .build();
          });

      return fixInitialOrdersAndDeliveries(beerGame.getGameConfiguration().getInitialBusinessStates(), bindToBusiness(Arrays
          .asList(factoryBusiness, regionalWarehouseBusiness, wholesaleBusiness, retailBusiness)));

    } catch (AgentException e) {
      logger.info(e.getMessage());
    }
    return Collections.emptyList();
  }

  private List<Business> bindToBusiness(List<Business> businesses) {
    List<Business> businessList = new ArrayList<>();

    final FactoryBusiness fb = (FactoryBusiness) businesses.stream()
        .filter(x -> x instanceof FactoryBusiness).findFirst().orElse(null);
    businessList.add(fb);

    RegionalWarehouseBusiness rwb = (RegionalWarehouseBusiness) businesses.stream()
        .filter(x -> x instanceof RegionalWarehouseBusiness).findFirst().orElse(null);
    WholesaleBusiness wb = (WholesaleBusiness) businesses.stream()
        .filter(x -> x instanceof WholesaleBusiness).findFirst().orElse(null);
    RetailBusiness rb = (RetailBusiness) businesses.stream()
        .filter(x -> x instanceof RetailBusiness).findFirst().orElse(null);

    if (rb != null && (rb.getWholesaleBusinesses() == null || rb.getWholesaleBusinesses()
        .isEmpty())) {
      final List<WholesaleBusiness> wsb = new ArrayList<>();
      wsb.add(wb);
      rb = RetailBusinessBuilder.fromBaseBusiness(rb)
          .withWholesaleBusinesses(wsb).build();
    }
    businessList.add(rb);

    if (wb != null && (wb.getRegionalWarehouseBusinesses() == null || wb
        .getRegionalWarehouseBusinesses().isEmpty())) {
      final List<RegionalWarehouseBusiness> rwbs = new ArrayList<>();
      rwbs.add(rwb);
      wb = WholesaleBusinessBuilder.fromBaseBusiness(wb)
          .withRegionalWarehouseBusinesses(rwbs).build();
    }
    businessList.add(wb);

    if (rwb != null && (rwb.getFactoryBusinesses() == null || rwb.getFactoryBusinesses()
        .isEmpty())) {
      final List<FactoryBusiness> fbs = new ArrayList<>();
      fbs.add(fb);
      rwb = RegionalWarehouseBusinessBuilder
          .fromBaseBusiness(rwb).withFactoryBusinesses(fbs).build();
    }

    businessList.add(rwb);

    return businessList;
  }

  private List<Business> fixInitialOrdersAndDeliveries(Map<Class<? extends Business>, BusinessState> initialBusinessStates, List<Business> businesses) {
    List<Business> businessesWithInitialState = new ArrayList<>();

    for (Business business : businesses) {
      BusinessState businessState = initialBusinessStates.get(business.getClass());

      Business lowerLevelBusiness = getLowerLevelBusinesses(business, businesses);
      UUID lowerLevelBusinessUUID = lowerLevelBusiness.equals(business)
          ? ((RetailBusiness) lowerLevelBusiness).getCustomerUUID()
          : lowerLevelBusiness.getUUID();

      Business upperLevelBusiness = getUpperLevelBusinesses(business);
      UUID upperLevelBusinessUUID = upperLevelBusiness.equals(business)
          ? ((FactoryBusiness) upperLevelBusiness).getSupplierUUID()
          : upperLevelBusiness.getUUID();

      List<Action> initialRoundActions = new ArrayList<>();
      initialRoundActions.add(new OrderAction(businessState.getIncomingOrders(), lowerLevelBusinessUUID, business.getUUID())); // incoming order
      initialRoundActions.add(new OrderAction(businessState.getOutgoingOrders(), business.getUUID(), upperLevelBusinessUUID)); // outgoing order

      initialRoundActions.add(new DeliveryAction(businessState.getIncomingGoods(), upperLevelBusinessUUID, business.getUUID())); // incoming goods
      initialRoundActions.add(new DeliveryAction(businessState.getOutgoingGoods(), business.getUUID(), lowerLevelBusinessUUID)); // outgoing goods

      business.getActionsPerRound().put(0, initialRoundActions);

      List<Action> firstRoundActions = new ArrayList<>();
      firstRoundActions.add(new OrderAction(businessState.getIncomingOrders(), lowerLevelBusinessUUID, business.getUUID())); // incoming order

      firstRoundActions.add(new DeliveryAction(businessState.getIncomingGoods(), upperLevelBusinessUUID, business.getUUID())); // incoming goods

      business.getActionsPerRound().put(1, firstRoundActions);

      if (business instanceof FactoryBusiness) {
        List<Action> secondRoundActions = new ArrayList<>();
        secondRoundActions.add(new DeliveryAction(businessState.getIncomingGoods(), upperLevelBusinessUUID, business.getUUID())); // incoming goods

        business.getActionsPerRound().put(2, secondRoundActions);
      }

      businessesWithInitialState.add(business.getBusinessType().getBuilderFromBusiness(business).withBusinessState(businessState).build());
    }
    return businessesWithInitialState;
  }

  private Business getUpperLevelBusinesses(Business business) {
    switch (business.getBusinessType()) {
      case RETAIL:
        return ((RetailBusiness) business).getWholesaleBusinesses().get(0);
      case WHOLESALE:
        return ((WholesaleBusiness) business).getRegionalWarehouseBusinesses().get(0);
      case REGIONAL_WAREHOUSE:
        return ((RegionalWarehouseBusiness) business).getFactoryBusinesses().get(0);
      case FACTORY:
        return business;
    }
    return null;
  }

  private Business getLowerLevelBusinesses(Business business, List<Business> businesses) {
    switch (business.getBusinessType()) {
      case RETAIL:
        return business;
      case WHOLESALE:
        return businesses.stream()
            .filter(x -> x instanceof RetailBusiness)
            .filter(x -> getUpperLevelBusinesses(x).equals(business))
            .findFirst()
            .get();
      case REGIONAL_WAREHOUSE:
        return businesses.stream()
            .filter(x -> x instanceof WholesaleBusiness)
            .filter(x -> getUpperLevelBusinesses(x).equals(business))
            .findFirst()
            .get();
      case FACTORY:
        return businesses.stream()
            .filter(x -> x instanceof RegionalWarehouseBusiness)
            .filter(x -> getUpperLevelBusinesses(x).equals(business))
            .findFirst()
            .get();
    }
    return null;
  }
}
