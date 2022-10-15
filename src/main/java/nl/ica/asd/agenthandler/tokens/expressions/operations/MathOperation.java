package nl.ica.asd.agenthandler.tokens.expressions.operations;

import java.util.Map;
import javax.validation.constraints.NotNull;
import nl.ica.asd.agenthandler.tokens.expressions.IntValue;

public class MathOperation extends IntOperation implements IntValue {

  private final MathOperatorType mathOperatorType;

  public MathOperation(@NotNull MathOperatorType operator, int line, int charIndex) {
    super(line, charIndex);
    mathOperatorType = operator;
  }

  @Override
  public int getIntValue(Map<String, Integer> variableMap) {
    return mathOperatorType.getMathOperator()
        .calculate(getValueA().getIntValue(variableMap), getValueB().getIntValue(variableMap));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }

    MathOperation that = (MathOperation) o;

    return mathOperatorType == that.mathOperatorType;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + mathOperatorType.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "MathOperation{" +
        "mathOperatorType=" + mathOperatorType +
        "} " + super.toString();
  }
}
