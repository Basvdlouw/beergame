package nl.ica.asd.agenthandler.exceptions;

public class AgentException extends Exception {

  public AgentException(String message) {
    super(message);
  }

  public AgentException(String message, Exception cause) {
    super(message, cause);
  }
}
