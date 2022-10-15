package nl.ica.asd.agenthandler.tokens.expressions;

import java.util.Map;

public class IntLiteral extends Literal implements IntValue {

  private final int value;

  public IntLiteral(int value, int line, int charIndex) {
    super(line, charIndex);
    this.value = value;
  }

  @Override
  public int getIntValue(Map<String, Integer> variableMap) {
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

    IntLiteral that = (IntLiteral) o;

    return value == that.value;
  }

  @Override
  public int hashCode() {
    return value;
  }

  @Override
  public String toString() {
    return "IntLiteral{" +
        "value=" + value +
        '}';
  }

  @Override
  public void check() {
    //IntLiteral has no children and no check logic
  }
}
