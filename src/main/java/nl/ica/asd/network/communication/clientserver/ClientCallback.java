package nl.ica.asd.network.communication.clientserver;

import java.io.IOException;

public interface ClientCallback {

  void handle(String originalMessage, String responseFromServer)
      throws IOException;

  void receiveFailedRequest(String originalMessage) throws IOException;
}