package nl.ica.asd.agenthandler.tokens;

import java.util.Map;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import nl.ica.asd.agenthandler.BusinessInfo;
import nl.ica.asd.agenthandler.exceptions.CheckerException;
import nl.ica.asd.agenthandler.exceptions.TokenException;
import nl.ica.asd.agenthandler.tokens.expressions.IntValue;

public class VariableAssignment extends Statement {

  private final String name;
  private IntValue intValue;

  public VariableAssignment(@NotNull String name, int line, int charIndex) {
    super(line, charIndex);
    this.name = name.toLowerCase();
  }

  @Override
  public void addChild(ASTNode astNode) {
    if (astNode instanceof IntValue) {
      if (intValue == null) {
        intValue = (IntValue) astNode;
      } else {
        throw new TokenException("VariableAssignment already has an IntValue.");
      }
    } else {
      throw new TokenException(astNode.getClass().getName() + " is not an IntValue.");
    }
  }

  @Override
  public void check() throws CheckerException {
    if (BusinessInfo.contains(name)) {
      throw new CheckerException("Term " + name + " cannot be changed.", this);
    }

    intValue.check();
  }

  @Override
  public void execute(Map<String, Integer> variableMap) {
    variableMap.put(name, intValue.getIntValue(variableMap));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VariableAssignment that = (VariableAssignment) o;
    return Objects.equals(name, that.name) &&
        Objects.equals(intValue, that.intValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, intValue);
  }

  @Override
  public String toString() {
    return "VariableAssignment{" +
        "name='" + name + '\'' +
        ", intValue=" + intValue +
        "} " + super.toString();
  }
}
