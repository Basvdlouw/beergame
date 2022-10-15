package nl.ica.asd.frontend.gui.converters;

import java.util.ResourceBundle;
import javafx.util.StringConverter;
import nl.ica.asd.logic.domain.Business;
import nl.ica.asd.logic.domain.businesses.FactoryBusiness;
import nl.ica.asd.logic.domain.businesses.RegionalWarehouseBusiness;
import nl.ica.asd.logic.domain.businesses.RetailBusiness;
import nl.ica.asd.logic.domain.businesses.WholesaleBusiness;

public class PlayerLevelStringConverter extends StringConverter<String> {
  private final ResourceBundle resourceBundle;

  private static final String UNDEFINED_KEY = "steplevel.undefined";
  private static final String SUPPLIER_KEY = "playturn.supplier";
  private static final String FACTORY_KEY = "playturn.factory";
  private static final String WAREHOUSE_KEY = "playturn.regional_warehouse";
  private static final String WHOLESALE_KEY = "playturn.wholesale";
  private static final String RETAIL_KEY = "playturn.retail";
  private static final String DEMAND_KEY = "playturn.demand";

  private static final String SUPPLIER = "SUPPLIER";
  private static final String FACTORY = "FACTORY";
  private static final String WAREHOUSE = "WAREHOUSE";
  private static final String WHOLESALE = "WHOLESALE";
  private static final String RETAIL = "RETAIL";
  private static final String DEMAND = "DEMAND";

  public PlayerLevelStringConverter(ResourceBundle resourceBundle) {
    this.resourceBundle = resourceBundle;
  }

  @Override
  public String toString(String key) {
    switch (key) {
      case SUPPLIER:
        return resourceBundle.getString(SUPPLIER_KEY);
      case FACTORY:
        return resourceBundle.getString(FACTORY_KEY);
      case WAREHOUSE:
        return resourceBundle.getString(WAREHOUSE_KEY);
      case WHOLESALE:
        return resourceBundle.getString(WHOLESALE_KEY);
      case RETAIL:
        return resourceBundle.getString(RETAIL_KEY);
      case DEMAND:
        return resourceBundle.getString(DEMAND_KEY);
      default:
        return resourceBundle.getString(UNDEFINED_KEY);
    }
  }

  @Override
  public String fromString(String string) {
    throw new UnsupportedOperationException();
  }

  public String fromBusiness(Business b) {
    String result = "";
    if (b instanceof FactoryBusiness) {
      result = resourceBundle.getString(FACTORY_KEY);
    } else if (b instanceof RegionalWarehouseBusiness) {
      result = resourceBundle.getString(WAREHOUSE_KEY);
    } else if (b instanceof RetailBusiness) {
      result = resourceBundle.getString(RETAIL_KEY);
    } else if (b instanceof WholesaleBusiness) {
      result = resourceBundle.getString(WHOLESALE_KEY);
    }
    return result;
  }
}
