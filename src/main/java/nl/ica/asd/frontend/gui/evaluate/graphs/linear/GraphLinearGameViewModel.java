package nl.ica.asd.frontend.gui.evaluate.graphs.linear;

import nl.ica.asd.logic.postgameprocessor.dto.PlayedGame;

public class GraphLinearGameViewModel {

  private PlayedGame playedGame;

  public PlayedGame getPlayedGame() {
    return playedGame;
  }

  public GraphLinearGameViewModel(PlayedGame playedGame) {
    this.playedGame = playedGame;
  }
}
