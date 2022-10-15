package nl.ica.asd.agenthandler;

import java.io.Serializable;
import java.util.Objects;

public class ScriptError implements Serializable {

  private final ScriptErrorType errorType;
  private final String message;
  private final int line;
  private final int token;

  public ScriptError(ScriptErrorType errorType, String message, int line, int token) {
    this.errorType = errorType;
    this.message = message;
    this.line = line;
    this.token = token;
  }

  public String getMessage() {
    return message;
  }

  public int getLine() {
    return line;
  }

  public int getToken() {
    return token;
  }

  public ScriptErrorType getErrorType() {
    return errorType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ScriptError that = (ScriptError) o;
    return getLine() == that.getLine() &&
        getToken() == that.getToken() &&
        getErrorType() == that.getErrorType() &&
        Objects.equals(getMessage(), that.getMessage());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getErrorType(), getMessage(), getLine(), getToken());
  }

  @Override
  public String toString() {
    return "ScriptError{" +
        "errorType=" + errorType +
        ", message='" + message + '\'' +
        ", line=" + line +
        ", token=" + token +
        '}';
  }
}
