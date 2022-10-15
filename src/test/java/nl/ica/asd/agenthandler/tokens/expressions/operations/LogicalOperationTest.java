package nl.ica.asd.agenthandler.tokens.expressions.operations;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import nl.ica.asd.agenthandler.exceptions.TokenException;
import nl.ica.asd.agenthandler.tokens.expressions.BoolLiteral;
import nl.ica.asd.agenthandler.tokens.expressions.IntLiteral;
import nl.ica.asd.util.ObjectTester;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LogicalOperationTest {

  private LogicalOperation logicalOperationAND;
  private LogicalOperation logicalOperationOR;

  private BoolLiteral boolTrue;
  private BoolLiteral boolFalse;

  private Map<String, Integer> variableMap;

  @BeforeEach
  void setUp() {
    this.logicalOperationAND = new LogicalOperation(LogicalOperatorType.AND, 0, 0);
    this.logicalOperationOR = new LogicalOperation(LogicalOperatorType.OR, 0, 0);
    this.boolTrue = new BoolLiteral(true, 0, 0);
    this.boolFalse = new BoolLiteral(false, 0, 0);
    this.variableMap = new HashMap<>();
  }

  @Test
  void getBoolValue_should_return_true_when_child_is_OrOperator() {
    logicalOperationOR.addChild(boolTrue);
    logicalOperationOR.addChild(boolFalse);
    logicalOperationAND.addChild(logicalOperationOR);
    logicalOperationAND.addChild(boolTrue);

    assertTrue(logicalOperationAND.getBoolValue(variableMap));
  }

  @Test
  void addChild_should_throwTokenException_when_aThirdChildIsAddedInAnLogicalOperation() {
    logicalOperationAND.addChild(boolTrue);
    logicalOperationAND.addChild(boolTrue);

    assertThrows(TokenException.class, () -> logicalOperationAND.addChild(boolTrue));
  }

  @Test
  void addChild_should_throwTokenException_when_notABoolValueIsAdded() {
    IntLiteral intLiteral = new IntLiteral(1, 0, 0);
    assertThrows(TokenException.class, () -> logicalOperationAND.addChild(intLiteral));
  }

  @Test
  void getBoolValue_should_throwTokenException_when_ALogicalOperationDoesntHaveALogicalOperatorType() {
    LogicalOperation test = new LogicalOperation(null, 0, 0);
    test.addChild(boolTrue);
    test.addChild(boolTrue);
    assertThrows(NullPointerException.class, () -> test.getBoolValue(variableMap));
  }

  @Test
  void getBoolValue_should_throwTokenException_when_ALogicalOperationDoesntHaveAnyChildren() {
    LogicalOperation test = new LogicalOperation(LogicalOperatorType.OR, 0, 0);
    assertThrows(TokenException.class, () -> test.getBoolValue(variableMap));
  }

  @Test
  void logicalOperationAND_getBoolValue_should_returnTrue_when_bothChilderenAreTrue() {
    logicalOperationAND.addChild(boolTrue);
    logicalOperationAND.addChild(boolTrue);

    assertTrue(logicalOperationAND.getBoolValue(variableMap));
  }

  @Test
  void logicalOperationAND_getBoolValue_should_returnFalse_when_oneChildIsTrueAndTheOtherFalse() {
    logicalOperationAND.addChild(boolTrue);
    logicalOperationAND.addChild(boolFalse);

    assertFalse(logicalOperationAND.getBoolValue(variableMap));
  }

  @Test
  void logicalOperationAND_getBoolValue_should_returnFalse_when_bothChilderenAreFalse() {
    logicalOperationAND.addChild(boolFalse);
    logicalOperationAND.addChild(boolFalse);

    assertFalse(logicalOperationAND.getBoolValue(variableMap));
  }

  @Test
  void logicalOperationOR_getBoolValue_should_returnTrue_when_bothChilderenAreTrue() {
    logicalOperationOR.addChild(boolTrue);
    logicalOperationOR.addChild(boolTrue);

    assertTrue(logicalOperationOR.getBoolValue(variableMap));
  }

  @Test
  void logicalOperationOR_getBoolValue_should_returnTrue_when_oneChildIsTrueAndTheOtherFalse() {
    logicalOperationOR.addChild(boolTrue);
    logicalOperationOR.addChild(boolFalse);

    assertTrue(logicalOperationOR.getBoolValue(variableMap));
  }

  @Test
  void logicalOperationOR_getBoolValue_should_returnFalse_when_bothChilderenAreFalse() {
    logicalOperationOR.addChild(boolFalse);
    logicalOperationOR.addChild(boolFalse);

    assertFalse(logicalOperationOR.getBoolValue(variableMap));
  }

  @Test
  void runObjectTesterOnLogicalOperation() {
    new ObjectTester<>(
        new LogicalOperation(LogicalOperatorType.AND, 0, 0),
        new LogicalOperation(LogicalOperatorType.AND, 0, 0),
        new LogicalOperation(LogicalOperatorType.OR, 0, 0)
    ).testEquals().testHashCode().testToString();
  }
}