package nl.ica.asd.storage.gameconfig.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Validator<T> {

  private final Map<T, List<ValidateRule<T>>> rules;

  public Validator() {
    this.rules = new HashMap<>();
  }

  public Validator<T> rule(T value, ValidateRule<T> rule) {
    if (rules.containsKey(value)) {
      rules.get(value).add(rule);
    } else {
      final List<ValidateRule<T>> temp = new ArrayList<>();
      temp.add(rule);
      rules.put(value, temp);
    }

    return this;
  }

  private void checkRule(T value, ValidateRule<T> rule) {
    rule.test(value).throwIfInvalid();
  }

  public void validate() {
    rules.forEach(((o, validateRules) -> validateRules.forEach(
        rule -> checkRule(o, rule)
    )));
  }
}
