package nl.ica.asd.logic.domain.builders;

import java.util.Date;
import java.util.List;
import java.util.Set;
import nl.ica.asd.logic.domain.BeerGame;
import nl.ica.asd.logic.domain.Business;
import nl.ica.asd.logic.domain.GameState;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.storage.domain.GameConfiguration;

public final class BeerGameBuilder {

  private int currentRound;
  private String name;
  private Date dateTime;
  private List<Business> businesses;
  private Set<Player> players;
  private Date startTimeCurrentRound;
  private GameState gameState;
  private GameConfiguration gameConfiguration;

  private BeerGameBuilder() {
  }

  public static BeerGameBuilder aBeerGame() {
    return new BeerGameBuilder();
  }

  public static BeerGameBuilder fromBaseBeerGame(BeerGame beerGame) {
    return new BeerGameBuilder()
        .withCurrentRound(beerGame.getCurrentRound())
        .withName(beerGame.getName())
        .withDateTime(beerGame.getDateTime())
        .withBusinesses(beerGame.getBusinesses())
        .withPlayers(beerGame.getPlayers())
        .withStartTimeCurrentRound(beerGame.getStartTimeCurrentRound())
        .withGameState(beerGame.getGameState())
        .withGameConfiguration(beerGame.getGameConfiguration());
  }

  public BeerGameBuilder withCurrentRound(int currentRound) {
    this.currentRound = currentRound;
    return this;
  }

  public BeerGameBuilder withName(String name) {
    this.name = name;
    return this;
  }

  public BeerGameBuilder withDateTime(Date dateTime) {
    this.dateTime = dateTime;
    return this;
  }

  public BeerGameBuilder withBusinesses(List<Business> businesses) {
    this.businesses = businesses;
    return this;
  }

  public BeerGameBuilder withPlayers(Set<Player> players) {
    this.players = players;
    return this;
  }

  public BeerGameBuilder withStartTimeCurrentRound(Date startTimeCurrentRound) {
    this.startTimeCurrentRound = startTimeCurrentRound;
    return this;
  }

  public BeerGameBuilder withGameState(GameState gameState) {
    this.gameState = gameState;
    return this;
  }

  public BeerGameBuilder withGameConfiguration(GameConfiguration gameConfiguration) {
    this.gameConfiguration = gameConfiguration;
    return this;
  }

  public BeerGame build() {
    return new BeerGame(currentRound, name, dateTime, businesses, players, startTimeCurrentRound,
        gameState, gameConfiguration);
  }
}
