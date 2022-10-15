package nl.ica.asd.logic.postgameprocessor;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;
import nl.ica.asd.logic.domain.BeerGame;
import nl.ica.asd.logic.domain.Business;
import nl.ica.asd.logic.domain.GameState;
import nl.ica.asd.logic.domain.builders.BeerGameBuilder;
import nl.ica.asd.logic.domain.businesses.FactoryBusiness;
import nl.ica.asd.logic.domain.businesses.RegionalWarehouseBusiness;
import nl.ica.asd.logic.domain.businesses.RetailBusiness;
import nl.ica.asd.logic.domain.businesses.WholesaleBusiness;
import nl.ica.asd.logic.postgameprocessor.dto.BusinessRoundState;
import nl.ica.asd.logic.postgameprocessor.dto.PlayedGame;
import nl.ica.asd.logic.statecalculator.BusinessState;
import nl.ica.asd.logic.statecalculator.StateCalculator;
import nl.ica.asd.storage.exception.UnknownBusinessException;
import nl.ica.asd.storage.exception.UnknownGameException;
import nl.ica.asd.storage.gamedatastorage.GameDataAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("PostGameProcessor")
public class DefaultPostGameProcessor implements PostGameProcessor {

  @Inject
  private StateCalculator stateCalculator;

  @Inject
  private GameDataAccess gameDataAccess;

  private Logger logger = LoggerFactory.getLogger(DefaultPostGameProcessor.class);

  @Override
  public Set<PlayedGame> getPlayedBeerGames() {
    Set<PlayedGame> playedGames;
    Set<BeerGame> allGames = gameDataAccess.getAvailableGames();
    playedGames = allGames.stream()
        .filter(game -> game.getGameState() == GameState.FINISHED)
        .map(game -> new PlayedGame(game.getName(), game.getDateTime()))
        .collect(
            Collectors.toSet());
    return playedGames;
  }

  @Override
  public BeerGame getPlayedBeerGame(PlayedGame playedGame) throws PostGameProcessorException {
    Set<BeerGame> allGames = gameDataAccess.getAvailableGames();
    BeerGame beerGame = allGames.stream()
        .filter(
            game -> game.getName().equals(playedGame.getName()) &&
                game.getDateTime().equals(playedGame.getDate()))
        .findFirst()
        .orElseThrow(() -> new PostGameProcessorException("Beergame could not be found"));
    try {
      gameDataAccess.setCurrentGame(beerGame);
    } catch (UnknownGameException e) {
      logger.error(e.getMessage(), e);
    }
    return beerGame;
  }

  @Override
  public List<BusinessRoundState> getBeerGameTurn(int round) throws PostGameProcessorException {
    try {
      Map<UUID, BusinessState> businessStateMap = stateCalculator.getBusinessStatesForRound(round);

      return businessStateMap.entrySet().stream()
          .map(kvp -> new BusinessRoundState(kvp.getKey(), kvp.getValue().getIncomingOrders(),
              kvp.getValue().getOutgoingOrders(),
              kvp.getValue().getIncomingGoods(),
              kvp.getValue().getOutgoingGoods(), kvp.getValue().getStock(),
              kvp.getValue().getOpenOrders(), kvp.getValue().getBudget()))
          .collect(Collectors.toList());
    } catch (UnknownGameException | UnknownBusinessException exception) {
      String message = String
          .format("Unable to fetch business states from StateCalculator. Exception : %s",
              exception.getMessage());
      logger.error(message, exception);
      throw new PostGameProcessorException(message, exception);
    }
  }

  @Override
  public List<Business> getBeerGameBusinesses(BusinessLevelType businessLevelType)
      throws PostGameProcessorException {
    try {
      return gameDataAccess.getBeerGame().getBusinesses().stream()
          .filter(business -> isBusinessOfLevel(businessLevelType, business))
          .collect(Collectors.toList());
    } catch (UnknownGameException exception) {
      String message = String
          .format("Unable to fetch Businesses for level %s. Exception: %s", businessLevelType,
              exception.getMessage());
      logger.error(message, exception);
      throw new PostGameProcessorException(message, exception);
    }
  }

  private static boolean isBusinessOfLevel(BusinessLevelType businessLevelType, Business business) {
    switch (businessLevelType) {
      case FACTORY:
        return business instanceof FactoryBusiness;
      case REGIONALWAREHOUSE:
        return business instanceof RegionalWarehouseBusiness;
      case RETAIL:
        return business instanceof RetailBusiness;
      case WHOLESALE:
        return business instanceof WholesaleBusiness;
      default:
        throw new IllegalArgumentException();
    }
  }

  @Override
  public Business getBeerGameBusiness(UUID uuid)
      throws PostGameProcessorException {
    try {
      return gameDataAccess.getBusinesses().stream()
          .filter(business -> business.getUUID().equals(uuid))
          .findFirst()
          .orElseThrow(() -> new PostGameProcessorException("Business could not be found"));
    } catch (UnknownGameException e) {
      logger.error(String.format("Businesses could not be found %s", e.getMessage()));
      throw new PostGameProcessorException("Businesses could not be found", e);
    }
  }

  @Override
  public void saveCompletedBeerGame(BeerGame beergame)
      throws PostGameProcessorException {
    if (beergame == null) {
      throw new IllegalArgumentException("beergame can't be null");
    }

    BeerGame finishedBeerGame = BeerGameBuilder.fromBaseBeerGame(beergame)
        .withGameState(GameState.FINISHED).build();

    try {
      gameDataAccess.setCurrentGame(finishedBeerGame);
    } catch (UnknownGameException exception) {
      String message = String.format("Unable to set finished beer game as default. Exception: %s",
          exception.getMessage());
      logger.error(message, exception);
      throw new PostGameProcessorException(message, exception);
    }

    try {
      gameDataAccess.saveBeerGameSettings(finishedBeerGame);
    } catch (UnknownGameException exception) {
      String message = String
          .format("Unable to save finished beer game. Exception: %s", exception.getMessage());
      logger.error(exception.getMessage());
      throw new PostGameProcessorException(message, exception);
    }
  }
}
