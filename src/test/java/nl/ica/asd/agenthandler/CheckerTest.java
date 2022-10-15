package nl.ica.asd.agenthandler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import nl.ica.asd.agenthandler.exceptions.CheckerException;
import nl.ica.asd.agenthandler.exceptions.ScriptErrorException;
import nl.ica.asd.agenthandler.tokens.IfStatement;
import nl.ica.asd.agenthandler.tokens.StatementContainer;
import nl.ica.asd.agenthandler.tokens.VariableAssignment;
import nl.ica.asd.agenthandler.tokens.expressions.IntLiteral;
import nl.ica.asd.agenthandler.tokens.expressions.Variable;
import nl.ica.asd.agenthandler.tokens.expressions.operations.Comparison;
import nl.ica.asd.agenthandler.tokens.expressions.operations.ComparisonOperatorType;
import org.junit.jupiter.api.Test;

class CheckerTest {

  private ASTBuilder astBuilder = new ASTBuilder();
  private Checker checker = new Checker();

  @Test
  void checkAST_should_throwNullPointerException_when_astIsNull() {
    assertThrows(NullPointerException.class, () -> checker.checkAST(null));
  }

  @Test
  void checkAST_should_throwCheckerException_when_variableAssignmentOnStock() {
    AST ast = createTestAST(new VariableAssignment("stock", 0, 0));

    assertThrows(CheckerException.class, () -> checker.checkAST(ast));
  }

  @Test
  void checkAST_should_notThrowCheckerException_when_variableAssignmentOnOrder() {
    AST ast = createTestAST(new VariableAssignment("order", 0, 0));

    try {
      checker.checkAST(ast);
    } catch (CheckerException e) {
      fail("CheckerException was thrown", e);
    }
  }

  @Test
  void checkAST_should_throwExceptionWithRightLineAndCharIndex() {
    try {
      AST ast = astBuilder.getAST("if order = 10 ( \n"
          + "budget = 999999\n"
          + ")");
      checker.checkAST(ast);
    } catch (CheckerException e) {
      assertEquals(2, e.getAstNode().getLine());
      assertEquals(0, e.getAstNode().getCharIndex());
    } catch (ScriptErrorException e) {
      fail();
    }
  }

  private AST createTestAST(VariableAssignment variableAssignment) {
    StatementContainer statementContainer = new StatementContainer(0, 0);
    IfStatement ifStatement = new IfStatement(0, 0);
    Comparison comparison = new Comparison(ComparisonOperatorType.EQUALS, 0, 0);
    Variable variable = new Variable("stock", 0, 0);
    IntLiteral comparisonIntLiteral = new IntLiteral(10, 0, 0);
    IntLiteral assignmentIntLiteral = new IntLiteral(10, 0, 0);

    statementContainer.addChild(ifStatement);
    ifStatement.addChild(comparison);
    comparison.addChild(variable);
    comparison.addChild(comparisonIntLiteral);
    ifStatement.addChild(variableAssignment);
    variableAssignment.addChild(assignmentIntLiteral);
    return new AST(statementContainer);
  }
}
