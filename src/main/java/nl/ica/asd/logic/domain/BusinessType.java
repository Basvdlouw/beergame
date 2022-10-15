package nl.ica.asd.logic.domain;

import nl.ica.asd.logic.domain.builders.BusinessBuilder;
import nl.ica.asd.logic.domain.builders.FactoryBusinessBuilder;
import nl.ica.asd.logic.domain.builders.RegionalWarehouseBusinessBuilder;
import nl.ica.asd.logic.domain.builders.RetailBusinessBuilder;
import nl.ica.asd.logic.domain.builders.WholesaleBusinessBuilder;
import nl.ica.asd.logic.domain.businesses.FactoryBusiness;
import nl.ica.asd.logic.domain.businesses.RegionalWarehouseBusiness;
import nl.ica.asd.logic.domain.businesses.RetailBusiness;
import nl.ica.asd.logic.domain.businesses.WholesaleBusiness;

public enum BusinessType {
  FACTORY("Factory", 1, "\uD83C\uDFED"),
  REGIONAL_WAREHOUSE("Regional warehouse", 2, "\uD83C\uDFEC"),
  WHOLESALE("Wholesale", 3, "\uD83C\uDFEA"),
  RETAIL("Retail", 4, "\uD83C\uDF7A"),
  NOT_SET("Business not set yet", -1, "X");

  private final String businessName;
  private final int indexInChain;
  private final String icon;

  BusinessType(String businessName, int indexInChain, String icon) {
    this.businessName = businessName;
    this.indexInChain = indexInChain;
    this.icon = icon;
  }

  public String getBusinessName() {
    return businessName;
  }

  public int getIndexInChain() {
    return indexInChain;
  }

  public static BusinessType getBusinessTypeFromClass(Class<? extends Business> classType) {
    if (classType.equals(FactoryBusiness.class)) {
      return FACTORY;
    }
    else if (classType.equals(RegionalWarehouseBusiness.class)) {
      return REGIONAL_WAREHOUSE;
    }
    else if (classType.equals(WholesaleBusiness.class)) {
      return WHOLESALE;
    }
    else if (classType.equals(RetailBusiness.class)) {
      return RETAIL;
    }
    else {
      return NOT_SET;
    }
  }

  public BusinessBuilder getBuilder() {
    switch (this) {
      case FACTORY:
        return FactoryBusinessBuilder.aFactoryBusiness();
      case REGIONAL_WAREHOUSE:
        return RegionalWarehouseBusinessBuilder.aRegionalWarehouseBusiness();
      case WHOLESALE:
        return WholesaleBusinessBuilder.aWholesaleBusiness();
      case RETAIL:
        return RetailBusinessBuilder.aRetailBusiness();
      default:
        return null;
    }
  }

  public BusinessBuilder getBuilderFromBusiness(Business business) {
    switch (this) {
      case FACTORY:
        return FactoryBusinessBuilder.fromBaseBusiness((FactoryBusiness) business);
      case REGIONAL_WAREHOUSE:
        return RegionalWarehouseBusinessBuilder
            .fromBaseBusiness((RegionalWarehouseBusiness) business);
      case WHOLESALE:
        return WholesaleBusinessBuilder.fromBaseBusiness((WholesaleBusiness) business);
      case RETAIL:
        return RetailBusinessBuilder.fromBaseBusiness((RetailBusiness) business);
      default:
        return null;
    }
  }

  public String getIcon() {
    return icon;
  }
}
