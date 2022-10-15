package nl.ica.asd.frontend.gui.evaluate.linear;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import nl.ica.asd.logic.domain.BeerGame;

public class EvaluateLinearGameViewModel {

  private BeerGame beerGame;

  private IntegerProperty roundProperty = new SimpleIntegerProperty(0);
  private IntegerProperty playedRounds = new SimpleIntegerProperty(0);
  private IntegerProperty playedRoundsLabel = new SimpleIntegerProperty(0);
  private IntegerProperty roundPropertyLabel = new SimpleIntegerProperty(1);

  public void setBeerGame(BeerGame selectedItem) {
    beerGame = selectedItem;
    playedRounds.set(getPlayedRounds());
    playedRoundsLabel.set(getPlayedRounds()+1);
  }

  public BeerGame getBeerGame() {
    return beerGame;
  }


  public IntegerProperty getRound() {
    return roundProperty;
  }

  public IntegerProperty getPlayedRoundsLabel() {
    return playedRoundsLabel;
  }
  public IntegerProperty getRoundPropertyLabel() {
    return roundPropertyLabel;
  }

  public void setRound(int value) {
    roundPropertyLabel.set(value+1);
    roundProperty.set(value);
  }

  public int getPlayedRounds() {
    return beerGame.getCurrentRound() - 1;
  }

  public void incrementRound() {
    if (getRound().get() + 1 <= playedRounds.get()) {
      setRound(getRound().get() + 1);
    }
  }

  public void decrementRound() {
    if (getRound().get() - 1 >= 0) {
      setRound(getRound().get() - 1);
    }
  }
}