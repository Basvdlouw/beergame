package nl.ica.asd.agenthandler.tokens.expressions;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import java.util.Map;
import nl.ica.asd.agenthandler.exceptions.TokenException;
import nl.ica.asd.agenthandler.tokens.expressions.operations.Comparison;
import nl.ica.asd.agenthandler.tokens.expressions.operations.ComparisonOperatorType;
import nl.ica.asd.util.ObjectTester;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ComparisonTest {

  private Map<String, Integer> variableMap;

  @BeforeEach
  void setUp() {
    variableMap = new HashMap<>();
  }

  @Test
  void getBoolValue_should_returnTrue_when_operatorIsEqualsAndValuesAreEqual() {
    Comparison comparison;
    comparison = new Comparison(ComparisonOperatorType.EQUALS, 0, 0);
    comparison.addChild(new IntLiteral(5, 0, 0));
    comparison.addChild(new IntLiteral(5, 0, 0));
    assertTrue(comparison.getBoolValue(variableMap));
  }

  @Test
  void getBoolValue_should_returnFalse_when_operatorIsEqualsAndValuesAreNotEqual() {
    Comparison comparison;
    comparison = new Comparison(ComparisonOperatorType.EQUALS, 0, 0);
    comparison.addChild(new IntLiteral(2, 0, 0));
    comparison.addChild(new IntLiteral(3, 0, 0));
    assertFalse(comparison.getBoolValue(variableMap));
  }

  @Test
  void getBoolValue_should_returnTrue_when_operatorIsMoreThanAndAIsMoreThanB() {
    Comparison comparison;
    comparison = new Comparison(ComparisonOperatorType.MORE_THAN, 0, 0);
    comparison.addChild(new IntLiteral(5, 0, 0));
    comparison.addChild(new IntLiteral(4, 0, 0));
    assertTrue(comparison.getBoolValue(variableMap));
  }

  @Test
  void getBoolValue_should_returnFalse_when_operatorIsMoreThanAndAIsLessThanB() {
    Comparison comparison;
    comparison = new Comparison(ComparisonOperatorType.MORE_THAN, 0, 0);
    comparison.addChild(new IntLiteral(4, 0, 0));
    comparison.addChild(new IntLiteral(5, 0, 0));
    assertFalse(comparison.getBoolValue(variableMap));
  }

  @Test
  void getBoolValue_should_returnTrue_when_operatorIsLessThanAndAIsMoreThanB() {
    Comparison comparison;
    comparison = new Comparison(ComparisonOperatorType.LESS_THAN, 0, 0);
    comparison.addChild(new IntLiteral(4, 0, 0));
    comparison.addChild(new IntLiteral(5, 0, 0));
    assertTrue(comparison.getBoolValue(variableMap));
  }

  @Test
  void getBoolValue_should_returnFalse_when_operatorIsLessThanAndAIsLessThanB() {
    Comparison comparison;
    comparison = new Comparison(ComparisonOperatorType.LESS_THAN, 0, 0);
    comparison.addChild(new IntLiteral(5, 0, 0));
    comparison.addChild(new IntLiteral(4, 0, 0));
    assertFalse(comparison.getBoolValue(variableMap));
  }

  @Test
  void addChild_should_throwTokenException_when_tooManyChildrenAreAdded() {
    Comparison comparison;
    comparison = new Comparison(ComparisonOperatorType.EQUALS, 0, 0);
    try {
      comparison.addChild(new IntLiteral(5, 0, 0));
      comparison.addChild(new IntLiteral(4, 0, 0));
      comparison.addChild(new IntLiteral(6, 0, 0));
      fail("Expected a TokenException.");
    } catch (TokenException e) {
      // Success!
    }
  }

  @Test
  void addChild_should_throwTokenException_when_wrongTypeIsAdded() {
    Comparison comparison;
    comparison = new Comparison(ComparisonOperatorType.EQUALS, 0, 0);
    try {
      comparison.addChild(new BoolLiteral(true, 0, 0));
      fail("Expected a TokenException.");
    } catch (TokenException e) {
      // Success!
    }
  }

  @Test
  void runObjectTesterOnComparison() {
    new ObjectTester<>(
        new Comparison(ComparisonOperatorType.EQUALS, 0, 0),
        new Comparison(ComparisonOperatorType.EQUALS, 0, 0),
        new Comparison(ComparisonOperatorType.MORE_THAN, 0, 0)
    ).testEquals().testHashCode().testToString();
  }
}