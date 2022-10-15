package nl.ica.asd.util;

public class RetryException extends RuntimeException {

  public RetryException(String message, Throwable cause) {
    super(message, cause);
  }
}
