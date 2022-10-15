package nl.ica.asd.storage.gameconfig.validation;

import java.util.function.Predicate;

public class DataValidation<T> implements ValidateRule<T> {

  private final Predicate<T> predicate;
  private final String onErrorMessage;

  public DataValidation(Predicate<T> predicate, String onErrorMessage) {
    this.predicate = predicate;
    this.onErrorMessage = onErrorMessage;
  }

  public static <T> DataValidation<T> from(Predicate<T> predicate, String onErrorMessage) {
    return new DataValidation<>(predicate, onErrorMessage);
  }

  @Override
  public ValidateRuleResult test(T rule) {
    return predicate.test(rule) ? ValidateRuleResult.ok()
        : ValidateRuleResult.fail(this.onErrorMessage);
  }
}
