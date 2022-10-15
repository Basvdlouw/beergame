package nl.ica.asd.frontend.gui.evaluate.linear.business;

import java.util.Observable;
import nl.ica.asd.frontend.gui.service.ThreadRipperObserver;
import nl.ica.asd.logic.postgameprocessor.dto.BusinessRoundState;

public class BusinessBlockObserver extends ThreadRipperObserver {

  private final BusinessBlockViewModel businessBlockViewModel;

  public BusinessBlockObserver(BusinessBlockViewModel businessBlockViewModel) {
    this.businessBlockViewModel = businessBlockViewModel;
  }

  @Override
  protected void synchronizedUpdate(Observable observable, Object argument) {

    if (argument instanceof BusinessRoundState
        && businessBlockViewModel.getBusinessId() == ((BusinessRoundState) argument)
        .getBusinessUuid()) {
      BusinessRoundState businessRoundState = (BusinessRoundState) argument;

      businessBlockViewModel.setIncomingGoods(businessRoundState.getIncomingGoods());
      businessBlockViewModel.setIncomingOrders(businessRoundState.getIncomingOrders());
      businessBlockViewModel.setOutgoingGoods(businessRoundState.getOutgoingGoods());
      businessBlockViewModel.setPlacedOrders(businessRoundState.getOutgoingOrders());
      businessBlockViewModel.setBudgetProperty(businessRoundState.getBudget());
      businessBlockViewModel.setOpenOrdersProperty(businessRoundState.getOpenOrders());
      businessBlockViewModel.setStockProperty(businessRoundState.getStock());
    }
  }
}