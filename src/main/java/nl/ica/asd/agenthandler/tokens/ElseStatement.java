package nl.ica.asd.agenthandler.tokens;

public class ElseStatement extends StatementContainer {

  public ElseStatement(int line, int charIndex) {
    super(line, charIndex);
  }

  @Override
  public String toString() {
    return "ElseStatement{} " + super.toString();
  }
}
