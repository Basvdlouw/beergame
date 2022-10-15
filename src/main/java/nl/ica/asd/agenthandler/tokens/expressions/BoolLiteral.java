package nl.ica.asd.agenthandler.tokens.expressions;

import java.util.Map;
import java.util.Objects;

public class BoolLiteral extends Literal implements BoolValue {

  private final boolean value;

  public BoolLiteral(boolean value, int line, int charIndex) {
    super(line, charIndex);
    this.value = value;
  }

  @Override
  public boolean getBoolValue(Map<String, Integer> variableMap) {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BoolLiteral that = (BoolLiteral) o;
    return value == that.value;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return "BoolLiteral{" +
        "value=" + value +
        "}";
  }

  @Override
  public void check() {
    //BoolLiteral has no children and no check logic
  }
}
