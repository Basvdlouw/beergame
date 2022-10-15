package nl.ica.asd.logic.statecalculator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BusinessStateBuilderTest {

  @Mock
  private BusinessState businessState;

  @Test
  void aBusinessState_should_returnNotNull() {
    assertNotNull(BusinessStateBuilder.aBusinessState());
  }

  @Test
  void when_changedVariablesInBuilder_fromBaseAgent_should_buildAgentWithNewVariables() {
    when(businessState.getStock()).thenReturn(10);
    when(businessState.getBudget()).thenReturn(50);
    when(businessState.getIncomingGoods()).thenReturn(5);
    when(businessState.getOutgoingGoods()).thenReturn(5);
    when(businessState.getIncomingOrders()).thenReturn(5);
    when(businessState.getOpenOrders()).thenReturn(0);

    int newStock = 15;
    final BusinessState buildBusinessState = BusinessStateBuilder
        .fromBaseBusinessState(businessState).withStock(newStock).build();
    assertNotEquals(businessState.getStock(), buildBusinessState.getStock(),
        "stock of BusinessStates should not be the same");
    assertEquals(newStock, buildBusinessState.getStock(),
        String.format("new stock of BusinessState should be %s", newStock));
  }
}
