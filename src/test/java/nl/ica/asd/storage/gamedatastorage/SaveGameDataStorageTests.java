package nl.ica.asd.storage.gamedatastorage;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
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
import nl.ica.asd.logic.domain.BusinessRules;
import nl.ica.asd.logic.domain.BusinessType;
import nl.ica.asd.logic.domain.GameState;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.logic.domain.PlayerStatus;
import nl.ica.asd.logic.domain.actions.OrderAction;
import nl.ica.asd.logic.domain.businesses.FactoryBusiness;
import nl.ica.asd.logic.domain.businesses.RegionalWarehouseBusiness;
import nl.ica.asd.logic.domain.businesses.RetailBusiness;
import nl.ica.asd.logic.domain.businesses.WholesaleBusiness;
import nl.ica.asd.storage.domain.GameConfiguration;
import nl.ica.asd.storage.exception.UnknownBusinessException;
import nl.ica.asd.storage.exception.UnknownException;
import nl.ica.asd.storage.exception.UnknownGameException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.collections.Sets;

public class SaveGameDataStorageTests {

  private static Set<Player> players;

  private static List<Business> businesses;

  @Mock
  static Agent agent;

  @Mock
  static BeerGame beerGame;

  @Mock
  Business business;

  @Mock
  FactoryBusiness factoryBusiness;

  @Mock
  WholesaleBusiness wholesaleBusiness;

  @Mock
  RegionalWarehouseBusiness regionalWarehouseBusiness;

  @Mock
  RetailBusiness retailBusiness;

  @BeforeAll
  public static void init() {
  }

  @BeforeEach
  public void setup() {
    MockitoAnnotations.initMocks(this);

    // Fill dummy data
    final UUID businessUUID = UUID.randomUUID();
    final Player player1 = new Player("FEM Student1", businessUUID, BusinessType.FACTORY,
        PlayerStatus.CONNECTED_AND_ACTIVE, "1",
        1);
    final Player player2 = new Player("FEM Student2", businessUUID, BusinessType.FACTORY,
        PlayerStatus.CONNECTED_AND_ACTIVE, "2",
        1);

    players = new HashSet<>();
    players.add(player1);
    players.add(player2);

    final Map<Integer, List<Action>> factoryBusinessActions = new HashMap<>();
    factoryBusinessActions.put(1, new ArrayList<>());

    final Business factoryBusiness = new FactoryBusiness(10, 10, factoryBusinessActions, agent,
        businessUUID, player1,
        UUID.randomUUID());

    businesses = new ArrayList<>();
    businesses.add(factoryBusiness);

    // Agent mock
    when(agent.getBusinessRules()).thenReturn("rule");

    when(beerGame.getName()).thenReturn("BeerTestingGame");
    when(beerGame.getDateTime())
        .thenReturn(new GregorianCalendar(2018, Calendar.MARCH, 12).getTime());
    when(beerGame.getBusinesses()).thenReturn(businesses);
    when(beerGame.getPlayers()).thenReturn(players);
  }

  @Test
  public void saveBusinessRules_should_throwUnknownBusinessException_when_savingWithWrongBusiness()
      throws UnknownException {
    final GameDataStorage dataStorage = mock(GameDataStorage.class);

    when(dataStorage.getBeerGame()).thenReturn(beerGame);

    final Business business = mock(FactoryBusiness.class);
    final Business notExistingBusiness = mock(FactoryBusiness.class);
    when(business.getAgent()).thenReturn(agent);

    when(business.getUUID()).thenReturn(UUID.randomUUID());

    final List<Business> businessesMock = new ArrayList<>();
    businessesMock.add(business);

    when(beerGame.getBusinesses()).thenReturn(businessesMock);

    final BusinessRules businessRules = new BusinessRules(notExistingBusiness, "rules");

    doCallRealMethod().when(dataStorage).saveBusinessRules(businessRules);

    Assertions.assertThrows(UnknownBusinessException.class,
        () -> dataStorage.saveBusinessRules(businessRules));
  }

  @Test
  public void saveBusinessRules_should_replaceOldBusinessRule_when_savingFactoryBusinessCorrectly()
      throws UnknownException {
    final GameDataStorage dataStorage = mock(GameDataStorage.class);

    when(dataStorage.getBeerGame()).thenReturn(beerGame);

    when(factoryBusiness.getUUID()).thenReturn(new UUID(1, 1));
    final ArrayList<Business> businesses = new ArrayList<>();
    businesses.add(factoryBusiness);
    when(beerGame.getBusinesses()).thenReturn(businesses);
    when(factoryBusiness.getAgent()).thenReturn(agent);

    final BusinessRules businessRules = new BusinessRules(factoryBusiness, "rules");

    doCallRealMethod().when(dataStorage).saveBusinessRules(businessRules);

    Assertions.assertDoesNotThrow(() -> dataStorage.saveBusinessRules(businessRules));
    verify(dataStorage, times(1)).saveBeerGameSettings(any(BeerGame.class));
  }

