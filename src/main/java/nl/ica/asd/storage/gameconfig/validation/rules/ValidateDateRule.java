package nl.ica.asd.storage.gameconfig.validation.rules;

import java.util.Date;
import java.util.Objects;
import nl.ica.asd.storage.gameconfig.validation.DataValidation;
import nl.ica.asd.storage.gameconfig.validation.ValidateRule;

public class ValidateDateRule {

  private ValidateDateRule() {
  }

  public static final ValidateRule<Date> notNull = DataValidation
      .from(Objects::nonNull, "Date can't be null!");

  public static ValidateRule<Date> moreThan(Date date) {
    return DataValidation.from(s -> s.getTime() >= date.getTime(),
        String.format("must have more than %s chars.", date));
  }

  public static ValidateRule<Date> lessThan(Date date) {
    return DataValidation.from(s -> s.getTime() <= date.getTime(),
        String.format("must have less than %s chars.", date));
  }

  public static ValidateRule<Date> between(Date start, Date end) {
    return moreThan(start).and(lessThan(end));
  }
}
