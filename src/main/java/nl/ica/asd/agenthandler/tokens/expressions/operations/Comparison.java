package nl.ica.asd.agenthandler.tokens.expressions.operations;

import java.util.Map;
import javax.validation.constraints.NotNull;
import nl.ica.asd.agenthandler.tokens.expressions.BoolValue;

public class Comparison extends IntOperation implements BoolValue {

  private final ComparisonOperatorType comparisonOperatorType;

  public Comparison(@NotNull ComparisonOperatorType comparisonOperatorType, int line,
      int charIndex) {
    super(line, charIndex);
    this.comparisonOperatorType = comparisonOperatorType;
  }

  @Override
  public boolean getBoolValue(Map<String, Integer> variableMap) {
    return comparisonOperatorType.getComparisonOperator()
        .compare(getValueA().getIntValue(variableMap), getValueB().getIntValue(variableMap));
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

    Comparison that = (Comparison) o;

    return comparisonOperatorType == that.comparisonOperatorType;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + comparisonOperatorType.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "Comparison{" +
        "comparisonOperatorType=" + comparisonOperatorType +
        "} " + super.toString();
  }
}
