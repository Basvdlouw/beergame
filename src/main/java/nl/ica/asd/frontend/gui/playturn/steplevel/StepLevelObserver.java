package nl.ica.asd.frontend.gui.playturn.steplevel;

import java.util.Observable;
import nl.ica.asd.frontend.gui.service.ThreadRipperObserver;
import nl.ica.asd.frontend.gui.service.dto.PlayTurnState;

public class StepLevelObserver extends ThreadRipperObserver {

  private StepLevelViewModel viewModel;

  public StepLevelObserver(StepLevelViewModel viewModel) {
    this.viewModel = viewModel;
  }

  @Override
  protected void synchronizedUpdate(Observable observable, Object argument) {
    if (argument instanceof PlayTurnState) {

      switch (viewModel.getStep()) {
        case INCOMINGGOODS:
          viewModel.setStepValue(((PlayTurnState) argument).getIncomingGoods());
          break;

        case STOCK:
          viewModel.setStepValue(((PlayTurnState) argument).getStock());
          break;

        case OPENORDERS:
          viewModel.setStepValue(((PlayTurnState) argument).getOpenOrders());
          break;

        case BUDGET:
          viewModel.setStepValue(((PlayTurnState) argument).getBudget());
          break;

        case OUTGOINGGOODS:
          viewModel.setStepValue(((PlayTurnState) argument).calculateOutgoingGoods());
          break;

        case INCOMINGORDERS:
          viewModel.setStepValue(((PlayTurnState) argument).getIncomingOrders());
          break;
        default:
      }
    }
  }
}