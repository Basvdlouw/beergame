package nl.ica.asd.util;

import static nl.ica.asd.util.Retry.retry;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

public class TryAgain implements TestExecutionExceptionHandler {

  @Override
  public void handleTestExecutionException(ExtensionContext extensionContext, Throwable throwable) {

    if (!(throwable instanceof RetryException)) {
      retry(() -> extensionContext.getRequiredTestMethod()
          .invoke(extensionContext.getRequiredTestInstance()));
    }
  }
}
