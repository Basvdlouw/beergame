package nl.ica.asd.storage.gameconfig.validation.rules;

import nl.ica.asd.storage.gameconfig.validation.ValidationRuntimeException;
import nl.ica.asd.storage.gameconfig.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ValidateStringRuleTest {

  @Test
  void validate_should_throwException_when_stringIsNullWithRule() {
    Assertions.assertThrows(ValidationRuntimeException.class,
        () -> new Validator<String>().rule(null, ValidateStringRule.notNull).validate());
  }

  @Test
  void validate_should_pass_when_stringIsNotNullWithRule() {
    Assertions.assertDoesNotThrow(
        () -> new Validator<String>().rule("not null", ValidateStringRule.notNull).validate());
  }

  @Test
  void validate_should_throwException_when_stringNotMatchesWithRule() {
    Assertions.assertThrows(ValidationRuntimeException.class,
        () -> new Validator<String>().rule("*&*#", ValidateStringRule.matches("^[-a-zA-Z0-9._]+"))
            .validate());
  }

  @Test
  void validate_should_pass_when_stringMatchesWithRule() {
    Assertions.assertDoesNotThrow(
        () -> new Validator<String>()
            .rule("BeerGame", ValidateStringRule.matches("^[-a-zA-Z0-9._]+")).validate());
  }

  @Test
  void validate_should_throwException_when_stringNotContainsWithRule() {
    Assertions.assertThrows(ValidationRuntimeException.class,
        () -> new Validator<String>().rule("stin", ValidateStringRule.contains("stijn"))
            .validate());
  }

  @Test
  void validate_should_pass_when_stringContainsWithRule() {
    Assertions.assertDoesNotThrow(
        () -> new Validator<String>().rule("Stijn", ValidateStringRule.matches("Stijn"))
            .validate());
  }
}
