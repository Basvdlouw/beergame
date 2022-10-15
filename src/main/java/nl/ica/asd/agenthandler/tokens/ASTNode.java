package nl.ica.asd.agenthandler.tokens;

import nl.ica.asd.agenthandler.exceptions.CheckerException;
import nl.ica.asd.agenthandler.exceptions.TokenException;

public abstract class ASTNode {

  private final int line;
  private final int charIndex;

  public ASTNode(int line, int charIndex) {
    this.line = line;
    this.charIndex = charIndex;
  }

  public void addChild(ASTNode astNode) {
    throw new TokenException(getClass().getName() + " doesn't support child nodes.");
  }

  public int getLine() {
    return line;
  }

  public int getCharIndex() {
    return charIndex;
  }

  public abstract void check() throws CheckerException;
}
