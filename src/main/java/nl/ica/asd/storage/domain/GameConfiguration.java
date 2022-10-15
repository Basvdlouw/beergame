package nl.ica.asd.storage.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import nl.ica.asd.logic.domain.Business;
import nl.ica.asd.logic.domain.GameVisibilityType;
import nl.ica.asd.logic.domain.SupplyChainType;
import nl.ica.asd.logic.statecalculator.BusinessState;

public class GameConfiguration {

  private final int maxPlayers;
  private final int maxRounds;
  private final SupplyChainType gameType;
  private final int timeLimitPerRound;
  private final GameVisibilityType visibility;
  private final Map<Class<? extends Business>, BusinessState> initialBusinessStates;

  @JsonCreator
  public GameConfiguration(@JsonProperty("maxPlayers") int maxPlayers,
      @JsonProperty("maxRounds") int maxRounds,
      @JsonProperty("supplyChainType") SupplyChainType gameType,
      @JsonProperty("timeLimitPerRound") int timeLimitPerRound,
      @JsonProperty("gameVisibilityType") GameVisibilityType visibility,
      @JsonProperty("initialValues") Map<Class<? extends Business>, BusinessState> initialBusinessStates) {
    this.maxPlayers = maxPlayers;
    this.maxRounds = maxRounds;
    this.gameType = gameType;
    this.timeLimitPerRound = timeLimitPerRound;
    this.visibility = visibility;
    this.initialBusinessStates = initialBusinessStates;
  }

  public int getMaxPlayers() {
    return maxPlayers;
  }

  public int getMaxRounds() {
    return maxRounds;
  }

  public SupplyChainType getSupplyChainType() {
    return gameType;
  }

  public int getTimeLimitPerRound() {
    return timeLimitPerRound;
  }

  public GameVisibilityType getGameVisibilityType() {
    return visibility;
  }

  public Map<Class<? extends Business>, BusinessState> getInitialBusinessStates() {
    return initialBusinessStates;
  }
}
