package nl.ica.asd.storage.gameconfig.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ValidatorTest {

  @Test
  void validate_should_throwException_when_integerWithCustomRuleIsInvalid() {
    final ValidateRule<Integer> rule = DataValidation.from(i -> i > 0, "error");

    Assertions.assertThrows(ValidationRuntimeException.class,
        () -> new Validator<Integer>().rule(0, rule).validate());
  }

  @Test
  void validate_should_pass_when_integerWithCustomRuleIsValid() {
    final ValidateRule<Integer> rule = DataValidation.from(i -> i > 0, "error");
    Assertions.assertDoesNotThrow(() -> new Validator<Integer>().rule(1, rule).validate());
  }

  @Test
  void validate_should_pass_when_rulesAreChainedAndValid() {
    final ValidateRule<String> rule = DataValidation.from(i -> i.contains("t"), "error");
    Assertions.assertDoesNotThrow(
        () -> new Validator<String>().rule("Test", rule).rule("Stijn", rule).validate());
  }

  @Test
  void validate_should_pass_when_rulesOrRuleIsValid() {
    final ValidateRule<Integer> rule = DataValidation.from(i -> i > 1, "error");
    final ValidateRule<Integer> ruleOr = rule.or(DataValidation.from(i -> i < 3, "error"));
    Assertions.assertDoesNotThrow(
        () -> new Validator<Integer>().rule(2, ruleOr).validate());
  }
}