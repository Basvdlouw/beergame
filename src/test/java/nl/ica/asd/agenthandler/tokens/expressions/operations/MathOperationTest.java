package nl.ica.asd.agenthandler.tokens.expressions.operations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;
import nl.ica.asd.agenthandler.exceptions.TokenException;
import nl.ica.asd.agenthandler.tokens.expressions.BoolLiteral;
import nl.ica.asd.agenthandler.tokens.expressions.IntLiteral;
import nl.ica.asd.util.ObjectTester;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MathOperationTest {

  private MathOperation mathOperation;

  private IntLiteral intLiteralA, intLiteralB, intLiteralC;

  private BoolLiteral boolLiteral;

  private Map<String, Integer> variableMap;

  @BeforeEach
  void setUp() {
    this.intLiteralA = new IntLiteral(1, 0, 0);
    this.intLiteralB = new IntLiteral(2, 0, 0);
    this.intLiteralC = new IntLiteral(3, 0, 0);
    this.boolLiteral = new BoolLiteral(true, 0, 0);
    variableMap = new HashMap<>();
  }

  @Test
  void getIntValue_should_returnPercentage_when_operatorIsPercentage() {
    this.mathOperation = new MathOperation(MathOperatorType.PERCENTAGE, 0, 0);

    mathOperation.addChild(new IntLiteral(20, 0, 0));
    mathOperation.addChild(new IntLiteral(10, 0, 0));

    assertEquals(2,
        mathOperation.getIntValue(variableMap));
  }

  @Test
  void getIntValue_should_returnMultiplyValue_when_operatorIsMultiply() {
    this.mathOperation = new MathOperation(MathOperatorType.MULTIPLY, 0, 0);

    mathOperation.addChild(intLiteralB);
    mathOperation.addChild(intLiteralC);

    Map<String, Integer> variableMap = new HashMap<>();
    assertEquals(mathOperation.getIntValue(variableMap),
        intLiteralB.getIntValue(variableMap) * intLiteralC.getIntValue(variableMap));
  }

  @Test
  void getIntValue_should_returnDivideValue_when_operatorIsDivide() {
    this.mathOperation = new MathOperation(MathOperatorType.DIVIDE, 0, 0);

    mathOperation.addChild(intLiteralC);
    mathOperation.addChild(intLiteralB);

    assertEquals(mathOperation.getIntValue(variableMap),
        intLiteralC.getIntValue(variableMap) / intLiteralB.getIntValue(variableMap));
  }

  @Test
  void getIntValue_should_returnSum_when_operatorIsPlus() {
    this.mathOperation = new MathOperation(MathOperatorType.PLUS, 0, 0);

    mathOperation.addChild(intLiteralB);
    mathOperation.addChild(intLiteralC);

    assertEquals(mathOperation.getIntValue(variableMap),
        intLiteralB.getIntValue(variableMap) + intLiteralC.getIntValue(variableMap));
  }

  @Test
  void getIntValue_should_returnMinusValue_when_operatorIsMinus() {
    this.mathOperation = new MathOperation(MathOperatorType.MINUS, 0, 0);

    mathOperation.addChild(intLiteralC);
    mathOperation.addChild(intLiteralB);

    assertEquals(mathOperation.getIntValue(variableMap),
        intLiteralC.getIntValue(variableMap) - intLiteralB.getIntValue(variableMap));
  }

  @Test
  void getIntValue_should_returnPower_when_operatorIsPower() {
    this.mathOperation = new MathOperation(MathOperatorType.POWER, 0, 0);

    mathOperation.addChild(intLiteralB);
    mathOperation.addChild(intLiteralC);

    assertEquals(8, mathOperation.getIntValue(variableMap));
  }

  @Test
  void addChild_should_throwTokenException_when_runThreeTimes() {
    this.mathOperation = new MathOperation(MathOperatorType.MINUS, 0, 0);

    mathOperation.addChild(intLiteralA);
    mathOperation.addChild(intLiteralB);
    assertThrows(TokenException.class, () -> mathOperation.addChild(intLiteralC));
  }

  @Test
  void addChild_should_throwTokenException_when_childIsNoIntValue() {
    this.mathOperation = new MathOperation(MathOperatorType.MULTIPLY, 0, 0);

    assertThrows(TokenException.class, () -> mathOperation.addChild(boolLiteral));
  }

  @Test
  void runObjectTesterOnMathOperation() {
    new ObjectTester<>(
        new MathOperation(MathOperatorType.PLUS, 0, 0),
        new MathOperation(MathOperatorType.PLUS, 0, 0),
        new MathOperation(MathOperatorType.MINUS, 0, 0)
    ).testEquals().testHashCode().testToString();
  }
}
