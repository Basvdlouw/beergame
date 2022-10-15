package nl.ica.asd.frontend.gui.converters;

import java.util.ResourceBundle;
import javafx.util.StringConverter;
import nl.ica.asd.frontend.gui.playturn.steplevel.StepLevel;

public class StepLevelStringConverter extends StringConverter<StepLevel> {

  private final ResourceBundle resourceBundle;

  private static final String UNDEFINED_KEY = "steplevel.undefined";
  private static final String INCOMING_GOODS_KEY = "steplevel.incoming_goods";
  private static final String STOCK_KEY = "steplevel.stock";
  private static final String OPEN_ORDERS_KEY = "steplevel.open_orders";
  private static final String BUDGET_KEY = "steplevel.budget";
  private static final String OUTGOING_GOODS_KEY = "steplevel.outgoing_goods";
  private static final String INCOMING_ORDERS_KEY = "steplevel.incoming_orders";


  public StepLevelStringConverter(ResourceBundle resourceBundle) {
    this.resourceBundle = resourceBundle;
  }

  @Override
  public String toString(StepLevel object) {
    if (object != null) {
      switch (object) {
        case INCOMINGGOODS:
          return resourceBundle.getString(INCOMING_GOODS_KEY);
        case STOCK:
          return resourceBundle.getString(STOCK_KEY);
        case OPENORDERS:
          return resourceBundle.getString(OPEN_ORDERS_KEY);
        case BUDGET:
          return resourceBundle.getString(BUDGET_KEY);
        case OUTGOINGGOODS:
          return resourceBundle.getString(OUTGOING_GOODS_KEY);
        case INCOMINGORDERS:
          return resourceBundle.getString(INCOMING_ORDERS_KEY);
      }
    }
    return resourceBundle.getString(UNDEFINED_KEY);
  }

  @Override
  public StepLevel fromString(String string) {
    if (string.equals(resourceBundle.getString(INCOMING_GOODS_KEY))) {
      return StepLevel.INCOMINGGOODS;
    } else if (string.equals(resourceBundle.getString(STOCK_KEY))) {
      return StepLevel.STOCK;
    } else if (string.equals(resourceBundle.getString(OPEN_ORDERS_KEY))) {
      return StepLevel.OPENORDERS;
    } else if (string.equals(resourceBundle.getString(BUDGET_KEY))) {
      return StepLevel.BUDGET;
    } else if (string.equals(resourceBundle.getString(OUTGOING_GOODS_KEY))) {
      return StepLevel.OUTGOINGGOODS;
    } else if (string.equals(resourceBundle.getString(INCOMING_ORDERS_KEY))) {
      return StepLevel.INCOMINGORDERS;
    }

    throw new IllegalArgumentException();
  }
}