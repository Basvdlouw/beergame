package nl.ica.asd.agenthandler.tokens;

import static org.junit.jupiter.api.Assertions.assertThrows;

import nl.ica.asd.agenthandler.exceptions.TokenException;
import org.junit.jupiter.api.Test;

class ASTNodeTest {

  @Test
  void addChild_should_throwTokenException() {
    assertThrows(TokenException.class,
        () -> new TestStatement(0, 0).addChild(new TestStatement(0, 0)),
        "ASTNode doesn't support child nodes.");
  }

}
