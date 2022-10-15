package nl.ica.asd.agenthandler;

import nl.ica.asd.agenthandler.tokens.IfStatement;
import nl.ica.asd.agenthandler.tokens.Statement;
import nl.ica.asd.agenthandler.tokens.expressions.operations.Comparison;
import nl.ica.asd.agenthandler.tokens.expressions.operations.ComparisonOperatorType;
import nl.ica.asd.util.ObjectTester;
import org.junit.jupiter.api.Test;

class ASTTest {

  @Test
  void runObjectTesterOnAST() {
    Statement firstStatement = new IfStatement(0, 0);
    firstStatement.addChild(new Comparison(ComparisonOperatorType.MORE_THAN, 0, 0));
    Statement similarStatement = new IfStatement(0, 0);
    similarStatement.addChild(new Comparison(ComparisonOperatorType.MORE_THAN, 0, 0));
    Statement differentStatement = new IfStatement(0, 0);
    differentStatement.addChild(new Comparison(ComparisonOperatorType.LESS_THAN, 0, 0));
    new ObjectTester<>(
        new AST(firstStatement),
        new AST(similarStatement),
        new AST(differentStatement)
    ).testEquals().testHashCode().testToString();
  }
}