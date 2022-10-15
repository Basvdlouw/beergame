package nl.ica.asd.frontend.gui.converters;

import java.util.ResourceBundle;
import javafx.util.StringConverter;
import nl.ica.asd.logic.domain.SupplyChainType;

public class SupplyChainTypeStringConverter extends StringConverter<SupplyChainType> {

  private final ResourceBundle resourceBundle;

  private static final String UNDEFINED_KEY = "domain.supplychaintype.undefined";
  private static final String LINEAR_KEY = "domain.supplychaintype.linear";
  private static final String NETWORK_KEY = "domain.supplychaintype.network";
  private static final String PYRAMID_KEY = "domain.supplychaintype.pyramid";


  public SupplyChainTypeStringConverter(ResourceBundle resourceBundle) {
    this.resourceBundle = resourceBundle;
  }

  @Override
  public String toString(SupplyChainType object) {
    if (object != null) {
      switch (object) {
        case LINEAR:
          return resourceBundle.getString(LINEAR_KEY);
        case NETWORK:
          return resourceBundle.getString(NETWORK_KEY);
        case PYRAMID:
          return resourceBundle.getString(PYRAMID_KEY);
      }
    }

    return resourceBundle.getString(UNDEFINED_KEY);
  }

  @Override
  public SupplyChainType fromString(String string) {
    if (string.equals(resourceBundle.getString(LINEAR_KEY))) {
      return SupplyChainType.LINEAR;
    } else if (string.equals(resourceBundle.getString(NETWORK_KEY))) {
      return SupplyChainType.NETWORK;
    } else if (string.equals(resourceBundle.getString(PYRAMID_KEY))) {
      return SupplyChainType.PYRAMID;
    }

    throw new IllegalArgumentException();
  }
}
