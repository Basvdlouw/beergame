package nl.ica.asd.agenthandler.exceptions;

import nl.ica.asd.agenthandler.tokens.ASTNode;

public class CheckerException extends Exception {

  private final transient ASTNode astNode;

  public CheckerException(String message, ASTNode astNode) {
    super(message);
    this.astNode = astNode;
  }

  public CheckerException(String message, Exception cause, ASTNode astNode) {
    super(message, cause);
    this.astNode = astNode;
  }

  public ASTNode getAstNode() {
    return astNode;
  }
}
