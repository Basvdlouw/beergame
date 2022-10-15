package nl.ica.asd.agenthandler.tokens;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;
import nl.ica.asd.agenthandler.exceptions.TokenException;
import nl.ica.asd.agenthandler.tokens.expressions.BoolLiteral;
import nl.ica.asd.agenthandler.tokens.expressions.IntLiteral;
import nl.ica.asd.agenthandler.tokens.expressions.Variable;
import nl.ica.asd.agenthandler.tokens.expressions.operations.MathOperation;
import nl.ica.asd.agenthandler.tokens.expressions.operations.MathOperatorType;
import nl.ica.asd.util.ObjectTester;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VariableAssignmentTest {

  private Map<String, Integer> variableMap;

  @BeforeEach
  private void setUp() {
    variableMap = new HashMap<>();
    variableMap.put("valuea", 10);
    variableMap.put("valueb", 20);
  }

  @Test
  void execute_should_putValueInMap() {
    VariableAssignment assignment = new VariableAssignment("valuec", 0, 0);
    assignment.addChild(new IntLiteral(5, 0, 0));
    assignment.execute(variableMap);
    assertEquals(5, variableMap.get("valuec").intValue());
  }

  @Test
  void execute_should_putValueInMapWithPowerOperation() {
    VariableAssignment assignment = createVariableAssignment(MathOperatorType.POWER);

    assignment.execute(variableMap);

    assertEquals(100, variableMap.get("valuea").intValue());
  }

  @Test
  void execute_should_putValueInMapWithMultiplyOperation() {
    VariableAssignment assignment = createVariableAssignment(MathOperatorType.MULTIPLY);

    assignment.execute(variableMap);

    assertEquals(20, variableMap.get("valuea").intValue());
  }

  @Test
  void execute_should_putValueInMapWithDivideOperation() {
    VariableAssignment assignment = createVariableAssignment(MathOperatorType.DIVIDE);

    assignment.execute(variableMap);

    assertEquals(5, variableMap.get("valuea").intValue());
  }

  @Test
  void execute_should_putValueInMapWithplusOperation() {
    VariableAssignment assignment = createVariableAssignment(MathOperatorType.PLUS);

    assignment.execute(variableMap);

    assertEquals(12, variableMap.get("valuea").intValue());
  }

  @Test
  void execute_should_putValueInMapWithMinusOperation() {
    VariableAssignment assignment = createVariableAssignment(MathOperatorType.MINUS);

    assignment.execute(variableMap);

    assertEquals(8, variableMap.get("valuea").intValue());
  }

  @Test
  void execute_should_putValueInMapWithPercentageOperation() {
    VariableAssignment assignment = createVariableAssignment(MathOperatorType.PERCENTAGE);

    assignment.execute(variableMap);

    assertEquals(0, variableMap.get("valuea").intValue());
  }

  private VariableAssignment createVariableAssignment(MathOperatorType mathOperatorType) {
    VariableAssignment assignment = new VariableAssignment("valuea", 0, 0);

    MathOperation mathOperation = new MathOperation(mathOperatorType, 0, 0);
    mathOperation.addChild(new Variable("valuea", 0, 0));
    mathOperation.addChild(new IntLiteral(2, 0, 0));

    assignment.addChild(mathOperation);
    return assignment;
  }

  @Test
  void addChild_should_throwTokenException_when_childIsAddedTwice() {
    VariableAssignment assignment;
    assignment = new VariableAssignment("someValue", 0, 0);
    assignment.addChild(new IntLiteral(5, 0, 0));
    assertThrows(TokenException.class, () -> assignment.addChild(new IntLiteral(6, 0, 0)));
  }

  @Test
  void addChild_should_throwTokenException_when_wrongTypeIsAdded() {
    VariableAssignment assignment;
    assignment = new VariableAssignment("someValue", 0, 0);
    assertThrows(TokenException.class, () -> assignment.addChild(new BoolLiteral(true, 0, 0)));
  }

  @Test
  void runObjectTesterOnVariableAssignment() {
    new ObjectTester<>(
        new VariableAssignment("valuea", 0, 0),
        new VariableAssignment("valuea", 0, 0),
        new VariableAssignment("valueb", 0, 0)
    ).testEquals().testHashCode().testToString();
  }
}