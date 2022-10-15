package nl.ica.asd.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import nl.ica.asd.logic.domain.Action;
import nl.ica.asd.logic.domain.Agent;
import nl.ica.asd.logic.domain.BeerGame;
import nl.ica.asd.logic.domain.Business;
import nl.ica.asd.logic.domain.BusinessType;
import nl.ica.asd.logic.domain.GameState;
import nl.ica.asd.logic.domain.GameVisibilityType;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.logic.domain.PlayerStatus;
import nl.ica.asd.logic.domain.SupplyChainType;
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
import nl.ica.asd.storage.domain.GameConfiguration;
import nl.ica.asd.storage.domain.GamePrices;

public class TestHelpers {

  private static BeerGame beerGame;
  private static GameConfiguration gameConfiguration;

  private static FactoryBusiness factoryBusiness;
  private static RegionalWarehouseBusiness regionalWarehouseBusiness;
  private static WholesaleBusiness wholesaleBusiness;
  private static RetailBusiness retailBusiness;

  private static Player factoryPlayer;
  private static Player regionalWarehousePlayer;
  private static Player wholesalePlayer;
  private static Player retailPlayer;

  private static Agent factoryAgent;
  private static Agent regionalWarehouseAgent;
  private static Agent wholesaleAgent;
  private static Agent retailAgent;

  private static GamePrices factoryGamePrices;
  private static GamePrices regionalWarehouseGamePrices;
  private static GamePrices wholesaleGamePrices;
  private static GamePrices retailGamePrices;

  private static final Set<Player> players = new HashSet<>();
  private static final List<Business> businesses = new ArrayList<>();
  private static final List<FactoryBusiness> factoryBusinesses = new ArrayList<>();
  private static final List<RegionalWarehouseBusiness> regionalWarehouseBusinesses = new ArrayList<>();
  private static final List<WholesaleBusiness> wholesaleBusinesses = new ArrayList<>();

  private static final int currentRound = 30;
  private static final UUID factoryUuid = UUID.fromString("9d0d712e-b19e-4359-8e5e-1cd4050519e5");
  private static final UUID regionalWarehouseUuid = UUID
      .fromString("106025cb-f7ac-4d6a-86b4-833e71e7fb53");
  private static final UUID wholesaleUuid = UUID.fromString("49b7f29c-331c-4dfb-9636-f86b8daf7b15");
  private static final UUID retailUuid = UUID.fromString("e8ccdf87-a19f-4501-b781-6b4214b41152");

  private TestHelpers() {
  }

