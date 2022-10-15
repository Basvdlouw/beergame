package nl.ica.asd.agenthandler.exceptions;

public class TokenException extends RuntimeException {

  public TokenException(String s) {
    super(s);
  }

  public TokenException(String s, Throwable throwable) {
    super(s, throwable);
  }
}
