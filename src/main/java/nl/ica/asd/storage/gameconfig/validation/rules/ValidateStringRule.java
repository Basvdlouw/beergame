package nl.ica.asd.storage.gameconfig.validation.rules;

import java.util.Objects;
import nl.ica.asd.storage.gameconfig.validation.DataValidation;
import nl.ica.asd.storage.gameconfig.validation.ValidateRule;

public class ValidateStringRule {

  private ValidateStringRule() {
  }

  public static final ValidateRule<String> notNull = DataValidation
      .from(Objects::nonNull, "Name can't be null!");

  public static ValidateRule<String> matches(String pattern) {
    return DataValidation
        .from(s -> s.matches(pattern), "Name must match pattern");
  }

  public static ValidateRule<String> contains(String c) {
    return DataValidation.from(s -> s.contains(c), String.format("Name must contain %s", c));
  }
}