  public static BeerGame createTestingBeerGame() {
    players.clear();
    businesses.clear();
    factoryBusinesses.clear();
    regionalWarehouseBusinesses.clear();
    wholesaleBusinesses.clear();

    factoryPlayer = new Player("factoryUsername", factoryUuid, BusinessType.FACTORY,
        PlayerStatus.CONNECTED_AND_ACTIVE, "127.0.01.123", 7777);
    regionalWarehousePlayer = new Player("regionalWarehouseUsername", regionalWarehouseUuid,
        BusinessType.REGIONAL_WAREHOUSE,
        PlayerStatus.CONNECTED_AND_ACTIVE, "127.0.01.124", 7777);
    wholesalePlayer = new Player("wholesaleUsername", wholesaleUuid, BusinessType.WHOLESALE,
        PlayerStatus.CONNECTED_AND_ACTIVE, "127.0.01.125", 7777);
    retailPlayer = new Player("retailUsername", retailUuid, BusinessType.RETAIL,
        PlayerStatus.CONNECTED_AND_ACTIVE, "127.0.01.126", 7777);

    players.add(factoryPlayer);
    players.add(regionalWarehousePlayer);
    players.add(wholesalePlayer);
    players.add(retailPlayer);

    factoryAgent = new Agent("Order = 20");
    regionalWarehouseAgent = new Agent("Order = 15");
    wholesaleAgent = new Agent("Order = 10");
    retailAgent = new Agent("Order = 5");

    final Map<Class<? extends Business>, BusinessState> initialStates = new HashMap<>();
    initialStates.put(FactoryBusiness.class, new BusinessState(20, 0, 3, 3, 3, 200, 3, 0, 0));
    initialStates.put(RegionalWarehouseBusiness.class, new BusinessState(15, 0, 3, 3, 3, 150, 3, 0, 0));
    initialStates.put(WholesaleBusiness.class, new BusinessState(10, 0, 3, 3, 3, 100, 3, 0, 0));
    initialStates.put(RetailBusiness.class, new BusinessState(5, 0, 3, 3, 3, 50, 3, 0, 0));

    factoryBusiness = new FactoryBusiness(
        initialStates.get(FactoryBusiness.class).getBudget(),
        initialStates.get(FactoryBusiness.class).getStock(), Collections.emptyMap(),
        factoryAgent, factoryUuid, factoryPlayer, UUID.randomUUID());
    factoryBusinesses.add(factoryBusiness);

    regionalWarehouseBusiness = new RegionalWarehouseBusiness(
        initialStates.get(RegionalWarehouseBusiness.class).getBudget(),
        initialStates.get(RegionalWarehouseBusiness.class).getStock(), Collections.emptyMap(),
        regionalWarehouseAgent, regionalWarehouseUuid, regionalWarehousePlayer, factoryBusinesses);
    regionalWarehouseBusinesses.add(regionalWarehouseBusiness);

    wholesaleBusiness = new WholesaleBusiness(
        initialStates.get(WholesaleBusiness.class).getBudget(),
        initialStates.get(WholesaleBusiness.class).getStock(), Collections.emptyMap(),
        wholesaleAgent, wholesaleUuid, wholesalePlayer, regionalWarehouseBusinesses);
    wholesaleBusinesses.add(wholesaleBusiness);

    retailBusiness = new RetailBusiness(
        initialStates.get(RetailBusiness.class).getBudget(),
        initialStates.get(RetailBusiness.class).getStock(), Collections.emptyMap(),
        retailAgent, retailUuid, retailPlayer, wholesaleBusinesses, UUID.randomUUID());

    final Map<Integer, List<Action>> factoryActions = new HashMap<>();
    final Map<Integer, List<Action>> regionalWarehouseActions = new HashMap<>();
    final Map<Integer, List<Action>> wholesaleActions = new HashMap<>();
    final Map<Integer, List<Action>> retailActions = new HashMap<>();

    for (int i = 1; i <= currentRound; i++) {
      factoryActions.put(i, new ArrayList<>());
      regionalWarehouseActions.put(i, new ArrayList<>());
      wholesaleActions.put(i, new ArrayList<>());
      retailActions.put(i, new ArrayList<>());

      int amount = i * 3 % 8;
      // outgoing goods
      factoryActions.get(i)
          .add(new DeliveryAction(amount, factoryBusiness.getUUID(),
              regionalWarehouseBusiness.getUUID()));
      regionalWarehouseActions.get(i)
          .add(new DeliveryAction(amount, regionalWarehouseBusiness.getUUID(),
              wholesaleBusiness.getUUID()));
      wholesaleActions.get(i)
          .add(new DeliveryAction(amount, wholesaleBusiness.getUUID(), retailBusiness.getUUID()));
      retailActions.get(i).add(new DeliveryAction(amount, retailBusiness.getUUID(), null));

      //incoming goods
      factoryActions.get(i)
          .add(new DeliveryAction(amount, null, factoryBusiness.getUUID()));
      regionalWarehouseActions.get(i)
          .add(new DeliveryAction(amount, factoryBusiness.getUUID(),
              regionalWarehouseBusiness.getUUID()));
      wholesaleActions.get(i)
          .add(new DeliveryAction(amount, regionalWarehouseBusiness.getUUID(),
              wholesaleBusiness.getUUID()));
      retailActions.get(i)
          .add(new DeliveryAction(amount, wholesaleBusiness.getUUID(), retailBusiness.getUUID()));

      // incoming orders
      factoryActions.get(i)
          .add(new OrderAction(amount, regionalWarehouseBusiness.getUUID(),
              factoryBusiness.getUUID()));
      regionalWarehouseActions.get(i)
          .add(new OrderAction(amount, wholesaleBusiness.getUUID(),
              regionalWarehouseBusiness.getUUID()));
      wholesaleActions.get(i)
          .add(new OrderAction(amount, regionalWarehouseBusiness.getUUID(),
              wholesaleBusiness.getUUID()));
      retailActions.get(i).add(new OrderAction(amount, null, retailBusiness.getUUID()));
    }

    factoryBusiness = FactoryBusinessBuilder.fromBaseBusiness(factoryBusiness)
        .withActionsPerRound(factoryActions).build();
    regionalWarehouseBusiness = RegionalWarehouseBusinessBuilder
        .fromBaseBusiness(regionalWarehouseBusiness).withActionsPerRound(regionalWarehouseActions)
        .build();
    wholesaleBusiness = WholesaleBusinessBuilder.fromBaseBusiness(wholesaleBusiness)
        .withActionsPerRound(wholesaleActions).build();
    retailBusiness = RetailBusinessBuilder.fromBaseBusiness(retailBusiness)
        .withActionsPerRound(retailActions).build();

    businesses.add(factoryBusiness);
    businesses.add(regionalWarehouseBusiness);
    businesses.add(wholesaleBusiness);
    businesses.add(retailBusiness);

    gameConfiguration = new GameConfiguration(players.size(), currentRound + 2,
        SupplyChainType.LINEAR, 0, GameVisibilityType.INVISIBLE, initialStates);
    beerGame = new BeerGame(currentRound + 1, "TestingBeerGame", new Date(4362345), businesses,
        players, new Date(), GameState.STARTED, gameConfiguration);
    return beerGame;
  }

  public static <T> void resetSingletonMock(T mock, String instanceName) {
    try {
      Field instance = mock.getClass().getDeclaredField(instanceName);
      instance.setAccessible(true);
      instance.set(null, null);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static <T> T singletonMock(T mock, String instanceName) {
    try {
      Field instance = mock.getClass().getDeclaredField(instanceName);
      instance.setAccessible(true);
      instance.set(instance, mock);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return mock;
  }
}

