package nl.ica.asd.agenthandler.tokens.expressions;

import nl.ica.asd.util.ObjectTester;
import org.junit.jupiter.api.Test;

class IntLiteralTest {

  @Test
  void runObjectTesterIntLiteral() {
    new ObjectTester<>(
        new IntLiteral(5, 0, 0),
        new IntLiteral(5, 0, 0),
        new IntLiteral(10, 0, 0)
    ).testEquals().testHashCode().testToString();
  }
}