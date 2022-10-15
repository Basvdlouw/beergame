package nl.ica.asd.agenthandler.exceptions;

import nl.ica.asd.agenthandler.ScriptError;

public class ScriptErrorException extends AgentException {

  private final ScriptError[] scriptErrors;

  public ScriptErrorException(ScriptError[] scriptErrors) {
    this("There were one or more script errors.", scriptErrors);
  }

  ScriptErrorException(String message, ScriptError[] scriptErrors) {
    super(message);
    this.scriptErrors = scriptErrors;
  }

  ScriptErrorException(String message, Exception cause, ScriptError[] scriptErrors) {
    super(message, cause);
    this.scriptErrors = scriptErrors;
  }

  public ScriptError[] getScriptErrors() {
    return scriptErrors;
  }
}
