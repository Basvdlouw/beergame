package nl.ica.asd.logic.postgameprocessor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import nl.ica.asd.logic.domain.BeerGame;
import nl.ica.asd.logic.domain.Business;
import nl.ica.asd.logic.domain.GameState;
import nl.ica.asd.logic.domain.businesses.FactoryBusiness;
import nl.ica.asd.logic.domain.businesses.RegionalWarehouseBusiness;
import nl.ica.asd.logic.domain.businesses.RetailBusiness;
import nl.ica.asd.logic.domain.businesses.WholesaleBusiness;
import nl.ica.asd.logic.postgameprocessor.dto.BusinessRoundState;
import nl.ica.asd.logic.postgameprocessor.dto.PlayedGame;
import nl.ica.asd.logic.statecalculator.BusinessState;
import nl.ica.asd.logic.statecalculator.StateCalculator;
import nl.ica.asd.storage.exception.UnknownGameException;
import nl.ica.asd.storage.gamedatastorage.GameDataAccess;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultPostGameProcessorTest {

  @Mock
  private StateCalculator stateCalculator;

  @InjectMocks
  private DefaultPostGameProcessor postGameProcessor;


  @Mock
  private GameDataAccess mockedGameDataAccess;

  private List<Business> businessSet = new ArrayList<>();
  private UUID u1 = UUID.randomUUID();
  private UUID u2 = UUID.randomUUID();
  private UUID u3 = UUID.randomUUID();
  private UUID u4 = UUID.randomUUID();

  @BeforeEach
  void setUp() throws UnknownGameException {

    businessSet.addAll(Arrays.asList(new FactoryBusiness(0, 0, null, null, u1, null, UUID.randomUUID()),
        new RegionalWarehouseBusiness(0, 0, null, null, u2, null, null),
        new RetailBusiness(0, 0, null, null, u3, null, null, UUID.randomUUID())));

  }

  @AfterEach
  void tearDown() {
  }

  @Test
  void getPlayedBeerGames_should_returnPlayedBeerGames() {

    Set<BeerGame> availableGames = new HashSet<>();
    Set<BeerGame> finishedGames = new HashSet<>();
    Set<BeerGame> unfinishedGames = new HashSet<>();

    finishedGames
        .addAll(Arrays.asList(new BeerGame(0, "game 1", new Date(), null, null, new Date(0),
                GameState.FINISHED, null),
            new BeerGame(0, "game 2", new Date(), null, null, new Date(0),
                GameState.FINISHED, null),
            new BeerGame(0, "game 3", new Date(), null, null, new Date(0),
                GameState.FINISHED, null),
            new BeerGame(0, "game 4", new Date(), null, null, new Date(0),
                GameState.FINISHED, null)));

    unfinishedGames
        .addAll(Arrays.asList(new BeerGame(0, "game 5", new Date(), null, null, new Date(0),
                GameState.STARTED, null),
            new BeerGame(0, "game 6", new Date(), null, null, new Date(0),
                GameState.SETUP, null),
            new BeerGame(0, "game 7", new Date(), null, null, new Date(0),
                GameState.SETUP, null),
            new BeerGame(0, "game 8", new Date(), null, null, new Date(0),
                GameState.SETUP, null)));

    availableGames.addAll(finishedGames);
    availableGames.addAll(unfinishedGames);

    when(mockedGameDataAccess.getAvailableGames()).thenReturn(availableGames);

    Set<PlayedGame> playedGames = postGameProcessor.getPlayedBeerGames();

    assertEquals(playedGames.size(),
        finishedGames.size());

    playedGames.forEach(game -> {
      Optional<BeerGame> playedGame = availableGames.stream()
          .filter(availableGame -> availableGame.getName().equals(game.getName())).findFirst();

      if (playedGame.isPresent()) {
        assertEquals(playedGame.get().getName(), game.getName());
        assertEquals(playedGame.get().getDateTime(), game.getDate());
      } else {
        fail();
      }
    });
  }

  @Test
  void getPlayedBeerGames_should_returnPlayedBeerGames_when_NoGamesAreAvailable() {

    when(mockedGameDataAccess.getAvailableGames()).thenReturn(new HashSet<>());
    Set<PlayedGame> playedGames = postGameProcessor.getPlayedBeerGames();
    assertEquals(playedGames.size(), 0);
  }

  @Test
  void getPlayedBeerGame_should_returnPlayedBeerGame() throws PostGameProcessorException {

    Set<BeerGame> availableGames = new HashSet<>();
    Set<BeerGame> finishedGames = new HashSet<>();

    finishedGames
        .addAll(Arrays.asList(new BeerGame(0, "game 1", new Date(), null, null, new Date(0),
                GameState.FINISHED, null),
            new BeerGame(0, "game 2", new Date(), null, null, new Date(0),
                GameState.FINISHED, null),
            new BeerGame(0, "game 3", new Date(), null, null, new Date(0),
                GameState.FINISHED, null),
            new BeerGame(0, "game 4", new Date(), null, null, new Date(0),
                GameState.FINISHED, null)));

    availableGames.addAll(finishedGames);

    when(mockedGameDataAccess.getAvailableGames()).thenReturn(availableGames);

    BeerGame firstBeerGame = finishedGames.stream().findFirst().get();
    PlayedGame playedGame = new PlayedGame(firstBeerGame.getName(), firstBeerGame.getDateTime());
    BeerGame selectedBeerGame = postGameProcessor.getPlayedBeerGame(playedGame);
    assertEquals(selectedBeerGame, firstBeerGame);
  }

  @Test
  void getPlayedBeerGame_should_throwPostGameProcessorException_when_BeerGameCannotBeFound() {
    PlayedGame playedGame = mock(PlayedGame.class);
    assertThrows(PostGameProcessorException.class,
        () -> postGameProcessor.getPlayedBeerGame(playedGame));
  }

  @Test
  void getBeerGameTurn_should_returnBeerGameTurn() throws Exception {
    UUID u1 = UUID.randomUUID();
    UUID u2 = UUID.randomUUID();
    UUID u3 = UUID.randomUUID();
    UUID u4 = UUID.randomUUID();

    Map<UUID, BusinessState> businessStates = new HashMap<>();
    businessStates.put(u1, new BusinessState(10, 0, 10, 10, 10, 10, 3, 0, 0));
    businessStates.put(u2, new BusinessState(20, 0, 20, 20, 20, 20, 3, 0, 0));
    businessStates.put(u3, new BusinessState(30, 0, 30, 30, 30, 30, 3, 0, 0));
    businessStates.put(u4, new BusinessState(0, 10, 10, 40, 20, 0, 3, 0, 0));

    when(stateCalculator.getBusinessStatesForRound(1)).thenReturn(businessStates);

    List<BusinessRoundState> actual = postGameProcessor.getBeerGameTurn(1);

    List<BusinessRoundState> expected = new ArrayList<>();
    expected.add(new BusinessRoundState(u1, 10, 3, 10, 10, 10, 0, 10));
    expected.add(new BusinessRoundState(u2, 20, 3, 20, 20, 20, 0, 20));
    expected.add(new BusinessRoundState(u3, 30, 3, 30, 30, 30, 0, 30));
    expected.add(new BusinessRoundState(u4, 20, 3, 40, 10, 0, 10, 0));

    assertBusinessStates(expected, actual);
  }

  @Test
  void getBeergGameTurn_should_throwPostGameProcessingException_when_anUnkownGameExceptionThrown()
      throws Exception {
    when(stateCalculator.getBusinessStatesForRound(anyInt()))
        .thenThrow(new UnknownGameException("TEST"));

    assertThrows(PostGameProcessorException.class, () -> postGameProcessor.getBeerGameTurn(10));
  }

  @Test
  void getBeerGameBusinesses_should_returnFactoryBusinesses() throws Exception {
    testGetBeerGameBusinesses(mockedGameDataAccess, postGameProcessor, BusinessLevelType.FACTORY,
        0);
  }

  @Test
  void getBeerGameBusinesses_should_returnRegionalWarehouseBusinesses() throws Exception {
    testGetBeerGameBusinesses(mockedGameDataAccess, postGameProcessor,
        BusinessLevelType.REGIONALWAREHOUSE, 3);
  }

  @Test
  void getBeerGameBusinesses_should_returnWholesaleBusinesses() throws Exception {
    testGetBeerGameBusinesses(mockedGameDataAccess, postGameProcessor, BusinessLevelType.WHOLESALE,
        2);
  }

  @Test
  void getBeerGameBusinesses_should_returnRetailBusinesses() throws Exception {
    testGetBeerGameBusinesses(mockedGameDataAccess, postGameProcessor, BusinessLevelType.RETAIL, 1);
  }

  @Test
  void getBeerGameBusinesses_should_returnEmptyList_when_thereAreNoBusinessesOfProvidedLevelType()
      throws Exception {
    BeerGame beerGame = getDummyBeerGame();

    when(mockedGameDataAccess.getBeerGame()).thenReturn(beerGame);

    List<Business> businesses = postGameProcessor.getBeerGameBusinesses(BusinessLevelType.FACTORY);

    assertEquals(0, businesses.size());
  }

  @Test
  void getBeerGameBusinesses_should_throwPostGameProcessorException_when_thereIsNoDefaultBeerGame()
      throws Exception {
    when(mockedGameDataAccess.getBeerGame()).thenThrow(new UnknownGameException("TEST"));

    assertThrows(PostGameProcessorException.class,
        () -> postGameProcessor.getBeerGameBusinesses(BusinessLevelType.FACTORY));
  }

  @Test
  void getBeerGameBusiness_should_returnBeerGameBusiness()
      throws Exception {
    when(mockedGameDataAccess.getBusinesses()).thenReturn(businessSet);
    Business mockBusiness = new FactoryBusiness(0, 0, null, null, u1, null, UUID.randomUUID());
    Business testBusiness = postGameProcessor.getBeerGameBusiness(u1);
    assertEquals(testBusiness.getUUID(), mockBusiness.getUUID());
  }

  @Test
  void getBeerGameBusiness_should_throwPostGameProcessorException_when_businessesCannotBeFound()
      throws Exception {
    doThrow(new UnknownGameException("TEST")).when(mockedGameDataAccess).getBusinesses();
    assertThrows(PostGameProcessorException.class, () -> postGameProcessor.getBeerGameBusiness(u1));
  }

  @Test
  void getBeerGameBusiness_should_throuwPostGameProcessorException_when_businessUUIDCannotBeFound()
      throws Exception {
    when(mockedGameDataAccess.getBusinesses()).thenReturn(businessSet);
    assertThrows(PostGameProcessorException.class, () -> postGameProcessor.getBeerGameBusiness(u4));
  }

  @Test
  void saveCompletedBeergame_should_saveCompletedBeerGameAndSetAsDefault() throws Exception {
    BeerGame beerGame = getDummyBeerGame();

    postGameProcessor.saveCompletedBeerGame(beerGame);

    verify(mockedGameDataAccess, atLeastOnce()).setCurrentGame(
        ArgumentMatchers.argThat(actual -> actual.getGameState() == GameState.FINISHED));
    verify(mockedGameDataAccess, atLeastOnce()).saveBeerGameSettings(
        ArgumentMatchers.argThat(actual -> actual.getGameState() == GameState.FINISHED));
  }

  @Test
  void saveCompletedBeergame_should_throwPostGameProcessorException_when_beerGameIsNull() {
    assertThrows(IllegalArgumentException.class,
        () -> postGameProcessor.saveCompletedBeerGame(null));
  }

  @Test
  void saveCompletedBeergame_should_throwPostGameProcessorException_when_setAsDefaultThrowsException()
      throws Exception {
    BeerGame beerGame = getDummyBeerGame();

    doThrow(new UnknownGameException("TEST")).when(mockedGameDataAccess).setCurrentGame(any());

    assertThrows(PostGameProcessorException.class,
        () -> postGameProcessor.saveCompletedBeerGame(beerGame));
  }

  @Test
  void saveCompletedBeergame_should_throwPostGameProcessorException_when_saveBeerGameSettingsThrowsException()
      throws Exception {
    BeerGame beerGame = getDummyBeerGame();

    doThrow(new UnknownGameException("TEST")).when(mockedGameDataAccess)
        .saveBeerGameSettings(any());

    assertThrows(PostGameProcessorException.class,
        () -> postGameProcessor.saveCompletedBeerGame(beerGame));
  }

  private static void testGetBeerGameBusinesses(GameDataAccess gameDataAccess,
      PostGameProcessor postGameProcessor, BusinessLevelType businessLevelType, int businessIndex)
      throws Exception {
    BeerGame beerGame = getDummyBeerGameWithBusinesses();

    when(gameDataAccess.getBeerGame()).thenReturn(beerGame);

    List<Business> businesses = postGameProcessor.getBeerGameBusinesses(businessLevelType);

    assertEquals(1, businesses.size());
    assertSame(beerGame.getBusinesses().get(businessIndex), businesses.get(0));
  }

  private static BeerGame getDummyBeerGameWithBusinesses() {
    BeerGame beerGame = getDummyBeerGame();

    beerGame.getBusinesses().addAll(Arrays
        .asList(new FactoryBusiness(0, 0, null, null, UUID.randomUUID(), null, UUID.randomUUID()),
            new RetailBusiness(0, 0, null, null, UUID.randomUUID(), null, null, UUID.randomUUID()),
            new WholesaleBusiness(0, 0, null, null, UUID.randomUUID(), null, null),
            new RegionalWarehouseBusiness(0, 0, null, null, UUID.randomUUID(), null, null)));

    return beerGame;
  }

  private static BeerGame getDummyBeerGame() {
    return new BeerGame(0, "", new Date(), new ArrayList<>(), null, new Date(),
        GameState.STARTED, null);
  }

  private static void assertBusinessStates(List<BusinessRoundState> expecteds,
      List<BusinessRoundState> actuals) {
    expecteds.forEach(expected -> {
      BusinessRoundState actual = actuals.stream()
          .filter(x -> x.getBusinessUuid() == expected.getBusinessUuid()).findAny().get();

      assertBusinessState(expected, actual);
    });
  }

  private static void assertBusinessState(BusinessRoundState expected, BusinessRoundState actual) {
    assertEquals(expected.getBusinessUuid(), actual.getBusinessUuid());
    assertEquals(expected.getIncomingGoods(), actual.getIncomingGoods());
    assertEquals(expected.getOutgoingGoods(), actual.getOutgoingGoods());
    assertEquals(expected.getIncomingOrders(), actual.getIncomingOrders());
    assertEquals(expected.getOutgoingOrders(), actual.getOutgoingOrders());
    assertEquals(expected.getStock(), actual.getStock());
    assertEquals(expected.getOpenOrders(), actual.getOpenOrders());
    assertEquals(expected.getBudget(), actual.getBudget());
  }
}