package nl.ica.asd.frontend.gui.converters;

import java.util.ResourceBundle;
import javafx.util.StringConverter;
import nl.ica.asd.frontend.gui.evaluate.linear.business.BusinessLevel;

public class BusinessLevelStringConverter extends StringConverter<BusinessLevel> {

  private final ResourceBundle resourceBundle;

  private static final String UNDEFINED_KEY = "steplevel.undefined";
  private static final String SUPPLIER_KEY = "playturn.supplier";
  private static final String FACTORY_KEY = "playturn.factory";
  private static final String WAREHOUSE_KEY = "playturn.regional_warehouse";
  private static final String WHOLESALE_KEY = "playturn.wholesale";
  private static final String RETAIL_KEY = "playturn.retail";
  private static final String DEMAND_KEY = "playturn.demand";

  public BusinessLevelStringConverter(ResourceBundle resourceBundle) {
    this.resourceBundle = resourceBundle;
  }

  @Override
  public String toString(BusinessLevel businessLevel) {
    if (businessLevel != null) {
      switch (businessLevel) {
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
      }
    }
    return resourceBundle.getString(UNDEFINED_KEY);
  }

  @Override
  public BusinessLevel fromString(String string) {
    throw new UnsupportedOperationException();
  }
}
