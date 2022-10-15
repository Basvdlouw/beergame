package nl.ica.asd.logic.postgameprocessor;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import nl.ica.asd.logic.domain.BeerGame;
import nl.ica.asd.logic.domain.Business;
import nl.ica.asd.logic.postgameprocessor.dto.BusinessRoundState;
import nl.ica.asd.logic.postgameprocessor.dto.PlayedGame;

public interface PostGameProcessor {

  /**
   * This method is used to get all the beergames that have been played
   *
   * @return List of PlayedGames that need to be displayed to the user
   */
  Set<PlayedGame> getPlayedBeerGames();

  /**
   * This method gets a specific beergame based on the name and date of the beergame
   *
   * @param playedGame the name and date of a played beergame
   * @return The beergame with the same name and date as the given playedgame object
   */
  BeerGame getPlayedBeerGame(PlayedGame playedGame) throws PostGameProcessorException;

  /**
   * This method gets all the actions from a specific turn in a played beergame
   *
   * @param round the number of the round in the beergame
   * @return List of BusinessRoundState's for each Business in that turn of the beergame
   * @throws PostGameProcessorException when no business states are fetched from the state calculator
   */
  List<BusinessRoundState> getBeerGameTurn(int round) throws PostGameProcessorException;

  /**
   * This method gets all the businesses that are on the same level
   *
   * @param businessLevelType this level type relates to the type of business
   * @return List of Businesses that are on the given level
   * @throws PostGameProcessorException when no businesses can be fetched from a beergame.
   */
  List<Business> getBeerGameBusinesses(BusinessLevelType businessLevelType)
      throws PostGameProcessorException;

  /**
   * This method gets a specific business, based on the given level and the given identifier
   *
   * @param uuid this number relates to a specific player
   * @return A specific business
   */
  Business getBeerGameBusiness(UUID uuid)
      throws PostGameProcessorException;

  /**
   * This method saves the data of a completed beergame
   * @param beergame the completed beergame
   * @throws IllegalArgumentException when beergame is null
   * @throws PostGameProcessorException when beergame can't be set as default or unable to save it
   */
  void saveCompletedBeerGame(BeerGame beergame)
      throws PostGameProcessorException;

}
