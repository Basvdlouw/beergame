package nl.ica.asd.storage.gameconfig.validation;

public class ValidateRuleResult {

  private final boolean valid;
  private final String message;

  public static ValidateRuleResult ok() {
    return new ValidateRuleResult(true, null);
  }

  public static ValidateRuleResult fail(String message) {
    return new ValidateRuleResult(false, message);
  }

  private ValidateRuleResult(boolean valid, String messsage) {
    this.valid = valid;
    this.message = messsage;
  }

  public boolean isvalid() {
    return valid;
  }

  public void throwIfInvalid() {
    if (!isvalid()) {
      throw new ValidationRuntimeException(getMessage());
    }
  }

  public String getMessage() {
    return message;
  }
}

