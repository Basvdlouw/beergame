package nl.ica.asd.agenthandler.tokens.expressions;

import nl.ica.asd.agenthandler.tokens.Expression;

abstract class Literal extends Expression {

  Literal(int line, int charIndex) {
    super(line, charIndex);
  }
}
