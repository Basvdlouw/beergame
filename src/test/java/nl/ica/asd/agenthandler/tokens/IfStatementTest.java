package nl.ica.asd.agenthandler.tokens;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import nl.ica.asd.agenthandler.exceptions.TokenException;
import nl.ica.asd.agenthandler.tokens.expressions.BoolLiteral;
import nl.ica.asd.agenthandler.tokens.expressions.IntLiteral;
import nl.ica.asd.agenthandler.tokens.expressions.operations.Comparison;
import nl.ica.asd.agenthandler.tokens.expressions.operations.ComparisonOperatorType;
import nl.ica.asd.util.ObjectTester;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IfStatementTest {

  private Map<String, Integer> variableMap;

  @BeforeEach
  void setUp() {
    variableMap = new HashMap<>();
  }

  @Test
  void execute_should_executeStatement_when_conditionIsTrue() {
    IfStatement ifStatement = new IfStatement(0, 0);
    BoolLiteral condition = new BoolLiteral(true, 0, 0);
    TestStatement statement = new TestStatement(0, 0);
    ifStatement.addChild(condition);
    ifStatement.addChild(statement);
    ifStatement.execute(variableMap);
    assertTrue(statement.executedOnce());
  }

  @Test
  void execute_should_notExecuteStatement_when_conditionIsFalse() {
    IfStatement ifStatement = new IfStatement(0, 0);
    BoolLiteral condition = new BoolLiteral(false, 0, 0);
    TestStatement statement = new TestStatement(0, 0);
    ifStatement.addChild(condition);
    ifStatement.addChild(statement);
    ifStatement.execute(variableMap);
    assertTrue(statement.notExecuted());
  }

  @Test
  void execute_should_executeElse_when_conditionIsFalse() {
    IfStatement ifStatement = new IfStatement(0, 0);
    BoolLiteral condition = new BoolLiteral(false, 0, 0);
    TestStatement firstStatement = new TestStatement(0, 0);
    TestStatement secondStatement = new TestStatement(0, 0);
    ElseStatement elseStatement = new ElseStatement(0, 0);
    elseStatement.addChild(secondStatement);
    ifStatement.addChild(condition);
    ifStatement.addChild(firstStatement);
    ifStatement.addChild(elseStatement);
    ifStatement.execute(variableMap);
    assertTrue(firstStatement.notExecuted());
    assertTrue(secondStatement.executedOnce());
  }

  @Test
  void execute_should_notExecuteElse_when_conditionIsTrue() {
    IfStatement ifStatement = new IfStatement(0, 0);
    BoolLiteral condition = new BoolLiteral(true, 0, 0);
    TestStatement firstStatement = new TestStatement(0, 0);
    TestStatement secondStatement = new TestStatement(0, 0);
    ElseStatement elseStatement = new ElseStatement(0, 0);
    elseStatement.addChild(secondStatement);
    ifStatement.addChild(condition);
    ifStatement.addChild(firstStatement);
    ifStatement.addChild(elseStatement);
    ifStatement.execute(variableMap);
    assertTrue(firstStatement.executedOnce());
    assertTrue(secondStatement.notExecuted());
  }

  @Test
  void addChild_should_throwTokenException_whenAddingTwoConditions() {
    IfStatement ifStatement = new IfStatement(0, 0);
    ifStatement.addChild(new BoolLiteral(true, 0, 0));
    assertThrows(TokenException.class, () -> ifStatement.addChild(new BoolLiteral(true, 0, 0)));
  }

  @Test
  void addChild_should_throwTokenException_whenAddingTwoElses() {
    IfStatement ifStatement = new IfStatement(0, 0);
    ifStatement.addChild(new ElseStatement(0, 0));
    assertThrows(TokenException.class, () -> ifStatement.addChild(new ElseStatement(0, 0)));
  }

  @Test
  void addChild_should_throwTokenException_whenAddingWrongNodeType() {
    IfStatement ifStatement = new IfStatement(0, 0);
    assertThrows(TokenException.class, () -> ifStatement.addChild(new IntLiteral(5, 0, 0)));
  }

  @Test
  void runObjectTesterOnIfStatement() {
    IfStatement firstObject = new IfStatement(0, 0);
    firstObject.addChild(new Comparison(ComparisonOperatorType.MORE_THAN, 0, 0));
    IfStatement similarObject = new IfStatement(0, 0);
    similarObject.addChild(new Comparison(ComparisonOperatorType.MORE_THAN, 0, 0));
    IfStatement differentObject = new IfStatement(0, 0);
    differentObject.addChild(new Comparison(ComparisonOperatorType.LESS_THAN, 0, 0));
    new ObjectTester<>(firstObject, similarObject, differentObject)
        .testEquals().testHashCode().testToString();
  }
}