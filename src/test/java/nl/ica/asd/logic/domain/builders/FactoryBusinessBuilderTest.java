package nl.ica.asd.logic.domain.builders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import nl.ica.asd.logic.domain.businesses.FactoryBusiness;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class FactoryBusinessBuilderTest {

  @Mock
  private FactoryBusiness factoryBusiness;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.initMocks(this);
//    when(factoryBusiness.getFactoryStep1()).thenReturn(10);
  }

  @Test
  void aFactoryBusiness_should_returnNotNull() {
    assertNotNull(FactoryBusinessBuilder.aFactoryBusiness());
  }


  @Test
  void when_changedVariablesInBuilder_fromBaseBusiness_should_buildBusinessWithNewVariables() {
//    final int newFactoryStep = factoryBusiness.getFactoryStep1() * 10;
//    final FactoryBusiness buildFactoryBusiness = FactoryBusinessBuilder
//        .fromBaseBusiness(factoryBusiness)
//        .withFactoryStep1(newFactoryStep)
//        .build();
//    assertNotEquals(factoryBusiness.getFactoryStep1(), buildFactoryBusiness.getFactoryStep1(),
//        "getFactoryStep1 of businesses should not be the same");
//
//    assertEquals(buildFactoryBusiness.getFactoryStep1(), newFactoryStep,
//        "getFactoryStep1 of businesses should be the same");
  }
}

