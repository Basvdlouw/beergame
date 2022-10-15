package nl.ica.asd.logic.domain.builders;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import nl.ica.asd.logic.domain.Action;
import nl.ica.asd.logic.domain.Agent;
import nl.ica.asd.logic.domain.Business;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.logic.statecalculator.BusinessState;

public abstract class BusinessBuilder<S extends BusinessBuilder, B extends Business> {

  int budget;
  int stock;
  Map<Integer, List<Action>> actionsPerRound;
  Agent agent;
  UUID uuid;
  Player player;

  public abstract S fromBase(B business);

  public abstract S self();

  public abstract B build();

  public S withBudget(int budget) {
    this.budget = budget;
    return self();
  }

  public S withStock(int stock) {
    this.stock = stock;
    return self();
  }

  public S withActionsPerRound(Map<Integer, List<Action>> actionsPerRound) {
    this.actionsPerRound = actionsPerRound;
    return self();
  }

  public S withAgent(Agent agent) {
    this.agent = agent;
    return self();
  }

  public S withUuid(UUID uuid) {
    this.uuid = uuid;
    return self();
  }

  public S withPlayer(Player player) {
    this.player = player;
    return self();
  }

  public S withBusinessState(BusinessState businessState) {
    withBudget(businessState.getBudget());
    withStock(businessState.getStock());
    return self();
  }
}
