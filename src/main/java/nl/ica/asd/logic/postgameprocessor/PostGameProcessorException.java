package nl.ica.asd.logic.postgameprocessor;

public class PostGameProcessorException extends Exception {
  public PostGameProcessorException(String message) {
    super(message);
  }

  public PostGameProcessorException(String message, Throwable cause) {
    super(message, cause);
  }

  public PostGameProcessorException(Throwable cause) {
    super(cause);
  }
}
