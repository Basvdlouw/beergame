package nl.ica.asd.agenthandler;

import nl.ica.asd.agenthandler.exceptions.CheckerException;

class Checker {

  void checkAST(AST ast) throws CheckerException {
    if (ast == null) {
      throw new NullPointerException("AST is null.");
    }

    ast.getRoot().check();
  }

}
