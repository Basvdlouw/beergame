package nl.ica.asd.logic.gamemanager.gamestatemanager.businessinitializer;

import java.util.List;
import java.util.UUID;
import nl.ica.asd.logic.domain.Agent;
import nl.ica.asd.logic.domain.BeerGame;
import nl.ica.asd.logic.domain.Business;
import nl.ica.asd.logic.domain.builders.AgentBuilder;

public abstract class BusinessInitializer {

  private static int uuidSeed = 0;

  public abstract List<Business> initialize(BeerGame beerGame, List<Business> businesses);

  protected synchronized UUID generateBusinessUUID(BeerGame beerGame) {
    uuidSeed++;
    return UUID.nameUUIDFromBytes((beerGame.getName() + uuidSeed).getBytes());
  }

  protected Agent createAgent(String rules) {
    return AgentBuilder.anAgent().withBusinessRules(rules).build();
  }


}
