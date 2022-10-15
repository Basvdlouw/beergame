package nl.ica.asd.agenthandler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import nl.ica.asd.agenthandler.antlr.BeerGameParser.ComparisonContext;
import nl.ica.asd.agenthandler.antlr.BeerGameParser.Math_operationContext;
import nl.ica.asd.agenthandler.exceptions.ScriptErrorException;
import nl.ica.asd.agenthandler.exceptions.TokenException;
import nl.ica.asd.agenthandler.tokens.ElseStatement;
import nl.ica.asd.agenthandler.tokens.IfStatement;
import nl.ica.asd.agenthandler.tokens.StatementContainer;
import nl.ica.asd.agenthandler.tokens.VariableAssignment;
import nl.ica.asd.agenthandler.tokens.expressions.BoolLiteral;
import nl.ica.asd.agenthandler.tokens.expressions.IntLiteral;
import nl.ica.asd.agenthandler.tokens.expressions.Variable;
import nl.ica.asd.agenthandler.tokens.expressions.operations.Comparison;
import nl.ica.asd.agenthandler.tokens.expressions.operations.ComparisonOperatorType;
import nl.ica.asd.agenthandler.tokens.expressions.operations.LogicalOperation;
import nl.ica.asd.agenthandler.tokens.expressions.operations.LogicalOperatorType;
import nl.ica.asd.agenthandler.tokens.expressions.operations.MathOperation;
import nl.ica.asd.agenthandler.tokens.expressions.operations.MathOperatorType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ASTBuilderTest {

  @Mock
  private Math_operationContext mathOperationContext;

  @Mock
  private ComparisonContext comparisonContext;

  private ASTBuilder astBuilder;

  @BeforeEach
  void setUp() {
    astBuilder = new ASTBuilder();
  }

  @Test
  void getAST_should_generateTree_with_ANDOperator() throws ScriptErrorException {
    String code = "if true AND false (order = 10)";
    AST result = new ASTBuilder().getAST(code);

    StatementContainer root = new StatementContainer(0, 0);
    IfStatement ifStatement = new IfStatement(0, 0);
    LogicalOperation logicalOperation = new LogicalOperation(
        LogicalOperatorType.AND, 0, 0);
    logicalOperation.addChild(new BoolLiteral(true, 0, 0));
    logicalOperation.addChild(new BoolLiteral(false, 0, 0));
    ifStatement.addChild(logicalOperation);
    VariableAssignment order = new VariableAssignment("order", 0, 0);
    order.addChild(new IntLiteral(10, 0, 0));
    ifStatement.addChild(order);
    root.addChild(ifStatement);
    AST expected = new AST(root);

    assertEquals(expected, result);
  }

  @Test
  void getAST_should_generateTree_with_bracketLogicalOperator() throws ScriptErrorException {
    String code = "if 1 = 1 or (2 = 2 and 3 = 3)(\n"
        + " order = 4\n"
        + ")";
    AST result = new ASTBuilder().getAST(code);

    StatementContainer root = new StatementContainer(0, 0);
    IfStatement ifStatement = new IfStatement(0, 0);
    LogicalOperation logicalOperation = new LogicalOperation(
        LogicalOperatorType.OR, 0, 0);

    Comparison comparison = new Comparison(ComparisonOperatorType.EQUALS, 0, 0);
    comparison.addChild(new IntLiteral(1, 0, 0));
    comparison.addChild(new IntLiteral(1, 0, 0));
    logicalOperation.addChild(comparison);

    LogicalOperation logicalOperation2 = new LogicalOperation(
        LogicalOperatorType.AND, 0, 0);
    Comparison comparison1 = new Comparison(ComparisonOperatorType.EQUALS, 0, 0);
    comparison1.addChild(new IntLiteral(2, 0, 0));
    comparison1.addChild(new IntLiteral(2, 0, 0));

    Comparison comparison2 = new Comparison(ComparisonOperatorType.EQUALS, 0, 0);
    comparison2.addChild(new IntLiteral(3, 0, 0));
    comparison2.addChild(new IntLiteral(3, 0, 0));

    logicalOperation2.addChild(comparison1);
    logicalOperation2.addChild(comparison2);
    logicalOperation.addChild(logicalOperation2);

    ifStatement.addChild(logicalOperation);
    VariableAssignment order = new VariableAssignment("order", 0, 0);
    order.addChild(new IntLiteral(4, 0, 0));
    ifStatement.addChild(order);
    root.addChild(ifStatement);
    AST expected = new AST(root);

    assertEquals(expected, result);
  }

  @Test
  void getAST_should_makeASTWithCurrentContainer() throws ScriptErrorException {
    String example = "if 5 > 4 ( order = 10 )";
    assertTrue(astBuilder.getAST(example).getRoot() instanceof StatementContainer);
  }

  @Test
  void getAST_should_generateCorrectTree_when_moreThanCondition() throws ScriptErrorException {
    String code = "if 20 > 5 ( order = 10 )";
    AST result = new ASTBuilder().getAST(code);

    StatementContainer root = new StatementContainer(0, 0);
    IfStatement ifStatement = new IfStatement(0, 0);
    Comparison comparison = new Comparison(ComparisonOperatorType.MORE_THAN, 0, 0);
    comparison.addChild(new IntLiteral(20, 0, 0));
    comparison.addChild(new IntLiteral(5, 0, 0));
    ifStatement.addChild(comparison);

    VariableAssignment order = new VariableAssignment("order", 0, 0);
    order.addChild(new IntLiteral(10, 0, 0));
    ifStatement.addChild(order);

    root.addChild(ifStatement);
    AST expected = new AST(root);

    assertEquals(expected, result);
  }

  @Test
  void getAST_should_generateCorrectTree_when_lessThanCondition() throws ScriptErrorException {
    String code = "if 10 < 20 ( order = 10 )";
    AST result = new ASTBuilder().getAST(code);

    StatementContainer root = new StatementContainer(0, 0);
    IfStatement ifStatement = new IfStatement(0, 0);
    Comparison comparison = new Comparison(ComparisonOperatorType.LESS_THAN, 0, 0);
    comparison.addChild(new IntLiteral(10, 0, 0));
    comparison.addChild(new IntLiteral(20, 0, 0));
    ifStatement.addChild(comparison);

    VariableAssignment order = new VariableAssignment("order", 0, 0);
    order.addChild(new IntLiteral(10, 0, 0));
    ifStatement.addChild(order);

    root.addChild(ifStatement);
    AST expected = new AST(root);

    assertEquals(expected, result);
  }

  @Test
  void getAST_should_generateCorrectTree_when_EqualsCondition() throws ScriptErrorException {
    String code = "if 5 = 500 ( order = 10 )";
    AST result = new ASTBuilder().getAST(code);

    StatementContainer root = new StatementContainer(0, 0);
    IfStatement ifStatement = new IfStatement(0, 0);
    Comparison comparison = new Comparison(ComparisonOperatorType.EQUALS, 0, 0);
    comparison.addChild(new IntLiteral(5, 0, 0));
    comparison.addChild(new IntLiteral(500, 0, 0));
    ifStatement.addChild(comparison);

    VariableAssignment order = new VariableAssignment("order", 0, 0);
    order.addChild(new IntLiteral(10, 0, 0));
    ifStatement.addChild(order);

    root.addChild(ifStatement);
    AST expected = new AST(root);

    assertEquals(expected, result);
  }

  @Test
  void getAST_should_generateCorrectTree_when_conditionWithElse() throws ScriptErrorException {
    String code = "if 20 > 5 ( order = 5 ) else ( order = 10 )";
    AST result = new ASTBuilder().getAST(code);

    StatementContainer root = new StatementContainer(0, 0);
    IfStatement ifStatement = new IfStatement(0, 0);
    Comparison comparison = new Comparison(ComparisonOperatorType.MORE_THAN, 0, 0);
    comparison.addChild(new IntLiteral(20, 0, 0));
    comparison.addChild(new IntLiteral(5, 0, 0));
    ifStatement.addChild(comparison);

    VariableAssignment order = new VariableAssignment("order", 0, 0);
    order.addChild(new IntLiteral(5, 0, 0));
    ifStatement.addChild(order);

    ElseStatement elseStatement = new ElseStatement(0, 0);
    VariableAssignment order2 = new VariableAssignment("order", 0, 0);
    order2.addChild(new IntLiteral(10, 0, 0));
    elseStatement.addChild(order2);
    ifStatement.addChild(elseStatement);

    root.addChild(ifStatement);
    AST expected = new AST(root);

    assertEquals(expected, result);
  }

  @Test
  void getAST_should_generateMathOperationAndIntLiteral_when_inCode() throws ScriptErrorException {
    String example = "order = 2 * 4 / 30 + 4 - 13";
    AST ast = astBuilder.getAST(example);

    StatementContainer root = new StatementContainer(0, 0);
    VariableAssignment order = new VariableAssignment("order", 0, 0);
    MathOperation multiplyMathOperation = new MathOperation(MathOperatorType.MULTIPLY, 0, 0);
    multiplyMathOperation.addChild(new IntLiteral(2, 0, 0));
    multiplyMathOperation.addChild(new IntLiteral(4, 0, 0));

    MathOperation divideMathOperation = new MathOperation(MathOperatorType.DIVIDE, 0, 0);
    divideMathOperation.addChild(multiplyMathOperation);
    divideMathOperation.addChild(new IntLiteral(30, 0, 0));

    MathOperation plusMathOperation = new MathOperation(MathOperatorType.PLUS, 0, 0);
    plusMathOperation.addChild(divideMathOperation);
    plusMathOperation.addChild(new IntLiteral(4, 0, 0));

    MathOperation minusMathOperation = new MathOperation(MathOperatorType.MINUS, 0, 0);
    minusMathOperation.addChild(plusMathOperation);
    minusMathOperation.addChild(new IntLiteral(13, 0, 0));

    order.addChild(minusMathOperation);
    root.addChild(order);

    AST expected = new AST(root);
    assertEquals(expected, ast);
  }

  @Test
  void getAST_should_generateVariableOperationWithPower_when_inCode() throws ScriptErrorException {
    String example = "order = 2 order ^ 3";
    AST ast = astBuilder.getAST(example);

    StatementContainer root = new StatementContainer(0, 0);
    VariableAssignment order1 = new VariableAssignment("order", 0, 0);
    order1.addChild(new IntLiteral(2, 0, 0));
    root.addChild(order1);

    VariableAssignment order2 = new VariableAssignment("order", 0, 0);
    MathOperation powerMathOperation = new MathOperation(MathOperatorType.POWER, 0, 0);
    powerMathOperation.addChild(new Variable("order", 0, 0));
    powerMathOperation.addChild(new IntLiteral(3, 0, 0));
    order2.addChild(powerMathOperation);
    root.addChild(order2);

    AST expected = new AST(root);
    assertEquals(expected, ast);

    Map<String, Integer> variableMap;
    variableMap = new HashMap<>();

    AgentRunner agentrunner = new AgentRunner(expected);
    assertEquals(8, agentrunner.playTurn(variableMap));
  }

  @Test
  void getAST_should_causeSyntaxScriptErrors_when_invalidToken() {
    ScriptErrorException exception = assertThrows(ScriptErrorException.class,
        () -> astBuilder.getAST("order !"));
    ScriptError[] errors = exception.getScriptErrors();
    assertTrue(errors.length > 0, "Invalid token should cause at least one error!");
    assertEquals(ScriptErrorType.SYNTAX_ERROR, errors[0].getErrorType());
  }

  @Test
  void enterMathOperation_should_throwTokenException_when_allPossibleValuesAreNull() {
    when(mathOperationContext.int_literal()).thenReturn(null);
    when(mathOperationContext.PLUS()).thenReturn(null);
    when(mathOperationContext.MINUS()).thenReturn(null);
    when(mathOperationContext.MULTIPLY()).thenReturn(null);
    when(mathOperationContext.DIVIDE()).thenReturn(null);
    assertThrows(TokenException.class, () -> astBuilder.enterMath_operation(mathOperationContext),
        "Invalid mathOperatorType.");
  }

  @Test
  void enterComparison_should_throwTokenException_when_allPossibleValuesAreNull() {
    when(comparisonContext.EQUALS()).thenReturn(null);
    when(comparisonContext.MORE_THAN()).thenReturn(null);
    when(comparisonContext.LESS_THAN()).thenReturn(null);
    assertThrows(TokenException.class, () -> astBuilder.enterComparison(comparisonContext),
        "Invalid mathOperatorType.");
  }

  @Test
  void getAST_should_generateCorrectTree_when_variableAssignmentWithPlus()
      throws ScriptErrorException {
    runAssignmentTest("+", MathOperatorType.PLUS);
  }

  @Test
  void getAST_should_generateCorrectTree_when_variableAssignmentWithMinus()
      throws ScriptErrorException {
    runAssignmentTest("-", MathOperatorType.MINUS);
  }

  @Test
  void getAST_should_generateCorrectTree_when_variableAssignmentWithMultiply()
      throws ScriptErrorException {
    runAssignmentTest("*", MathOperatorType.MULTIPLY);
  }

  @Test
  void getAST_should_generateCorrectTree_when_variableAssignmentWithDivide()
      throws ScriptErrorException {
    runAssignmentTest("/", MathOperatorType.DIVIDE);
  }

  private void runAssignmentTest(String operatorToken, MathOperatorType expectedOperator)
      throws ScriptErrorException {
    String code = "someVariable " + operatorToken + " 5";
    AST result = new ASTBuilder().getAST(code);

    StatementContainer root = new StatementContainer(0, 0);
    VariableAssignment assignment = new VariableAssignment("someVariable", 0, 0);
    MathOperation mathOperation = new MathOperation(expectedOperator, 0, 0);
    mathOperation.addChild(new Variable("someVariable", 0, 0));
    mathOperation.addChild(new IntLiteral(5, 0, 0));
    assignment.addChild(mathOperation);
    root.addChild(assignment);

    AST expected = new AST(root);
    assertEquals(expected, result);
  }
}