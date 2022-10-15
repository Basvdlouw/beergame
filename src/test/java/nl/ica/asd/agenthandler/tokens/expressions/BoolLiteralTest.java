package nl.ica.asd.agenthandler.tokens.expressions;

import nl.ica.asd.util.ObjectTester;
import org.junit.jupiter.api.Test;

class BoolLiteralTest {

  @Test
  void runObjectTesterOnBoolLiteral() {
    new ObjectTester<>(
        new BoolLiteral(true, 0, 0),
        new BoolLiteral(true, 0, 0),
        new BoolLiteral(false, 0, 0)
    ).testEquals().testHashCode().testToString();
  }
}