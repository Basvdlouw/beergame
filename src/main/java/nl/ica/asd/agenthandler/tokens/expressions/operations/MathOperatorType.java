package nl.ica.asd.agenthandler.tokens.expressions.operations;

import javax.validation.constraints.NotNull;

public enum MathOperatorType {
  PLUS((a, b) -> a + b),
  MINUS((a, b) -> a - b),
  MULTIPLY((a, b) -> a * b),
  DIVIDE((a, b) -> a / b),
  POWER((a, b) -> (int) Math.pow(a, b)),
  PERCENTAGE((a, b) -> b * a / 100);

  private final MathOperator mathOperator;

  MathOperatorType(@NotNull MathOperator mathOperator) {
    this.mathOperator = mathOperator;
  }

  public MathOperator getMathOperator() {
    return mathOperator;
  }
}
