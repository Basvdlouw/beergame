package nl.ica.asd.agenthandler.tokens.expressions.operations;

import nl.ica.asd.agenthandler.exceptions.CheckerException;
import nl.ica.asd.agenthandler.exceptions.TokenException;
import nl.ica.asd.agenthandler.tokens.ASTNode;
import nl.ica.asd.agenthandler.tokens.Expression;
import nl.ica.asd.agenthandler.tokens.expressions.IntValue;

abstract class IntOperation extends Expression {

  private IntValue valueA;
  private IntValue valueB;

  IntOperation(int line, int charIndex) {
    super(line, charIndex);
  }

  @Override
  public void addChild(ASTNode astNode) {
    if (astNode instanceof IntValue) {
      if (valueA == null) {
        valueA = (IntValue) astNode;
      } else if (valueB == null) {
        valueB = (IntValue) astNode;
      } else {
        throw new TokenException("Int operation already has two value nodes.");
      }
    } else {
      throw new TokenException(astNode.getClass() + " is not an int value.");
    }
  }

  IntValue getValueA() {
    return valueA;
  }

  IntValue getValueB() {
    return valueB;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    IntOperation that = (IntOperation) o;

    if (getValueA() != null ? !getValueA().equals(that.getValueA()) : that.getValueA() != null) {
      return false;
    }
    return getValueB() != null ? getValueB().equals(that.getValueB()) : that.getValueB() == null;
  }

  @Override
  public int hashCode() {
    int result = getValueA() != null ? getValueA().hashCode() : 0;
    result = 31 * result + (getValueB() != null ? getValueB().hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "IntOperation{" +
        "valueA=" + valueA +
        ", valueB=" + valueB +
        '}';
  }

  @Override
  public void check() throws CheckerException {
    valueA.check();
    valueB.check();
  }
}
