package nl.ica.asd.agenthandler.tokens;

import java.util.Map;
import java.util.Objects;
import nl.ica.asd.agenthandler.exceptions.TokenException;
import nl.ica.asd.agenthandler.tokens.expressions.BoolValue;

public class IfStatement extends StatementContainer {

  private BoolValue condition;
  private ElseStatement elseStatement;

  public IfStatement(int line, int charIndex) {
    super(line, charIndex);
  }

  @Override
  public void addChild(ASTNode astNode) {
    if (astNode instanceof BoolValue) {
      if (condition == null) {
        condition = (BoolValue) astNode;
      } else {
        throw new TokenException("If-statement already has a condition.");
      }
    } else if (astNode instanceof ElseStatement) {
      if (elseStatement == null) {
        elseStatement = (ElseStatement) astNode;
      } else {
        throw new TokenException("If-statement already has an else-statement.");
      }
    } else {
      try {
        super.addChild(astNode);
      } catch (TokenException e) {
        throw new TokenException(astNode.getClass() + " is not a condition, else or statement.", e);
      }
    }
  }

  @Override
  public void execute(Map<String, Integer> variableMap) {
    if (condition.getBoolValue(variableMap)) {
      super.execute(variableMap);
    } else if (elseStatement != null) {
      elseStatement.execute(variableMap);
    }
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

    IfStatement that = (IfStatement) o;

    if (!Objects.equals(condition, that.condition)) {
      return false;
    }
    return Objects.equals(elseStatement, that.elseStatement);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (condition != null ? condition.hashCode() : 0);
    result = 31 * result + (elseStatement != null ? elseStatement.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "IfStatement{" +
        "condition=" + condition +
        ", elseStatement=" + elseStatement +
        "} " + super.toString();
  }
}
