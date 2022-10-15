package nl.ica.asd.agenthandler;

import nl.ica.asd.agenthandler.tokens.Statement;

public class AST {

  private final Statement root;

  AST(Statement root) {
    this.root = root;
  }

  Statement getRoot() {
    return root;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    AST ast = (AST) o;

    return getRoot().equals(ast.getRoot());
  }

  @Override
  public int hashCode() {
    return getRoot().hashCode();
  }

  @Override
  public String toString() {
    return "AST{" +
        "root=" + root +
        '}';
  }
}
