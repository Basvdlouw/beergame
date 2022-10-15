package nl.ica.asd.agenthandler;

import nl.ica.asd.agenthandler.exceptions.CheckerException;
import nl.ica.asd.agenthandler.exceptions.ScriptErrorException;

public class ASTHandler {

  public AST build(String script) throws ScriptErrorException {
    return check(parse(script));
  }

  private AST parse(String script) throws ScriptErrorException {
    return new ASTBuilder().getAST(script);
  }

  AST check(AST ast) throws ScriptErrorException {
    try {
      new Checker().checkAST(ast);
    } catch (CheckerException e) {
      throw new ScriptErrorException(new ScriptError[]{
          new ScriptError(
              ScriptErrorType.CHECKER_ERROR,
              e.getMessage(),
              e.getAstNode().getLine(),
              e.getAstNode().getCharIndex()
          )
      });
    }
    return ast;
  }
}
