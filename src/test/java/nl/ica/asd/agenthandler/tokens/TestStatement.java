package nl.ica.asd.agenthandler.tokens;

import java.util.Map;

public class TestStatement extends Statement {

  private int timesExecuted;

  TestStatement(int line, int charIndex) {
    super(line, charIndex);
  }

  @Override
  public void execute(Map<String, Integer> variableMap) {
    timesExecuted++;
  }

  public int getTimesExecuted() {
    return timesExecuted;
  }

  public boolean executedOnce() {
    return timesExecuted == 1;
  }

  public boolean notExecuted() {
    return timesExecuted == 0;
  }

  @Override
  public void check() {
    //This method is not used here
  }
}
