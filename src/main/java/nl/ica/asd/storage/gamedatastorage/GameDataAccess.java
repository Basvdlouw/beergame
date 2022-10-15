package nl.ica.asd.storage.gamedatastorage;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import nl.ica.asd.logic.domain.Action;
import nl.ica.asd.logic.domain.BeerGame;
import nl.ica.asd.logic.domain.Business;
import nl.ica.asd.logic.domain.BusinessRules;
import nl.ica.asd.logic.domain.BusinessType;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.storage.domain.GameData;
import nl.ica.asd.storage.exception.UnknownBusinessException;
import nl.ica.asd.storage.exception.UnknownException;
import nl.ica.asd.storage.exception.UnknownGameException;

public interface GameDataAccess {

  boolean hasCurrentGame();

  void setCurrentGame(BeerGame currentBeerGame) throws UnknownGameException;

  GameData getDefaultGameData() throws IOException;

  BeerGame getBeerGame() throws UnknownGameException;

  void saveBeerGameSettings(BeerGame beerGame) throws UnknownGameException;

  Set<Player> getPlayerList() throws UnknownGameException;

  void savePlayerList(Set<Player> playerList) throws UnknownGameException;

  void saveCurrentActions(List<? extends Action> actions, int round)
      throws UnknownGameException;

  Map<Integer, List<Action>> getAllActionsPerBusiness(UUID uuid)
      throws UnknownGameException, UnknownBusinessException;

  String getBusinessRules(Business business) throws UnknownGameException;

  void saveBusinessRules(BusinessRules businessRules)
      throws UnknownException;

  Set<BeerGame> getAvailableGames();

  List<Business> getBusinesses() throws UnknownGameException;

  int getCurrentRound() throws UnknownGameException;

  BusinessType getBusinessType(UUID businessUUID)
      throws UnknownGameException, UnknownBusinessException;

  int getAmountOfBusinessesAvailable(BusinessType businessType) throws UnknownGameException;

  Business getBusinessByUUID(UUID uuid) throws UnknownBusinessException, UnknownGameException;

  void reset();
}
