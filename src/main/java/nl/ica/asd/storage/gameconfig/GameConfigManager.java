package nl.ica.asd.storage.gameconfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import nl.ica.asd.logic.domain.BeerGame;
import nl.ica.asd.logic.domain.Business;
import nl.ica.asd.logic.domain.BusinessType;
import nl.ica.asd.logic.domain.GameState;
import nl.ica.asd.logic.domain.businesses.FactoryBusiness;
import nl.ica.asd.logic.domain.businesses.RegionalWarehouseBusiness;
import nl.ica.asd.logic.domain.businesses.RetailBusiness;
import nl.ica.asd.logic.domain.businesses.WholesaleBusiness;
import nl.ica.asd.logic.statecalculator.BusinessState;
import nl.ica.asd.storage.domain.GameConfiguration;
import nl.ica.asd.storage.domain.GameData;
import nl.ica.asd.storage.domain.GamePrices;
import nl.ica.asd.storage.exception.UnknownGameDataException;
import nl.ica.asd.storage.exception.UnknownGameException;
import nl.ica.asd.storage.gameconfig.validation.ValidationRuntimeException;
import nl.ica.asd.storage.gameconfig.validation.Validator;
import nl.ica.asd.storage.gameconfig.validation.rules.ValidateDateRule;
import nl.ica.asd.storage.gameconfig.validation.rules.ValidateIntRule;
import nl.ica.asd.storage.gameconfig.validation.rules.ValidateStringRule;
import nl.ica.asd.storage.gamedatastorage.GameDataAccess;
import nl.ica.asd.storage.gamedatastorage.GameDataStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("GameConfig")
public class GameConfigManager implements GameConfig {

  private static final Logger logger = LoggerFactory.getLogger(GameDataStorage.class);

  private GameDataAccess gameDataAccess;

  @Inject
  public GameConfigManager(@Named("GameDataAccess") GameDataAccess gameDataAccess) {
    this.gameDataAccess = gameDataAccess;
  }

  private boolean validateConfigFields(GameData data) {
    if (data == null) {
      return false;
    }

    try {
      final GameConfiguration settings = data.getSettings();
      new Validator<String>()
          .rule(data.getName(), ValidateStringRule.notNull)
          .rule(data.getName(), ValidateStringRule.matches("[\\p{L}\\p{Z}\\p{P}\\d]+"))
          .validate();
      new Validator<Date>()
          .rule(data.getDate(), ValidateDateRule.notNull)
          .validate();
      new Validator<Integer>()
          .rule(settings.getMaxPlayers(), ValidateIntRule.notNull)
          .rule(settings.getMaxPlayers(), ValidateIntRule.greaterThan(0))
          .rule(settings.getMaxRounds(), ValidateIntRule.notNull)
          .rule(settings.getMaxRounds(), ValidateIntRule.greaterThan(0))
          .rule(settings.getTimeLimitPerRound(), ValidateIntRule.notNull)
          .rule(settings.getTimeLimitPerRound(), ValidateIntRule.greaterThan(-1))
          .validate();
    } catch (ValidationRuntimeException e) {
      logger.error(e.getMessage());
      return false;
    }

    return true;
  }

  @Override
  public boolean isGameDataValid(GameData gameData) {
    return validateConfigFields(gameData);
  }

  @Override
  public GameData getDefaultGameData() throws IOException {
    return gameDataAccess.getDefaultGameData();
  }

  @Override
  public GameData getGameData() {
    BeerGame beergame;
    try {
      beergame = gameDataAccess.getBeerGame();
    } catch (UnknownGameException e) {
      throw new IllegalArgumentException("Combination of given name and date are invalid.");
    }

    return new GameData(beergame.getName(), beergame.getDateTime(),
        beergame.getGameConfiguration());
  }

  @Override
  public void setGameData(GameData gameData) throws UnknownGameDataException {
    if (!this.isGameDataValid(gameData)) {
      throw new UnknownGameDataException("gameData got invalid fields");
    }

    final GameConfiguration gameConfig = gameData.getSettings();
    try {
      BeerGame beerGame = new BeerGame(0, gameData.getName(), gameData.getDate(), new ArrayList<>(),
          new HashSet<>(), new Date(),
          GameState.SETUP, gameConfig);

      gameDataAccess.saveBeerGameSettings(beerGame);
      gameDataAccess.setCurrentGame(beerGame);
    } catch (UnknownGameException e) {
      logger.error(String
          .format("BeerGame could not be saved. System gave IOException: %s", e.getMessage()));
    }
  }

  @Override
  public GamePrices getGamePricesForBusiness(BusinessType type) {
    switch (type) {
      case FACTORY:
        return new GamePrices(10, 40, 15, 20);
      case WHOLESALE:
        return new GamePrices(70, 100, 15, 20);
      case RETAIL:
        return new GamePrices(100, 130, 15, 20);
      case REGIONAL_WAREHOUSE:
        return new GamePrices(40, 70, 15, 20);
      case NOT_SET:
        return new GamePrices(0, 0, 0, 0);
    }
    return new GamePrices(0, 0, 0, 0);
  }
  @Override
  public Map<Class<? extends Business>, BusinessState> getInitialBusinessStateLineair() {
    Map<Class<? extends Business>, BusinessState> initialBusinessState = new HashMap<>();
    initialBusinessState.put(FactoryBusiness.class, new BusinessState(6, 0, 3, 3, 3, 2000, 3, 0, 0));
    initialBusinessState.put(RegionalWarehouseBusiness.class, new BusinessState(6, 0, 3, 3, 3, 3000, 3, 0, 0));
    initialBusinessState.put(WholesaleBusiness.class, new BusinessState(6, 0, 3, 3, 3, 4500, 3, 0, 0));
    initialBusinessState.put(RetailBusiness.class, new BusinessState(6, 0, 3, 3, 3, 6000, 3, 0, 0));

    return initialBusinessState;
  }
}
