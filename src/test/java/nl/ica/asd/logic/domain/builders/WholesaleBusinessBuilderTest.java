package nl.ica.asd.logic.domain.builders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import nl.ica.asd.logic.domain.businesses.WholesaleBusiness;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class WholesaleBusinessBuilderTest {

  @Mock
  private WholesaleBusiness wholesaleBusiness;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.initMocks(this);
    when(wholesaleBusiness.getStock()).thenReturn(10);
  }

  @Test
  void aWholesaleBusiness_should_returnNotNull() {
    assertNotNull(WholesaleBusinessBuilder.aWholesaleBusiness());
  }

  @Test
  void when_changedVariablesInBuilder_fromBaseBusiness_should_buildBusinessWithNewVariables() {
    final int newStock = wholesaleBusiness.getStock() * 10;
    final WholesaleBusiness buildWholesaleBusiness = WholesaleBusinessBuilder
        .fromBaseBusiness(wholesaleBusiness)
        .withStock(newStock)
        .build();
    assertNotEquals(wholesaleBusiness.getStock(), buildWholesaleBusiness.getStock(),
        "getStock of businesses should not be the same");

    assertEquals(buildWholesaleBusiness.getStock(), newStock,
        "getStock of businesses should be the same");
  }
}
