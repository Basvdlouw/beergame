package nl.ica.asd.storage.gameconfig;

import java.io.IOException;
import java.util.Map;
import nl.ica.asd.logic.domain.Business;
import nl.ica.asd.logic.domain.BusinessType;
import nl.ica.asd.logic.statecalculator.BusinessState;
import nl.ica.asd.storage.domain.GameData;
import nl.ica.asd.storage.domain.GamePrices;
import nl.ica.asd.storage.exception.UnknownGameDataException;

public interface GameConfig {

  boolean isGameDataValid(GameData gameData);

  GameData getDefaultGameData() throws IOException;

  GameData getGameData();

  void setGameData(GameData gameData) throws UnknownGameDataException;

  GamePrices getGamePricesForBusiness(BusinessType type);

  Map<Class<? extends Business>, BusinessState> getInitialBusinessStateLineair();
}
