package nl.ica.asd.storage.gameconfig.validation.rules;

import java.util.Date;
import nl.ica.asd.storage.gameconfig.validation.ValidationRuntimeException;
import nl.ica.asd.storage.gameconfig.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ValidateDateRuleTest {

  private final Date today = new Date();
  private final Date tomorrow = new Date(today.getTime() + (1000 * 60 * 60 * 24));

  @Test
  void validate_should_throwException_when_dateIsNotGreaterThanOtherDateWithRule() {
    Assertions.assertThrows(ValidationRuntimeException.class,
        () -> new Validator<Date>().rule(today, ValidateDateRule.moreThan(tomorrow)).validate());
  }

  @Test
  void validate_should_pass_when_dateIsGreaterThanOtherDateWithRule() {
    Assertions.assertDoesNotThrow(
        () -> new Validator<Date>().rule(tomorrow, ValidateDateRule.moreThan(today)).validate());
  }

  @Test
  void validate_should_throwException_when_dateIsNotSmallerThanOtherDateWithRule() {
    Assertions.assertThrows(ValidationRuntimeException.class,
        () -> new Validator<Date>().rule(tomorrow, ValidateDateRule.lessThan(today)).validate());
  }

  @Test
  void validate_should_pass_when_dateIsSmallerThanOtherDateWithRule() {
    Assertions.assertDoesNotThrow(
        () -> new Validator<Date>().rule(today, ValidateDateRule.lessThan(tomorrow)).validate());
  }


  @Test
  void validate_should_throwException_when_dateIsNotInBetweenOtherDatesWithRule() {
    final Date afterTomorrow = new Date(tomorrow.getTime() + (1000 * 60 * 60 * 24));
    Assertions.assertThrows(ValidationRuntimeException.class,
        () -> new Validator<Date>().rule(today, ValidateDateRule.between(tomorrow, afterTomorrow))
            .validate());
  }

  @Test
  void validate_should_pass_when_dateIsInBetweenOtherDatesWithRule() {
    final Date afterTomorrow = new Date(tomorrow.getTime() + (1000 * 60 * 60 * 24));
    Assertions.assertDoesNotThrow(
        () -> new Validator<Date>().rule(tomorrow, ValidateDateRule.between(today, afterTomorrow))
            .validate());
  }
}