  @Test
  public void saveBusinessRules_should_replaceOldBusinessRule_when_savingWholeSaleBusinessCorrectly()
      throws UnknownException {
    final GameDataStorage dataStorage = mock(GameDataStorage.class);

    when(dataStorage.getBeerGame()).thenReturn(beerGame);

    when(wholesaleBusiness.getUUID()).thenReturn(new UUID(2, 2));
    final ArrayList<Business> businesses = new ArrayList<>();
    businesses.add(wholesaleBusiness);
    when(beerGame.getBusinesses()).thenReturn(businesses);
    when(wholesaleBusiness.getAgent()).thenReturn(agent);

    final BusinessRules businessRules = new BusinessRules(wholesaleBusiness, "rules");

    doCallRealMethod().when(dataStorage).saveBusinessRules(businessRules);

    Assertions.assertDoesNotThrow(() -> dataStorage.saveBusinessRules(businessRules));
    verify(dataStorage, times(1)).saveBeerGameSettings(any(BeerGame.class));
  }

  @Test
  public void saveBusinessRules_should_replaceOldBusinessRule_when_savingRegionalWarehouseCorrectly()
      throws UnknownException {
    final GameDataStorage dataStorage = mock(GameDataStorage.class);

    when(dataStorage.getBeerGame()).thenReturn(beerGame);

    when(regionalWarehouseBusiness.getUUID()).thenReturn(new UUID(3, 3));
    final ArrayList<Business> businesses = new ArrayList<>();
    businesses.add(regionalWarehouseBusiness);
    when(beerGame.getBusinesses()).thenReturn(businesses);
    when(regionalWarehouseBusiness.getAgent()).thenReturn(agent);

    final BusinessRules businessRules = new BusinessRules(regionalWarehouseBusiness, "rules");

    doCallRealMethod().when(dataStorage).saveBusinessRules(businessRules);

    Assertions.assertDoesNotThrow(() -> dataStorage.saveBusinessRules(businessRules));
    verify(dataStorage, times(1)).saveBeerGameSettings(any(BeerGame.class));
  }

  @Test
  public void saveBusinessRules_should_replaceOldBusinessRule_when_savingRetailBusinessCorrectly()
      throws UnknownException {
    final GameDataStorage dataStorage = mock(GameDataStorage.class);

    when(dataStorage.getBeerGame()).thenReturn(beerGame);

    when(retailBusiness.getUUID()).thenReturn(new UUID(4, 4));
    final ArrayList<Business> businesses = new ArrayList<>();
    businesses.add(retailBusiness);
    when(beerGame.getBusinesses()).thenReturn(businesses);
    when(retailBusiness.getAgent()).thenReturn(agent);

    final BusinessRules businessRules = new BusinessRules(retailBusiness, "rules");

    doCallRealMethod().when(dataStorage).saveBusinessRules(businessRules);

    Assertions.assertDoesNotThrow(() -> dataStorage.saveBusinessRules(businessRules));
    verify(dataStorage, times(1)).saveBeerGameSettings(any(BeerGame.class));
  }

  @Test
  public void saveCurrentActions_should_saveOrderActions_whenContainsOrderActions()
      throws UnknownGameException, UnknownBusinessException {
    final GameDataStorage dataStorage = mock(GameDataStorage.class);
    final OrderAction orderActionMock = mock(OrderAction.class);
    final List<Action> actions = new ArrayList<>();
    final Business business = businesses.get(0);

    when(dataStorage.getBusinessByUUID(business.getUUID())).thenReturn(business);
    when(orderActionMock.getSender()).thenReturn(business.getUUID());
    when(orderActionMock.getReceiver()).thenReturn(null);

    actions.add(orderActionMock);

    when(beerGame.getCurrentRound()).thenReturn(1);

    when(dataStorage.hasCurrentGame()).thenReturn(true);
    when(dataStorage.getBeerGame()).thenReturn(beerGame);

    doCallRealMethod().when(dataStorage).saveCurrentActions(actions, 1);
    assertDoesNotThrow(() -> dataStorage.saveCurrentActions(actions, 1));
    verify(dataStorage, times(1)).saveBeerGameSettings(any(BeerGame.class));
  }

