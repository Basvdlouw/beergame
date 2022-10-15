package nl.ica.asd.logic.domain.builders;

import static org.junit.jupiter.api.Assertions.assertEquals;

import nl.ica.asd.logic.domain.BusinessType;
import org.junit.jupiter.api.Test;

public class BusinessTypeBuilders {

//  @Test
//  void businessType_should_return_rightBuildersForRightTypes() {
//    assertEquals(FactoryBusinessBuilder.class, BusinessType.FACTORY.getBuilder().getClass());
//    assertEquals(RegionalWarehouseBusinessBuilder.class,
//        BusinessType.REGIONAL_WAREHOUSE.getBuilder().getClass());
//    assertEquals(WholesaleBusinessBuilder.class, BusinessType.WHOLESALE.getBuilder().getClass());
//    assertEquals(RetailBusinessBuilder.class, BusinessType.RETAIL.getBuilder().getClass());
//    assertEquals(FactoryBusinessBuilder.class, BusinessType.NOT_SET.getBuilder().getClass());
//  }

  @Test
  void checkBusinessTypeNamingAndIndexing() {
    assertEquals("Factory", BusinessType.FACTORY.getBusinessName());
    assertEquals(1, BusinessType.FACTORY.getIndexInChain());
    assertEquals("Regional warehouse", BusinessType.REGIONAL_WAREHOUSE.getBusinessName());
    assertEquals(2, BusinessType.REGIONAL_WAREHOUSE.getIndexInChain());
    assertEquals("Wholesale", BusinessType.WHOLESALE.getBusinessName());
    assertEquals(3, BusinessType.WHOLESALE.getIndexInChain());
    assertEquals("Retail", BusinessType.RETAIL.getBusinessName());
    assertEquals(4, BusinessType.RETAIL.getIndexInChain());
    assertEquals("Business not set yet", BusinessType.NOT_SET.getBusinessName());
    assertEquals(-1, BusinessType.NOT_SET.getIndexInChain());
  }

}
