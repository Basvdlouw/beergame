package nl.ica.asd.storage.gameconfig.validation.rules;

import java.util.Objects;
import nl.ica.asd.storage.gameconfig.validation.DataValidation;
import nl.ica.asd.storage.gameconfig.validation.ValidateRule;

public class ValidateIntRule {

  private ValidateIntRule() {
  }

  public static final ValidateRule<Integer> notNull = DataValidation
      .from(Objects::nonNull, "Number can't be null!");

  public static ValidateRule<Integer> lowerThan(int max) {
    return DataValidation
        .from(integer -> integer < max, String.format("must be lower than %s.", max));
  }

  public static ValidateRule<Integer> greaterThan(int min) {
    return DataValidation
        .from(integer -> integer > min, String.format("must be greater than %s.", min));
  }

  public static ValidateRule<Integer> intBetween(int min, int max) {
    return greaterThan(min).and(lowerThan(max));
  }
}
