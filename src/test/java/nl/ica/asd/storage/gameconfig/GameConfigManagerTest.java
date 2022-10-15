package nl.ica.asd.storage.gameconfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import nl.ica.asd.logic.domain.BeerGame;
import nl.ica.asd.logic.domain.Business;
import nl.ica.asd.logic.domain.GameState;
import nl.ica.asd.logic.domain.GameVisibilityType;
import nl.ica.asd.logic.domain.SupplyChainType;
import nl.ica.asd.logic.domain.builders.FactoryBusinessBuilder;
import nl.ica.asd.logic.domain.builders.RegionalWarehouseBusinessBuilder;
import nl.ica.asd.logic.domain.builders.RetailBusinessBuilder;
import nl.ica.asd.logic.domain.builders.WholesaleBusinessBuilder;
import nl.ica.asd.network.playerlobby.PlayerLobby;
import nl.ica.asd.storage.domain.GameConfiguration;
import nl.ica.asd.storage.domain.GameData;
import nl.ica.asd.storage.domain.GamePrices;
import nl.ica.asd.storage.exception.UnknownGameDataException;
import nl.ica.asd.storage.exception.UnknownGameException;
import nl.ica.asd.storage.gamedatastorage.GameDataAccess;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GameConfigManagerTest {

  private GameConfigManager gameConfigManager;
  @Mock
  private GameDataAccess gameDataAccess;

  private static BeerGame beerGame;
  private static GameData gameData;

  @Mock
  private PlayerLobby playerLobby;

  @BeforeAll
  public static void setup() {
    GameConfiguration gameConfiguration = new GameConfiguration(
        10, 20, SupplyChainType.LINEAR, 0, GameVisibilityType.INVISIBLE, null
    );
    gameData = new GameData("Test game", new Date(), gameConfiguration);

    beerGame = new BeerGame(1, "FEM Game", new Date(), new ArrayList<Business>(), new HashSet<>(),
        new Date(10), GameState.SETUP, gameConfiguration);
  }

  @BeforeEach
  public void beforeEachTest() {
    MockitoAnnotations.initMocks(this);
    gameConfigManager = new GameConfigManager(gameDataAccess);
  }

  @Test
  void setGameData_should_callGameAccessDataOneTime_when_gameDataIsValid()
      throws UnknownGameException, IOException, UnknownGameDataException {

    gameConfigManager.setGameData(gameData);
    verify(gameDataAccess, atLeastOnce()).saveBeerGameSettings(any(BeerGame.class));
  }

  @Test
  void setGameData_should_notCallGameAccessData_when_gameDataIsInvalid()
      throws UnknownGameException {
    assertThrows(UnknownGameDataException.class, () -> gameConfigManager.setGameData(null));
    verify(gameDataAccess, never()).saveBeerGameSettings(any(BeerGame.class));
  }

  @Test
  void getGameData_should_throwException_when_beergGameIsNull() {
    assertThrows(NullPointerException.class,
        () -> gameConfigManager.getGameData());
  }

  @Test
  void getGamedate_should_returGameData_when_paramsAreValid() throws UnknownGameException {
    final Date date = beerGame.getDateTime();
    final String name = beerGame.getName();

    when(gameDataAccess.getBeerGame()).thenReturn(beerGame);
    GameData game = gameConfigManager.getGameData();

    assertEquals(game.getName(), name, "beergame name does not equal gamedata name");
    assertEquals(game.getDate(), date, "beergame date does not equal gamedata date");
  }

  @Test
  void isGameDataValid_should_returnFalse_when_gameDateIsInvalid() {
    final GameConfiguration gameConfiguration = new GameConfiguration(10, 20,
        SupplyChainType.LINEAR, 0,
        GameVisibilityType.INVISIBLE, null);
    final GameData invalid = new GameData("~$+<>", new Date(), gameConfiguration);
    assertFalse(gameConfigManager.isGameDataValid(invalid));
  }

  @Test
  void isGameDataValid_should_returnTrue_when_gameDateIsValid() {
    assertTrue(gameConfigManager.isGameDataValid(gameData));
  }

  @Test
  void getGamePricesForBusiness_should_return_correctPrices() {
    Business factory = FactoryBusinessBuilder.aFactoryBusiness().build();
    Business warehouse = RegionalWarehouseBusinessBuilder.aRegionalWarehouseBusiness().build();
    Business retailer = RetailBusinessBuilder.aRetailBusiness().build();
    Business wholesale = WholesaleBusinessBuilder.aWholesaleBusiness().build();

    GamePrices factoryPrice = new GamePrices(10, 40, 15, 20);
    GamePrices wholesalePrice = new GamePrices(70, 100, 15, 20);
    GamePrices retailPrice = new GamePrices(100, 130, 15, 20);
    GamePrices warehousePrice = new GamePrices(40, 70, 15, 20);

    assertGamePrice(factoryPrice,
        gameConfigManager.getGamePricesForBusiness(factory.getBusinessType()));
    assertGamePrice(warehousePrice,
        gameConfigManager.getGamePricesForBusiness(warehouse.getBusinessType()));
    assertGamePrice(retailPrice,
        gameConfigManager.getGamePricesForBusiness(retailer.getBusinessType()));
    assertGamePrice(wholesalePrice,
        gameConfigManager.getGamePricesForBusiness(wholesale.getBusinessType()));
  }

  void assertGamePrice(GamePrices testData, GamePrices realData) {
    assertEquals(testData.getIncomingGoodsPrice(), realData.getIncomingGoodsPrice());
    assertEquals(testData.getOpenOrdersPrice(), realData.getOpenOrdersPrice());
    assertEquals(testData.getOutgoingGoodsPrice(), realData.getOutgoingGoodsPrice());
    assertEquals(testData.getStockPrice(), realData.getStockPrice());
  }
}


