package nl.ica.asd.storage.gamedatastorage;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import nl.ica.asd.logic.domain.actions.OrderAction;
import nl.ica.asd.logic.domain.builders.BeerGameBuilder;
import nl.ica.asd.logic.domain.businesses.RegionalWarehouseBusiness;
import nl.ica.asd.logic.statecalculator.BusinessState;
import nl.ica.asd.storage.domain.GameConfiguration;
import nl.ica.asd.storage.domain.GameData;
import nl.ica.asd.storage.exception.UnknownBusinessException;
import nl.ica.asd.storage.exception.UnknownException;
import nl.ica.asd.storage.exception.UnknownGameException;
import nl.ica.asd.util.GenericObjectMapper;
import nl.ica.asd.util.TestHelpers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class GameDataStorageTest {

  private static GameDataStorage gameDataStorage;
  private static GameData gameData;
  private static Set<Player> players = new HashSet<>();
  private static List<Business> businesses = new ArrayList<>();
  private static Agent agentHenk = new Agent("testingrule = 1");
  private static Agent agentSjaak = new Agent("testingrule = 2");

  private static BeerGame beerGame;

  @Mock
  private static Player playerHenk;

  @Mock
  private static Player playerSjaak;

  @Mock
  private Business businessFactory;

  @Mock
  private Business businessWholesale;

  @Mock
  private static Business factory;

  @Mock
  private static Business retail;

  @Mock
  private static OrderAction orderAction;

  private static GameData initialGameData;
  private static GenericObjectMapper objectMapper;

  @BeforeAll
  public static void init() {
    final Map<Class<? extends Business>, BusinessState> businessStates = new HashMap<>();
    final GameConfiguration gameConfiguration = new GameConfiguration(
        4,
        5,
        SupplyChainType.LINEAR,
        0,
        GameVisibilityType.VISIBLE,
        businessStates
    );

    beerGame = new BeerGame(1, "BeerTestingGame",
        new GregorianCalendar(2018, Calendar.MARCH, 12).getTime(), businesses, players, new Date(),
        GameState.SETUP, gameConfiguration);

    initialGameData = new GameData("BeerGame", null, gameConfiguration);
    objectMapper = GenericObjectMapper.getInstance();
    objectMapper = TestHelpers
        .singletonMock(mock(GenericObjectMapper.class), "genericObjectMapper");
  }

  @BeforeEach
  public void setup() throws UnknownGameException {
    gameDataStorage = new GameDataStorage();

    MockitoAnnotations.initMocks(this);

    final Optional<UUID> factoryUuid = Optional
        .of(UUID.fromString("253f955f-b0a0-4a0f-9e6c-609b03e8a7df"));
    final Optional<UUID> wholesaleUuid = Optional
        .of(UUID.fromString("253f955a-b0a0-4a0f-9e6c-609b03e8a7df"));

    players.add(playerHenk);
    players.add(playerSjaak);

    when(playerHenk.getUsername()).thenReturn("Henk");
    when(playerHenk.getBusinessUUIDOptional())
        .thenReturn(factoryUuid);

    when(playerSjaak.getUsername()).thenReturn("Sjaak");
    when(playerSjaak.getBusinessUUIDOptional())
        .thenReturn(wholesaleUuid);

    businesses.add(businessFactory);
    businesses.add(businessWholesale);

    when(businessFactory.getPlayer()).thenReturn(playerHenk);
    when(businessFactory.getUUID())
        .thenReturn(factoryUuid.get());
    when(businessFactory.getAgent())
        .thenReturn(agentHenk);
    when(businessFactory.getBusinessType()).thenReturn(BusinessType.FACTORY);

    when(businessWholesale.getPlayer()).thenReturn(playerSjaak);
    when(businessWholesale.getUUID())
        .thenReturn(wholesaleUuid.get());
    when(businessWholesale.getAgent())
        .thenReturn(agentSjaak);

    beerGame = BeerGameBuilder.fromBaseBeerGame(beerGame).withPlayers(players)
        .withBusinesses(businesses).build();

    gameDataStorage.setCurrentGame(beerGame);
  }

  @AfterEach
  public void tearDown() {
    reset(playerHenk);
    reset(playerSjaak);
    reset(businessFactory);
    reset(businessWholesale);
    reset(factory);
    reset(retail);
    reset(orderAction);
    businesses.clear();
  }

  @AfterAll
  public static void destroy() {
    TestHelpers.resetSingletonMock(objectMapper, "genericObjectMapper");
  }

  @Test
  public void hasCurrentGame_should_returnFalse_when_noGameSet() throws UnknownGameException {

    final GameDataStorage mockGameDataStorage = mock(GameDataStorage.class);
    mockGameDataStorage.setCurrentGame(beerGame);

    assertFalse(mockGameDataStorage.hasCurrentGame());
  }

  @Test
  public void hasCurrentGame_should_returnFalse_when_wrongGameDataSet()
      throws UnknownGameException {

    final GameDataStorage mockGameDataStorage = mock(GameDataStorage.class);
    mockGameDataStorage.setCurrentGame(null);

    assertFalse(mockGameDataStorage.hasCurrentGame());
  }

  @Test
  public void setCurrentGame_should_throwException_when_fileDoesNotExists() {
    final GameDataStorage mockGameDataStorage = new GameDataStorage();
    final Map<Class<? extends Business>, BusinessState> businessStates = new HashMap<>();
    final GameConfiguration gameConfiguration = new GameConfiguration(
        4,
        5,
        SupplyChainType.LINEAR,
        0,
        GameVisibilityType.VISIBLE,
        businessStates
    );

    final BeerGame failBeerGame = new BeerGame(6, "DitBestaatNiet",
        new GregorianCalendar(2014, Calendar.MARCH, 10).getTime(), businesses, players, new Date(),
        GameState.SETUP, gameConfiguration);

    Assertions.assertThrows(UnknownGameException.class,
        () -> mockGameDataStorage.setCurrentGame(failBeerGame));
  }

  @Test
  public void getBeerGame_should_throwException_when_noGameSet() throws UnknownGameException {
    final GameDataStorage mockGameDataStorage = mock(GameDataStorage.class);

    doCallRealMethod().when(mockGameDataStorage).getBeerGame();
    mockGameDataStorage.setCurrentGame(null);

    assertThrows(UnknownGameException.class, mockGameDataStorage::getBeerGame);
  }

  @Test
  public void savePlayerlist_should_notThrowException() throws UnknownGameException {
    final GameDataStorage mockGameDataStorage = mock(GameDataStorage.class);
    mockGameDataStorage.setCurrentGame(beerGame);
    assertDoesNotThrow(() -> mockGameDataStorage.savePlayerList(Collections.emptySet()));
  }

  @Test
  public void saveCurrentOrderActions_should_throwException_when_noGameSet()
      throws UnknownGameException {

    final GameDataStorage mockGameDataStorage = mock(GameDataStorage.class);
    doCallRealMethod().when(mockGameDataStorage)
        .saveCurrentActions(Collections.emptyList(), 0);
    mockGameDataStorage.setCurrentGame(beerGame);

    assertThrows(Exception.class,
        () -> mockGameDataStorage.saveCurrentActions(Collections.emptyList(), 0));
  }

  @Test
  public void saveCurrentOrderActions_should_notCallGetBusiness() throws UnknownGameException {
    final BeerGame game = mock(BeerGame.class);
    final GameDataStorage mockGameDataStorage = mock(GameDataStorage.class);

    when(mockGameDataStorage.getBeerGame()).thenReturn(game);

    mockGameDataStorage.saveCurrentActions(Collections.emptyList(), 2);

    verify(game, never()).getBusinesses();

    assertDoesNotThrow(
        () -> mockGameDataStorage.saveCurrentActions(Collections.emptyList(), 0));
  }

  @Test
  public void saveCurrentOrderActions_should_notCallActionsPerRound() throws UnknownGameException {
    final UUID uuidFactory = new UUID(1, 1);
    final UUID uuidRetail = new UUID(1, 3);
    final GameDataStorage mockGameDataStorage = mock(GameDataStorage.class);

    List<Action> actions = new ArrayList<>();
    actions.add(orderAction);

    final BeerGame beerGame1 = mock(BeerGame.class);

    when(beerGame1.getCurrentRound()).thenReturn(2);
    when(mockGameDataStorage.getBeerGame()).thenReturn(beerGame1);

    when(factory.getUUID()).thenReturn(uuidFactory);
    when(retail.getUUID()).thenReturn(uuidRetail);
    when(orderAction.getSender()).thenReturn(uuidRetail);

    mockGameDataStorage.saveCurrentActions(actions, 1);

    verify(factory, never()).getActionsPerRound();

    assertDoesNotThrow(
        () -> mockGameDataStorage.saveCurrentActions(Collections.emptyList(), 0));
  }

  @Test
  public void getActionOrderPerBusiness_should_throwException_when_noGameSet()
      throws UnknownGameException {

    final UUID uuid = UUID.randomUUID();
    BeerGame beergame1 = BeerGameBuilder.fromBaseBeerGame(beerGame).withBusinesses(null).build();
    gameDataStorage.setCurrentGame(beergame1);

    assertThrows(UnknownBusinessException.class, () -> gameDataStorage.getAllActionsPerBusiness(
        uuid));
  }

  @Test
  public void getAllActionsPerBusiness_should_returnBusinesses()
      throws UnknownBusinessException, UnknownGameException {
    Map<Integer, List<Action>> actionMap = new HashMap<>();
    actionMap.put(0, new ArrayList<>());
    actionMap.get(0).add(orderAction);
    when(businessFactory.getActionsPerRound()).thenReturn(actionMap);

    final Map<Integer, List<Action>> actions = gameDataStorage
        .getAllActionsPerBusiness(businessFactory.getUUID());

    assertEquals(1, actions.size());
    assertTrue(actions.get(0).contains(orderAction));
  }

  @Test
  public void getPlayerList_should_throwException_when_noGameSet() throws UnknownGameException {

    final GameDataStorage mockGameDataStorage = mock(GameDataStorage.class);
    doCallRealMethod().when(mockGameDataStorage).getPlayerList();
    when(mockGameDataStorage.getBeerGame()).thenThrow(UnknownGameException.class);

    assertThrows(UnknownGameException.class, mockGameDataStorage::getPlayerList);
  }

  @Test
  public void getPlayerList_should_notThrowException() {
    assertDoesNotThrow(() -> gameDataStorage.getPlayerList());
  }

  @Test
  public void when_getBusinessRules_shouldThrow() throws UnknownGameException {
    final Player player = new Player("TestedPlayer", UUID.randomUUID(), BusinessType.FACTORY,
        PlayerStatus.CONNECTED_AND_ACTIVE,
        "145.74.180.0", 1);
    final GameDataStorage mockGameDataStorage = mock(GameDataStorage.class);

    doCallRealMethod().when(mockGameDataStorage).getBusinessRules(businessFactory);

    mockGameDataStorage.setCurrentGame(beerGame);
    when(mockGameDataStorage.getBeerGame()).thenThrow(UnknownGameException.class);

    assertThrows(UnknownGameException.class,
        () -> mockGameDataStorage.getBusinessRules(businessFactory));
  }

  @Test
  public void when_getBusinessRules_shouldNotThrow() {

    assertDoesNotThrow(() -> gameDataStorage.getBusinessRules(null));
  }

  @Test
  void getDefaultGameData_when_getName_should_NameEqualBeerGame() throws IOException {
    when(objectMapper.readValue(any(InputStream.class), eq(GameData.class)))
        .thenReturn(initialGameData);
    gameData = gameDataStorage.getDefaultGameData();
    assertEquals("BeerGame", gameData.getName());
  }

  @Test
  void getDefaultGameData_when_getDate_should_DateEqualNull() throws IOException {
    when(objectMapper.readValue(any(InputStream.class), eq(GameData.class)))
        .thenReturn(initialGameData);
    gameData = gameDataStorage.getDefaultGameData();
    assertNull(gameData.getDate());
  }

  @Test
  void getDefaultGameData_when_getSettings_should_SettingsNotEqualNull() throws IOException {
    when(objectMapper.readValue(any(InputStream.class), eq(GameData.class)))
        .thenReturn(initialGameData);
    gameData = gameDataStorage.getDefaultGameData();
    Assertions.assertNotNull(gameData.getSettings());
  }

  @Test
  void getDefaultGameData_when_getSettingsGetMaxPlayers_should_MaxPlayersNotEqualNull()
      throws IOException {
    when(objectMapper.readValue(any(InputStream.class), eq(GameData.class)))
        .thenReturn(initialGameData);
    gameData = gameDataStorage.getDefaultGameData();
    assertTrue(gameData.getSettings().getMaxPlayers() > 0);
  }

  @Test
  void getDefaultGameData_when_getSettingsGetMaxTurns_should_MaxTurnsNotEqualNull()
      throws IOException {
    when(objectMapper.readValue(any(InputStream.class), eq(GameData.class)))
        .thenReturn(initialGameData);
    gameData = gameDataStorage.getDefaultGameData();
    assertTrue(gameData.getSettings().getMaxRounds() > 0);
  }

  @Test
  void getDefaultGameData_when_getSettingsGetTimeLimit_should_TimeLimitEqualNull()
      throws IOException {
    when(objectMapper.readValue(any(InputStream.class), eq(GameData.class)))
        .thenReturn(initialGameData);
    gameData = gameDataStorage.getDefaultGameData();
    assertEquals(gameData.getSettings().getTimeLimitPerRound(), 0);
  }

  @Test
  void getDefaultGameData_when_getSettingsGetGameType_should_GameTypeEqualLinear()
      throws IOException {
    when(objectMapper.readValue(any(InputStream.class), eq(GameData.class)))
        .thenReturn(initialGameData);
    gameData = gameDataStorage.getDefaultGameData();
    assertEquals(gameData.getSettings().getSupplyChainType(), SupplyChainType.LINEAR);
  }

  @Test
  void getDefaultGameData_when_getSettingsGetVisibility_should_VisibilityEqualVisible()
      throws IOException {
    when(objectMapper.readValue(any(InputStream.class), eq(GameData.class)))
        .thenReturn(initialGameData);
    gameData = gameDataStorage.getDefaultGameData();
    assertEquals(gameData.getSettings().getGameVisibilityType(), GameVisibilityType.VISIBLE);
  }

  @Test
  void saveBeerGameSettingsThrowUnknownGameExceptionWhenIsNull() {
    assertThrows(UnknownGameException.class, () -> gameDataStorage.saveBeerGameSettings(null));
  }

  @Test
  void saveBeerGameSettings_should_callMapperOnce_when_beerGameIsFilled()
      throws IOException, UnknownGameException {
    gameDataStorage.setCurrentGame(beerGame);
    gameDataStorage.saveBeerGameSettings(beerGame);
    verify(objectMapper, atLeastOnce()).writeValue(any(File.class), eq(beerGame));
  }

  @Test
  void setCurrentGame_should_throwUnknownGameException_when_beerGameIsNotAFile() {
    assertThrows(UnknownGameException.class, () -> gameDataStorage.setCurrentGame(
        BeerGameBuilder.aBeerGame()
            .withName("Fake beergame")
            .withDateTime(new Date(1547045490000L))
            .build()));
  }

  @Test
  void whenBeerGameIsNull_getPlayerList_should_returnEmptyList() throws UnknownGameException {
    final GameDataStorage mockStorage = mock(GameDataStorage.class);
    when(mockStorage.getBeerGame()).thenReturn(null);
    when(mockStorage.getPlayerList()).thenCallRealMethod();
    final Set<Player> playerList = mockStorage.getPlayerList();

    verify(mockStorage, times(1)).getBeerGame();
    assertTrue(playerList.isEmpty());
  }

  @Test
  void whenBeerGameIsFilled_getPlayerList_should_returnBeerGameList() throws UnknownGameException {
    final GameDataStorage mockStorage = mock(GameDataStorage.class);
    when(mockStorage.getBeerGame()).thenReturn(beerGame);
    when(mockStorage.getPlayerList()).thenCallRealMethod();
    final Set<Player> playerList = mockStorage.getPlayerList();

    verify(mockStorage, times(1)).getBeerGame();
    assertFalse(playerList.isEmpty());
    assertEquals(playerList.iterator().next(), players.iterator().next());
  }

  @Test
  void getBusinessRules_should_returnNull_when_playerIsNull() throws UnknownGameException {
    assertNull(gameDataStorage.getBusinessRules(null));
  }

  @Test
  void getBusinessRules_should_returnNull_when_agentIsNotPresent()
      throws IOException, UnknownGameException {
    final GameDataStorage mockGameDataStorage = mock(GameDataStorage.class);
    final Agent agentMock = mock(Agent.class);

    doCallRealMethod().when(mockGameDataStorage).getBusinessRules(businessWholesale);
    when(mockGameDataStorage.getBeerGame()).thenReturn(beerGame);
    when(businessWholesale.getAgent()).thenReturn(agentMock);
    when(agentMock.getBusinessRules()).thenReturn(null);

    assertNull(mockGameDataStorage.getBusinessRules(businessWholesale));
  }

  @Test
  void getBusinessRules_should_returnNull_when_businessIsNotPresent()
      throws IOException, UnknownGameException {
    final GameDataStorage mockGameDataStorage = mock(GameDataStorage.class);
    final UUID uuid = UUID.randomUUID();
    final UUID mockHouseUUID = UUID.randomUUID();
    final RegionalWarehouseBusiness house = new RegionalWarehouseBusiness(100, 100,
        new HashMap<Integer, List<Action>>(), null,
        uuid, null, null);
    final RegionalWarehouseBusiness mockHouse = mock(RegionalWarehouseBusiness.class);
    beerGame.getBusinesses().add(house);

    doCallRealMethod().when(mockGameDataStorage).getBusinessRules(mockHouse);
    when(mockGameDataStorage.getBeerGame()).thenReturn(beerGame);
    when(mockHouse.getUUID()).thenReturn(mockHouseUUID);

    assertNull(mockGameDataStorage.getBusinessRules(mockHouse));
  }

  @Test
  void getBusinessRules_should_returnBusinessRules_when_agentIsFound()
      throws IOException, UnknownGameException {
    final GameDataStorage mockGameDataStorage = mock(GameDataStorage.class);
    final UUID uuid = UUID.randomUUID();
    final RegionalWarehouseBusiness house = new RegionalWarehouseBusiness(100, 100,
        new HashMap<Integer, List<Action>>(), agentSjaak,
        uuid, null, null);
    beerGame.getBusinesses().add(house);

    doCallRealMethod().when(mockGameDataStorage).getBusinessRules(house);
    when(mockGameDataStorage.getBeerGame()).thenReturn(beerGame);

    assertEquals(mockGameDataStorage.getBusinessRules(house),
        agentSjaak.getBusinessRules());
  }

  @Test
  void getAvailableGames_shouldReturnEmptyListOfBeerGames_when_noBeerGamesAreFinished()
      throws IOException {
    when(objectMapper.readValue(any(InputStream.class), eq(BeerGame.class)))
        .thenReturn(beerGame);
    assertEquals(0, gameDataStorage.getAvailableGames().size());
  }

  @Test
  void getAvailableGames_shouldReturnListOfBeerGames_when_aBeerGamesIsFinished()
      throws IOException {
    final BeerGame finishedBeerGame = BeerGameBuilder.fromBaseBeerGame(beerGame)
        .withGameState(GameState.FINISHED).build();
    when(objectMapper.readValue(any(File.class), eq(BeerGame.class)))
        .thenReturn(finishedBeerGame);
    final Set<BeerGame> beerGames = gameDataStorage.getAvailableGames();
    assertEquals(1, beerGames.size());
    assertTrue(beerGames.contains(finishedBeerGame));
  }

  @Test
  void getBusinesses_should_returnBusinessesFromCurrentBeerGame() throws UnknownGameException {
    final GameDataStorage dataStorage = mock(GameDataStorage.class);

    final BeerGame beerGame = mock(BeerGame.class);
    when(beerGame.getBusinesses()).thenReturn(businesses);
    when(dataStorage.hasCurrentGame()).thenReturn(true);
    when(dataStorage.getBeerGame()).thenReturn(beerGame);

    doCallRealMethod().when(dataStorage).getBusinesses();
    assertEquals(businesses, dataStorage.getBusinesses());
  }

  @Test
  void getBusinesses_should_returnCurrentRoundFromCurrentBeerGame() throws UnknownGameException {
    final GameDataStorage dataStorage = mock(GameDataStorage.class);
    final BeerGame beerGame = mock(BeerGame.class);
    when(beerGame.getCurrentRound()).thenReturn(1);
    when(dataStorage.hasCurrentGame()).thenReturn(true);
    when(dataStorage.getBeerGame()).thenReturn(beerGame);

    doCallRealMethod().when(dataStorage).getCurrentRound();
    assertEquals(1, dataStorage.getCurrentRound());
  }

  @Test
  void getBusinessType_should_returnFactoryBusinessTypeForFactory() throws UnknownException {
    assertEquals(BusinessType.FACTORY, gameDataStorage.getBusinessType(businessFactory.getUUID()));
  }

  @Test
  void getBusinessType_should_throwUnknownBusinessException_when_uuidIsNull() {
    assertThrows(UnknownBusinessException.class, () -> gameDataStorage.getBusinessType(null));
  }

  @Test
  void getAmountOfBusinessesAvailable_should_returnOne_whenGameIsLiniar()
      throws UnknownGameException {
    assertEquals(1, gameDataStorage.getAmountOfBusinessesAvailable(BusinessType.FACTORY));
  }

  @Test
  void getAmountOfBusinessesAvailable_should_returnFourForWholeSale_whenGameIsPyramid()
      throws UnknownGameException {
    final GameConfiguration gameConfiguration = new GameConfiguration(15, 5,
        SupplyChainType.PYRAMID, 0, GameVisibilityType.VISIBLE, null);
    beerGame = BeerGameBuilder.fromBaseBeerGame(beerGame).withGameConfiguration(gameConfiguration)
        .build();
    gameDataStorage.setCurrentGame(beerGame);
    assertEquals(4, gameDataStorage.getAmountOfBusinessesAvailable(BusinessType.WHOLESALE));
  }

  @Test
  void getAmountOfBusinessesAvailable_should_returnMaxPlayersMinusThree_whenGameIsNetwork()
      throws UnknownGameException {
    GameConfiguration gameConfiguration = new GameConfiguration(10, 5,
        SupplyChainType.NETWORK, 0, GameVisibilityType.VISIBLE, null);
    beerGame = BeerGameBuilder.fromBaseBeerGame(beerGame).withGameConfiguration(gameConfiguration)
        .build();
    gameDataStorage.setCurrentGame(beerGame);
    assertEquals(7, gameDataStorage.getAmountOfBusinessesAvailable(BusinessType.WHOLESALE));

    gameConfiguration = new GameConfiguration(3, 5,
        SupplyChainType.NETWORK, 0, GameVisibilityType.VISIBLE, null);
    beerGame = BeerGameBuilder.fromBaseBeerGame(beerGame).withGameConfiguration(gameConfiguration)
        .build();
    gameDataStorage.setCurrentGame(beerGame);
    assertEquals(1, gameDataStorage.getAmountOfBusinessesAvailable(BusinessType.WHOLESALE));
  }
}
