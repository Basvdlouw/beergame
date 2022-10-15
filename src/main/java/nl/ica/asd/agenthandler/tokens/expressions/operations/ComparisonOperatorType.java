package nl.ica.asd.agenthandler.tokens.expressions.operations;

import javax.validation.constraints.NotNull;

public enum ComparisonOperatorType {
  EQUALS((a, b) -> a == b),
  MORE_THAN((a, b) -> a > b),
  LESS_THAN((a, b) -> a < b);

  private final ComparisonOperator comparisonOperator;

  ComparisonOperatorType(@NotNull ComparisonOperator comparisonOperator) {
    this.comparisonOperator = comparisonOperator;
  }

  ComparisonOperator getComparisonOperator() {
    return comparisonOperator;
  }
}