  @Test
  public void saveCurrentActions_should_saveOrderActions_whenNotContainsOrderActions()
      throws UnknownGameException, UnknownBusinessException {
    final GameDataStorage dataStorage = mock(GameDataStorage.class);
    final OrderAction orderActionMock = mock(OrderAction.class);
    final List<Action> actions = new ArrayList<>();
    final Map<Integer, List<Action>> factoryBusinessActions = new HashMap<>();
    final List<Business> businesses = new ArrayList<>();
    final Business factoryBusiness = new FactoryBusiness(10, 10, factoryBusinessActions, agent,
        UUID.randomUUID(), mock(Player.class),
        UUID.randomUUID());

    businesses.add(factoryBusiness);

    when(dataStorage.getBusinessByUUID(factoryBusiness.getUUID())).thenReturn(factoryBusiness);
    when(orderActionMock.getSender()).thenReturn(factoryBusiness.getUUID());
    when(orderActionMock.getReceiver()).thenReturn(null);

    actions.add(orderActionMock);

    when(beerGame.getCurrentRound()).thenReturn(1);
    when(beerGame.getBusinesses()).thenReturn(businesses);

    when(dataStorage.hasCurrentGame()).thenReturn(true);
    when(dataStorage.getBeerGame()).thenReturn(beerGame);

    doCallRealMethod().when(dataStorage).saveCurrentActions(actions, 1);
    assertDoesNotThrow(() -> dataStorage.saveCurrentActions(actions, 1));
    verify(dataStorage, times(1)).saveBeerGameSettings(any(BeerGame.class));
  }

  @Test
  public void saveCurrentActions_should_logError_when_roundsAreDifferent()
      throws UnknownGameException {
    final GameDataStorage dataStorage = mock(GameDataStorage.class);
    final List<Action> actions = Collections.emptyList();

    when(beerGame.getCurrentRound()).thenReturn(6);

    when(dataStorage.hasCurrentGame()).thenReturn(true);
    when(dataStorage.getBeerGame()).thenReturn(beerGame);

    doCallRealMethod().when(dataStorage).saveCurrentActions(actions, 1);
    assertDoesNotThrow(() -> dataStorage.saveCurrentActions(actions, 1));
    verify(dataStorage, never()).saveBeerGameSettings(eq(beerGame));
  }

  @Test
  public void saveCurrentActions_should_throwException_when_noGameSet()
      throws UnknownGameException {
    final GameDataStorage dataStorage = mock(GameDataStorage.class);
    final BeerGame beerGame = new BeerGame(
        0,
        "name",
        mock(Date.class),
        Collections.emptyList(),
        Sets.newSet(),
        mock(Date.class),
        mock(GameState.class),
        mock(GameConfiguration.class)
    );

    doCallRealMethod().when(dataStorage).setCurrentGame(beerGame);
    doCallRealMethod().when(dataStorage).getBeerGame();
    doCallRealMethod().when(dataStorage).saveCurrentActions(Collections.emptyList(), 1);

    assertThrows(UnknownGameException.class,
        () -> dataStorage.saveCurrentActions(Collections.emptyList(), 1));
  }

  @Test
  public void saveCurrentActions_should_notCallGetBusiness() throws UnknownGameException {
    final GameDataStorage dataStorage = mock(GameDataStorage.class);

    doCallRealMethod().when(dataStorage).setCurrentGame(beerGame);
    when(dataStorage.getBeerGame()).thenReturn(beerGame);

    dataStorage.setCurrentGame(beerGame);

    dataStorage.saveCurrentActions(Collections.emptyList(), 2);

    verify(beerGame, never()).getBusinesses();

    assertDoesNotThrow(() -> dataStorage.saveCurrentActions(Collections.emptyList(), 0));
  }

  @Test
  public void saveCurrentActions_should_notCallActionsPerRound() throws UnknownGameException {
    final GameDataStorage dataStorage = mock(GameDataStorage.class);

    final OrderAction orderActionMock = mock(OrderAction.class);
    final List<Action> actions = new ArrayList<>();
    actions.add(orderActionMock);

    when(beerGame.getCurrentRound()).thenReturn(2);
    when(dataStorage.getBeerGame()).thenReturn(beerGame);

    final Business factory = mock(FactoryBusiness.class);
    final Business retail = mock(RetailBusiness.class);

    final UUID uuid = UUID.randomUUID();

    when(factory.getUUID()).thenReturn(UUID.randomUUID());
    when(retail.getUUID()).thenReturn(uuid);
    when(orderActionMock.getSender()).thenReturn(uuid);

    dataStorage.saveCurrentActions(actions, 1);

    verify(factory, never()).getActionsPerRound();

    assertDoesNotThrow(
        () -> dataStorage.saveCurrentActions(Collections.emptyList(), 0));
  }

  @Test
  public void savePlayerList_should_callSaveBeerGameSettings_whenPlayerListEqualsOrIsSmallerThanMaxPlayers()
      throws UnknownGameException {
    final GameDataStorage dataStorage = mock(GameDataStorage.class);
    final GameConfiguration gameConfiguration = mock(GameConfiguration.class);

    when(gameConfiguration.getMaxPlayers()).thenReturn(5);
    when(beerGame.getGameConfiguration()).thenReturn(gameConfiguration);

    when(dataStorage.hasCurrentGame()).thenReturn(true);
    when(dataStorage.getBeerGame()).thenReturn(beerGame);
    doCallRealMethod().when(dataStorage).savePlayerList(players);
    assertDoesNotThrow(() -> dataStorage.savePlayerList(players));
    verify(dataStorage, times(1)).saveBeerGameSettings(any(BeerGame.class));
  }
}
