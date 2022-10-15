package nl.ica.asd.agenthandler.tokens;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;
import nl.ica.asd.agenthandler.tokens.expressions.Variable;
import nl.ica.asd.util.ObjectTester;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VariableTest {

  private Map<String, Integer> variableMap;

  @BeforeEach
  void setUp() {
    variableMap = new HashMap<>();
    variableMap.put("valuea", 10);
    variableMap.put("valueb", 20);
  }

  @Test
  void getIntValue_should_returnVariableValue_when_variableExists() {
    Variable variable = new Variable("valuea", 0, 0);
    assertEquals(10, variable.getIntValue(variableMap));
  }

  @Test
  void getIntValue_should_returnZero_when_variableDoesNotExist() {
    Variable variable = new Variable("valuec", 0, 0);
    assertEquals(0, variable.getIntValue(variableMap));
  }

  @Test
  void getIntValue_should_returnVariableValue_when_variableExistsWithDifferentCapitalization() {
    Variable variable = new Variable("ValueA", 0, 0);
    assertEquals(10, variable.getIntValue(variableMap));
  }

  @Test
  void runObjectTesterOnVariable() {
    new ObjectTester<>(new Variable("Value A", 0, 0), new Variable("Value A", 0, 0),
        new Variable("Value B", 0, 0))
        .testEquals()
        .testHashCode()
        .testToString();
  }
}