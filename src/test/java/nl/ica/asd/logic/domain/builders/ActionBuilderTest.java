package nl.ica.asd.logic.domain.builders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import nl.ica.asd.logic.domain.Action;
import nl.ica.asd.logic.domain.actions.DeliveryAction;
import nl.ica.asd.logic.domain.actions.OrderAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ActionBuilderTest {

  @Mock
  private Action action;

  @Mock
  private DeliveryAction deliveryAction;

  @Mock
  private OrderAction orderAction;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.initMocks(this);
    when(action.getAmount()).thenReturn(15);
    when(deliveryAction.getAmount()).thenReturn(5);
    when(orderAction.getAmount()).thenReturn(7);
  }

  @Test
  void anAgent_should_returnNotNull() {
    assertNotNull(ActionBuilder.anAction());
  }

  @Test
  void when_changedVariablesInBuilder_fromBaseAction_should_buildDeliveryActionWithNewAmount() {
    final int newAmount = 100;
    final DeliveryAction buildAction = ActionBuilder.fromBaseAction(deliveryAction)
        .withAmount(newAmount)
        .build();
    assertNotNull(buildAction);
    assertNotEquals(deliveryAction.getAmount(), buildAction.getAmount(),
        "amounts of actions should not be the same");
    assertEquals(newAmount, buildAction.getAmount(),
        String.format("amount of new action should be %d", newAmount));
  }

  @Test
  void when_changedVariablesInBuilder_fromBaseAction_should_buildOrderActionWithNewAmount() {
    final int newAmount = 100;
    final OrderAction buildAction = ActionBuilder.fromBaseAction(orderAction)
        .withAmount(newAmount)
        .build();
    assertNotNull(buildAction);
    assertNotEquals(orderAction.getAmount(), buildAction.getAmount(),
        "amounts of actions should not be the same");
    assertEquals(newAmount, buildAction.getAmount(),
        String.format("amount of new action should be %d", newAmount));
  }
}
