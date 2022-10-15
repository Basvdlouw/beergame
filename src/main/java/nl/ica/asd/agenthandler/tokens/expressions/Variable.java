package nl.ica.asd.agenthandler.tokens.expressions;

import java.util.Map;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import nl.ica.asd.agenthandler.tokens.Expression;

public class Variable extends Expression implements IntValue {

  private final String name;

  public Variable(@NotNull String name, int line, int charIndex) {
    super(line, charIndex);
    this.name = name.toLowerCase();
  }

  @Override
  public int getIntValue(Map<String, Integer> variableMap) {
    return variableMap.getOrDefault(this.name, 0);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Variable variable = (Variable) o;
    return Objects.equals(name, variable.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public String toString() {
    return "Variable{" +
        "name='" + name + '\'' +
        '}';
  }

  @Override
  public void check() {
    //Variable has no children and no check logic
  }
}
