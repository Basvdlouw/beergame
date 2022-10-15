package nl.ica.asd.agenthandler.tokens;

import java.util.Map;

public abstract class Statement extends ASTNode {

  Statement(int line, int charIndex) {
    super(line, charIndex);
  }

  public abstract void execute(Map<String, Integer> variableMap);
}
