package nl.ica.asd.storage.gameconfig.validation.rules;

import nl.ica.asd.storage.gameconfig.validation.ValidationRuntimeException;
import nl.ica.asd.storage.gameconfig.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ValidateIntRuleTest {

  @Test
  void validate_should_throwException_when_integerIsNullWithRule() {
    Assertions.assertThrows(ValidationRuntimeException.class,
        () -> new Validator<Integer>().rule(null, ValidateIntRule.notNull).validate());
  }

  @Test
  void validate_should_pass_when_integerIsNotNullWithRule() {
    Assertions.assertDoesNotThrow(
        () -> new Validator<Integer>().rule(1, ValidateIntRule.notNull).validate());
  }

  @Test
  void validate_should_throwException_when_integerIsNotGreaterThanWithRule() {
    Assertions.assertThrows(ValidationRuntimeException.class,
        () -> new Validator<Integer>().rule(0, ValidateIntRule.greaterThan(0)).validate());
  }

  @Test
  void validate_should_pass_when_integerIsGreaterThanWithRule() {
    Assertions.assertDoesNotThrow(
        () -> new Validator<Integer>().rule(1, ValidateIntRule.greaterThan(0)).validate());
  }

  @Test
  void validate_should_throwException_when_integerIsNotSmallerThanWithRule() {
    Assertions.assertThrows(ValidationRuntimeException.class,
        () -> new Validator<Integer>().rule(1, ValidateIntRule.lowerThan(0)).validate());
  }

  @Test
  void validate_should_pass_when_integerIsSmallerThanWithRule() {
    Assertions.assertDoesNotThrow(
        () -> new Validator<Integer>().rule(0, ValidateIntRule.lowerThan(1)).validate());
  }


  @Test
  void validate_should_throwException_when_integerIsNotInBetweenWithRule() {
    Assertions.assertThrows(ValidationRuntimeException.class,
        () -> new Validator<Integer>().rule(1, ValidateIntRule.intBetween(2, 3)).validate());
  }

  @Test
  void validate_should_pass_when_integerIsInBetweenWithRule() {
    Assertions.assertDoesNotThrow(
        () -> new Validator<Integer>().rule(1, ValidateIntRule.intBetween(0, 2)).validate());
  }
}
