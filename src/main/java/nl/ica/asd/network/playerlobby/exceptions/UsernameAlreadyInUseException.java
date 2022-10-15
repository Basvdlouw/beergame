package nl.ica.asd.network.playerlobby.exceptions;

public class UsernameAlreadyInUseException extends Exception {

  private final String username;

  public UsernameAlreadyInUseException(String username) {
    this.username = username;
  }

  public String getUsername() {
    return username;
  }
}
