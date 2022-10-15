package nl.ica.asd.logic.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Date;
import java.util.List;
import java.util.Set;
import nl.ica.asd.storage.domain.GameConfiguration;

@JsonPropertyOrder({"currentRound", "name", "dateTime", "businesses", "players",
    "startTimeCurrentRound", "gameState", "gameConfiguration"})
public class BeerGame {

  private final int currentRound;
  private final String name;
  private final Date dateTime;
  private final List<Business> businesses;
  private final Set<Player> players;
  private final GameState gameState;
  private final Date startTimeCurrentRound;
  private final GameConfiguration gameConfiguration;

  @JsonCreator
  public BeerGame(
      @JsonProperty("currentRound") int currentRound,
      @JsonProperty("name") String name,
      @JsonProperty("dateTime") Date dateTime,
      @JsonProperty("businesses") List<Business> businesses,
      @JsonProperty("players") Set<Player> players,
      @JsonProperty("startTimeCurrentRound") Date startTimeCurrentRound,
      @JsonProperty("gameState") GameState gameState,
      @JsonProperty("gameConfiguration") GameConfiguration gameConfiguration
  ) {
    this.currentRound = currentRound;
    this.name = name;
    this.dateTime = dateTime;
    this.businesses = businesses;
    this.players = players;
    this.startTimeCurrentRound = startTimeCurrentRound;
    this.gameState = gameState;
    this.gameConfiguration = gameConfiguration;
  }

  @JsonGetter("currentRound")
  public int getCurrentRound() {
    return currentRound;
  }

  @JsonGetter("name")
  public String getName() {
    return name;
  }

  @JsonGetter("dateTime")
  public Date getDateTime() {
    return dateTime;
  }

  @JsonGetter("businesses")
  public List<Business> getBusinesses() {
    return businesses;
  }

  @JsonGetter("players")
  public Set<Player> getPlayers() {
    return players;
  }

  @JsonGetter("startTimeCurrentRound")
  public Date getStartTimeCurrentRound() {
    return startTimeCurrentRound;
  }

  @JsonGetter("gameState")
  public GameState getGameState() {
    return gameState;
  }

  @JsonGetter("gameConfiguration")
  public GameConfiguration getGameConfiguration() {
    return gameConfiguration;
  }
}
