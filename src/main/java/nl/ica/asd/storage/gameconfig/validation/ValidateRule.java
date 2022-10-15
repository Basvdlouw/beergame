package nl.ica.asd.storage.gameconfig.validation;

public interface ValidateRule<T> {

  ValidateRuleResult test(T rule);

  default ValidateRule<T> and(ValidateRule<T> other) {
    return param -> {
      ValidateRuleResult firstResult = this.test(param);
      return !firstResult.isvalid() ? firstResult : other.test(param);
    };
  }

  default ValidateRule<T> or(ValidateRule<T> other) {
    return param -> {
      ValidateRuleResult firstResult = this.test(param);
      return firstResult.isvalid() ? firstResult : other.test(param);
    };
  }
}
