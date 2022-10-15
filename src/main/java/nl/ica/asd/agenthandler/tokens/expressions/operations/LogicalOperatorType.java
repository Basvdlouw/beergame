package nl.ica.asd.agenthandler.tokens.expressions.operations;

import javax.validation.constraints.NotNull;

public enum LogicalOperatorType {
  AND((leftChild, rightchild) -> leftChild && rightchild),
  OR((leftChild, rightchild) -> leftChild || rightchild);

  private final LogicalOperator logicalOperator;

  LogicalOperatorType(@NotNull LogicalOperator logicalOperator) {
    this.logicalOperator = logicalOperator;
  }

  public LogicalOperator getLogicalOperator() {
    return logicalOperator;
  }
}

