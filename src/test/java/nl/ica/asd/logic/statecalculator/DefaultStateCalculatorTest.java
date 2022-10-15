package nl.ica.asd.logic.statecalculator;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

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
import nl.ica.asd.storage.domain.GameConfiguration;
import nl.ica.asd.storage.domain.GamePrices;
import nl.ica.asd.storage.exception.UnknownBusinessException;
import nl.ica.asd.storage.exception.UnknownGameException;
import nl.ica.asd.storage.gameconfig.GameConfig;
import nl.ica.asd.storage.gamedatastorage.GameDataAccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DefaultStateCalculatorTest {

  @Mock
  private GameDataAccess gameDataAccess;
  @Mock
  private GameConfig gameConfig;

  @InjectMocks
  private DefaultStateCalculator stateCalculator;

  private BeerGame beerGame;
  private GameConfiguration gameConfiguration;

  private FactoryBusiness factoryBusiness;
  private RegionalWarehouseBusiness regionalWarehouseBusiness;
  private WholesaleBusiness wholesaleBusiness;
  private RetailBusiness retailBusiness;

  private Player factoryPlayer;
  private Player regionalWarehousePlayer;
  private Player wholesalePlayer;
  private Player retailPlayer;

  private Agent factoryAgent;
  private Agent regionalWarehouseAgent;
  private Agent wholesaleAgent;
  private Agent retailAgent;

  private GamePrices factoryGamePrices;
  private GamePrices regionalWarehouseGamePrices;
  private GamePrices wholesaleGamePrices;
  private GamePrices retailGamePrices;

  private final Set<Player> players = new HashSet<>();
  private final List<Business> businesses = new ArrayList<>();
  private final List<FactoryBusiness> factoryBusinesses = new ArrayList<>();
  private final List<RegionalWarehouseBusiness> regionalWarehouseBusinesses = new ArrayList<>();
  private final List<WholesaleBusiness> wholesaleBusinesses = new ArrayList<>();

  private final int currentRound = 1;
  private final UUID factoryUuid = UUID.fromString("9d0d712e-b19e-4359-8e5e-1cd4050519e5");
  private final UUID regionalWarehouseUuid = UUID
      .fromString("106025cb-f7ac-4d6a-86b4-833e71e7fb53");
  private final UUID wholesaleUuid = UUID.fromString("49b7f29c-331c-4dfb-9636-f86b8daf7b15");
  private final UUID retailUuid = UUID.fromString("e8ccdf87-a19f-4501-b781-6b4214b41152");

  @BeforeEach
  public void setup() throws UnknownBusinessException, UnknownGameException {
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

      int amount = 5;
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

    gameConfiguration = new GameConfiguration(players.size(), currentRound,
        SupplyChainType.LINEAR, 0, GameVisibilityType.INVISIBLE, initialStates);
    beerGame = new BeerGame(currentRound, "TestingBeerGame", new Date(4362345), businesses,
        players, new Date(), GameState.STARTED, gameConfiguration);
  }

  private void mockPrices(GamePrices gamePrices, BusinessType type) throws UnknownGameException {
    when(gameDataAccess.getBeerGame()).thenReturn(beerGame);
    gamePrices = new GamePrices(1, 5, 2, 3);
    doReturn(gamePrices).when(gameConfig).getGamePricesForBusiness(type);
  }

  private void mockAllPrices() throws UnknownGameException {
    mockPrices(factoryGamePrices, factoryBusiness.getBusinessType());
    mockPrices(regionalWarehouseGamePrices, regionalWarehouseBusiness.getBusinessType());
    mockPrices(wholesaleGamePrices, wholesaleBusiness.getBusinessType());
    mockPrices(retailGamePrices, retailBusiness.getBusinessType());
  }

  @Test
  void getBusinessStateForBusiness_should_throwException_when_businessNotFound()
      throws UnknownGameException, UnknownBusinessException {
    when(gameDataAccess.getBusinessByUUID(any())).thenThrow(UnknownBusinessException.class);
    final UUID uuid = UUID.randomUUID();
    assertThrows(UnknownBusinessException.class,
        () -> stateCalculator.getBusinessStateForBusiness(uuid, 0));
  }

//  @Test
//  void getBusinessStateForBusiness_should_returnWholesaleStatus_whenWholesaleUuidIsPassed()
//      throws UnknownBusinessException, UnknownGameException {
//    when(gameDataAccess.getBeerGame()).thenReturn(beerGame);
//    when(gameDataAccess.getBusinessByUUID(wholesaleUuid)).thenReturn(wholesaleBusiness);
//
//    mockPrices(wholesaleGamePrices, wholesaleBusiness.getBusinessType());
//
//    final BusinessState wholesaleState = stateCalculator
//        .getBusinessStateForBusiness(wholesaleUuid, currentRound);
//
//    assertEquals(10, wholesaleState.getStock());
//    assertEquals(112, wholesaleState.getBudget());
//  }

//  @Test
//  void getBusinessStateForBusiness_should_returnFactoryStatus_whenFactoryUuidIsPassed()
//      throws UnknownBusinessException, UnknownGameException {
//    when(gameDataAccess.getBeerGame()).thenReturn(beerGame);
//    when(gameDataAccess.getBusinessByUUID(factoryUuid)).thenReturn(factoryBusiness);
//
//    mockPrices(factoryGamePrices, factoryBusiness.getBusinessType());
//
//    final BusinessState factoryState = stateCalculator
//        .getBusinessStateForBusiness(factoryUuid, currentRound);
//    assertEquals(20, factoryState.getStock());
//    assertEquals(192, factoryState.getBudget());
//  }

//  @Test
//  void getBusinessStateForBusiness_should_returnRetail_whenRetailUuidIsPassed()
//      throws UnknownBusinessException, UnknownGameException {
//    when(gameDataAccess.getBeerGame()).thenReturn(beerGame);
//    when(gameDataAccess.getBusinessByUUID(retailUuid)).thenReturn(retailBusiness);
//
//    mockPrices(retailGamePrices, retailBusiness.getBusinessType());
//
//    final BusinessState retailState = stateCalculator
//        .getBusinessStateForBusiness(retailUuid, currentRound);
//    assertEquals(5, retailState.getStock());
//    assertEquals(72, retailState.getBudget());
//  }


  @Test
  void getBusinessStatesForRound_should_throwUnknownGameException_when_beerGameNotSet()
      throws UnknownGameException {
    when(gameDataAccess.getBeerGame()).thenThrow(UnknownGameException.class);
    assertThrows(UnknownGameException.class,
        () -> stateCalculator.getBusinessStatesForRound(currentRound));
  }

//  @Test
//  void getBusinessStatesForRound_should_returnAllBusinessStatesOfBeerGame()
//      throws UnknownBusinessException, UnknownGameException {
//    when(gameDataAccess.getBeerGame()).thenReturn(beerGame);
//    when(gameDataAccess.getBusinessByUUID(wholesaleUuid)).thenReturn(wholesaleBusiness);
//
//    mockAllPrices();
//
//    final Map<UUID, BusinessState> businessStatesMap = stateCalculator
//        .getBusinessStatesForRound(currentRound);
//    final BusinessState wholesaleBusinessState = stateCalculator
//        .getBusinessStateForBusiness(wholesaleUuid, currentRound);
//
//    assertEquals(businessStatesMap.size(), businesses.size());
//    assertEquals(wholesaleBusinessState.getStock(),
//        businessStatesMap.get(wholesaleUuid).getStock());
//    assertEquals(wholesaleBusinessState.getBudget(),
//        businessStatesMap.get(wholesaleUuid).getBudget());
//    assertEquals(wholesaleBusinessState.getIncomingGoods(),
//        businessStatesMap.get(wholesaleUuid).getIncomingGoods());
//    assertEquals(wholesaleBusinessState.getOutgoingGoods(),
//        businessStatesMap.get(wholesaleUuid).getOutgoingGoods());
//    assertEquals(wholesaleBusinessState.getIncomingOrders(),
//        businessStatesMap.get(wholesaleUuid).getIncomingOrders());
//    assertEquals(wholesaleBusinessState.getOpenOrders(),
//        businessStatesMap.get(wholesaleUuid).getOpenOrders());
//    assertNotNull(businessStatesMap.get(factoryUuid));
//    assertNotNull(businessStatesMap.get(regionalWarehouseUuid));
//    assertNotNull(businessStatesMap.get(wholesaleUuid));
//    assertNotNull(businessStatesMap.get(retailUuid));
//  }

  @Test
  void getBusinessStatesForPlayer_should_throwUnknownGameException_when_beerGameNotSet()
      throws UnknownGameException {
    when(gameDataAccess.getPlayerList()).thenThrow(UnknownGameException.class);
    assertThrows(UnknownGameException.class,
        () -> stateCalculator.getBusinessStatesForPlayer(regionalWarehousePlayer.getUsername()));
  }

  @Test
  void getBusinessStatesForPlayer_should_returnEmptyMap_when_playerNotFound()
      throws UnknownBusinessException, UnknownGameException {
    final Map<Integer, BusinessState> businessStateMap = stateCalculator
        .getBusinessStatesForPlayer(regionalWarehousePlayer.getUsername() + "-fake");
    assertEquals(0, businessStateMap.size());
  }

//  @Test
//  void getBusinessStatesForPlayer_should_returnCorrectBusinessStateForPlayer()
//      throws UnknownGameException, UnknownBusinessException {
//    when(gameDataAccess.getBeerGame()).thenReturn(beerGame);
//    when(gameDataAccess.getPlayerList()).thenReturn(players);
//    when(gameDataAccess.getBusinessByUUID(regionalWarehouseUuid))
//        .thenReturn(regionalWarehouseBusiness);
//
//    mockPrices(regionalWarehouseGamePrices, regionalWarehouseBusiness.getBusinessType());
//
//    final Map<Integer, BusinessState> businessStateMap = stateCalculator
//        .getBusinessStatesForPlayer(regionalWarehousePlayer.getUsername());
//    final BusinessState regionalWarehouseState = stateCalculator
//        .getBusinessStateForBusiness(regionalWarehouseUuid, currentRound);
//
//    assertEquals(currentRound, businessStateMap.size());
//    assertEquals(regionalWarehouseState.getStock(), businessStateMap.get(currentRound).getStock());
//    assertEquals(regionalWarehouseState.getOpenOrders(),
//        businessStateMap.get(currentRound).getOpenOrders());
//    assertEquals(regionalWarehouseState.getIncomingOrders(),
//        businessStateMap.get(currentRound).getIncomingOrders());
//    assertEquals(regionalWarehouseState.getOutgoingGoods(),
//        businessStateMap.get(currentRound).getOutgoingGoods());
//    assertEquals(regionalWarehouseState.getIncomingGoods(),
//        businessStateMap.get(currentRound).getIncomingGoods());
//    assertEquals(regionalWarehouseState.getBudget(),
//        businessStateMap.get(currentRound).getBudget());
//  }

  @Test
  void getAllBusinessStates_should_throwUnknownGameException_when_beerGameNotSet()
      throws UnknownGameException {
    when(gameDataAccess.getBeerGame()).thenThrow(UnknownGameException.class);
    assertThrows(UnknownGameException.class,
        () -> stateCalculator.getAllBusinessStates());
  }

//  @Test
//  void getAllBusinessStates_should_returnCorrectBusinessStates()
//      throws UnknownGameException, UnknownBusinessException {
//    when(gameDataAccess.getBeerGame()).thenReturn(beerGame);
//    when(gameDataAccess.getBusinessByUUID(factoryUuid)).thenReturn(factoryBusiness);
//
//    mockAllPrices();
//
//    final Map<Integer, Map<UUID, BusinessState>> businessStateMap = stateCalculator
//        .getAllBusinessStates();
//    final BusinessState factoryState = stateCalculator
//        .getBusinessStateForBusiness(factoryUuid, currentRound);
//
//    assertEquals(currentRound, businessStateMap.size());
//    assertEquals(businesses.size(), businessStateMap.get(currentRound).size());
//    assertEquals(factoryState.getStock(),
//        businessStateMap.get(currentRound).get(factoryUuid).getStock());
//  }
}
