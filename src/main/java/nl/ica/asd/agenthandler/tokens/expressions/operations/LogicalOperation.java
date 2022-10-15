package nl.ica.asd.agenthandler.tokens.expressions.operations;

import java.util.Map;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import nl.ica.asd.agenthandler.exceptions.CheckerException;
import nl.ica.asd.agenthandler.exceptions.TokenException;
import nl.ica.asd.agenthandler.tokens.ASTNode;
import nl.ica.asd.agenthandler.tokens.Expression;
import nl.ica.asd.agenthandler.tokens.expressions.BoolValue;

public class LogicalOperation extends Expression implements BoolValue {

  private BoolValue leftChild;
  private BoolValue rightChild;
  private final LogicalOperatorType logicalOperatorType;

  public LogicalOperation(
      @NotNull LogicalOperatorType logicalOperatorType, int line, int charIndex) {
    super(line, charIndex);
    this.logicalOperatorType = logicalOperatorType;
  }

  @Override
  public void addChild(ASTNode astNode) {
    if (astNode instanceof BoolValue) {
      if (leftChild == null) {
        leftChild = (BoolValue) astNode;
      } else if (rightChild == null) {
        rightChild = (BoolValue) astNode;
      } else {
        throw new TokenException("Logical operation already has two child nodes.");
      }
    } else {
      throw new TokenException(astNode.getClass() + " is not a Bool value.");
    }
  }

  @Override
  public void check() throws CheckerException {
    leftChild.check();
    rightChild.check();
  }

  @Override
  public boolean getBoolValue(Map<String, Integer> variableMap) {
    if (leftChild == null || rightChild == null) {
      throw new TokenException("Children of LogicalOperation can't be null!");
    }

    return logicalOperatorType.getLogicalOperator()
        .applyLogic(leftChild.getBoolValue(variableMap), rightChild.getBoolValue(variableMap));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LogicalOperation that = (LogicalOperation) o;
    return Objects.equals(leftChild, that.leftChild) &&
        Objects.equals(rightChild, that.rightChild) &&
        logicalOperatorType == that.logicalOperatorType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(leftChild, rightChild, logicalOperatorType);
  }

  @Override
  public String toString() {
    return "LogicalOperation{" +
        "leftChild=" + leftChild +
        ", rightChild=" + rightChild +
        ", logicalOperatorType=" + logicalOperatorType +
        "}";
  }
}
