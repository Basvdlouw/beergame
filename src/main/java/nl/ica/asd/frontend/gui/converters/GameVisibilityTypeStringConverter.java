package nl.ica.asd.frontend.gui.converters;

import java.util.ResourceBundle;
import javafx.util.StringConverter;
import nl.ica.asd.logic.domain.GameVisibilityType;

public class GameVisibilityTypeStringConverter extends StringConverter<GameVisibilityType> {

  private final ResourceBundle resourceBundle;

  private static final String UNDEFINED_KEY = "domain.gamevisibilitytype.undefined";
  private static final String VISIBLE_KEY = "domain.gamevisibilitytype.visible";
  private static final String INVISIBLE_KEY = "domain.gamevisibilitytype.invisible";


  public GameVisibilityTypeStringConverter(ResourceBundle resourceBundle) {
    this.resourceBundle = resourceBundle;
  }

  @Override
  public String toString(GameVisibilityType object) {
    if (object != null) {
      if (object == GameVisibilityType.VISIBLE) {
        return resourceBundle.getString(VISIBLE_KEY);
      } else if (object == GameVisibilityType.INVISIBLE) {
        return resourceBundle.getString(INVISIBLE_KEY);
      }
    }

    return resourceBundle.getString(UNDEFINED_KEY);
  }

  @Override
  public GameVisibilityType fromString(String string) {
    if (string.equals(resourceBundle.getString(VISIBLE_KEY))) {
      return GameVisibilityType.VISIBLE;
    } else if (string.equals(resourceBundle.getString(INVISIBLE_KEY))) {
      return GameVisibilityType.INVISIBLE;
    }

    throw new IllegalArgumentException();
  }
}
