package nl.ica.asd.util;

public interface AfterFailedTryCallback {

  void afterTry(int amountOfTriesLeft, Exception thrownException);


}
