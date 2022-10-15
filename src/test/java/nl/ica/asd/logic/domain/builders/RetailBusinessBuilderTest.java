package nl.ica.asd.logic.domain.builders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import nl.ica.asd.logic.domain.businesses.RetailBusiness;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class RetailBusinessBuilderTest {

  @Mock
  private RetailBusiness retailBusiness;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.initMocks(this);
    when(retailBusiness.getStock()).thenReturn(10);
  }


  @Test
  void aRetailBusiness_should_returnNotNull() {
    assertNotNull(RetailBusinessBuilder.aRetailBusiness());
  }

  @Test
  void when_changedVariablesInBuilder_fromBaseBusiness_should_buildBusinessWithNewVariables() {
    final int newStock = retailBusiness.getStock() * 10;
    final RetailBusiness buildRetailBusiness = RetailBusinessBuilder
        .fromBaseBusiness(retailBusiness)
        .withStock(newStock)
        .build();
    assertNotEquals(retailBusiness.getStock(), buildRetailBusiness.getStock(),
        "getStock of businesses should not be the same");

    assertEquals(buildRetailBusiness.getStock(), newStock,
        "getStock of businesses should be the same");
  }
}
