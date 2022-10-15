package nl.ica.asd.logic.domain.builders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import nl.ica.asd.logic.domain.businesses.RegionalWarehouseBusiness;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class RegionalWarehouseBusinessBuilderTest {

  @Mock
  private RegionalWarehouseBusiness regionalWarehouseBusiness;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.initMocks(this);
    when(regionalWarehouseBusiness.getStock()).thenReturn(10);
  }

  @Test
  void aRegionalWarehouseBusiness_should_returnNotNull() {
    assertNotNull(RegionalWarehouseBusinessBuilder.aRegionalWarehouseBusiness());
  }

  @Test
  void when_changedVariablesInBuilder_fromBaseBusiness_should_buildBusinessWithNewVariables() {
    final int newStock = regionalWarehouseBusiness.getStock() * 10;
    final RegionalWarehouseBusiness buildRegionalWarehouseBusiness = RegionalWarehouseBusinessBuilder
        .fromBaseBusiness(regionalWarehouseBusiness)
        .withStock(newStock)
        .build();
    assertNotEquals(regionalWarehouseBusiness.getStock(), buildRegionalWarehouseBusiness.getStock(),
        "getStock of businesses should not be the same");

    assertEquals(buildRegionalWarehouseBusiness.getStock(), newStock,
        "getStock of businesses should be the same");
  }
}
