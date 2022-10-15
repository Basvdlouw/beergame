package nl.ica.asd.agenthandler.tokens;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import nl.ica.asd.agenthandler.exceptions.CheckerException;
import nl.ica.asd.agenthandler.exceptions.TokenException;

public class StatementContainer extends Statement {

  private final List<Statement> statements;

  public StatementContainer(int line, int charIndex) {
    super(line, charIndex);
    this.statements = new ArrayList<>();
  }

  @Override
  public void addChild(ASTNode astNode) {
    if (astNode instanceof Statement) {
      addStatement((Statement) astNode);
    } else {
      throw new TokenException(astNode.getClass() + " is not a statement.");
    }
  }

  @Override
  public void check() throws CheckerException {
    for (Statement statement : statements) {
      statement.check();
    }
  }

  @Override
  public void execute(Map<String, Integer> variableMap) {
    for (Statement statement : statements) {
      statement.execute(variableMap);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    StatementContainer that = (StatementContainer) o;

    return statements.equals(that.statements);
  }

  @Override
  public int hashCode() {
    return statements.hashCode();
  }

  @Override
  public String toString() {
    return "StatementContainer{" +
        "statements=" + statements +
        "}";
  }

  void addStatement(Statement statement) {
    statements.add(statement);
  }
}
