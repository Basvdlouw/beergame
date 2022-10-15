package nl.ica.asd.agenthandler;

import nl.ica.asd.util.ObjectTester;
import org.junit.jupiter.api.Test;

class ScriptErrorTest {

  @Test
  void runObjectTesterOnScriptError() {
    new ObjectTester<>(
        new ScriptError(ScriptErrorType.CHECKER_ERROR, "Dit is een foutmelding.", 0, 0),
        new ScriptError(ScriptErrorType.CHECKER_ERROR, "Dit is een foutmelding.", 0, 0),
        new ScriptError(ScriptErrorType.CHECKER_ERROR, "Dit is een andere foutmelding.", 0, 0)
    ).testEquals().testHashCode().testToString();
  }
}