package nl.ica.asd.frontend.gui.playturn;

import java.util.Observable;
import nl.ica.asd.frontend.gui.service.ThreadRipperObserver;
import nl.ica.asd.frontend.gui.service.dto.PlayTurnState;

public class PlayTurnObserver extends ThreadRipperObserver {

  private PlayTurnViewModel viewModel;

  public PlayTurnObserver(PlayTurnViewModel viewModel) {
    this.viewModel = viewModel;
  }

  @Override
  protected void synchronizedUpdate(Observable observable, Object argument) {
    if (argument instanceof PlayTurnState) {
      int currentRound = ((PlayTurnState) argument).getCurrentRound();

      if (currentRound != viewModel.getRound()) {
        viewModel.setRound(currentRound);
        viewModel.setTimer(((PlayTurnState) argument).getCurrentTime());
      }
    }
  }
}