package nl.ica.asd.network.failurehandler.exceptions;

public class UnknownNodeException extends Exception {

  private final String ipAddress;

  public UnknownNodeException(String ipAddress) {
    super("Unknown node:" + ipAddress);
    this.ipAddress = ipAddress;
  }

  public String getIpAddress() {
    return ipAddress;
  }
}