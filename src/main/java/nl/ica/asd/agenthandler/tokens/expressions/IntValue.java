package nl.ica.asd.agenthandler.tokens.expressions;

import java.util.Map;
import nl.ica.asd.agenthandler.exceptions.CheckerException;

public interface IntValue {

  int getIntValue(Map<String, Integer> variableMap);

  void check() throws CheckerException;
}
