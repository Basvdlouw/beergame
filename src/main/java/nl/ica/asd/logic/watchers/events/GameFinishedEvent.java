package nl.ica.asd.logic.watchers.events;

import nl.ica.asd.logic.domain.BeerGame;

public class GameFinishedEvent extends Event {

  private BeerGame beerGame;

  public GameFinishedEvent(BeerGame beerGame) {
    this.beerGame = beerGame;
  }

  public BeerGame getBeerGame() {
    return beerGame;
  }
}
