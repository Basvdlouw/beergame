package nl.ica.asd.network.communication.clientserver;

import static org.awaitility.Awaitility.await;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import nl.ica.asd.logic.domain.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {

  private boolean responseBack = false;


  private static final Logger logger = LoggerFactory.getLogger(Client.class);


  public String sendMessage(String message, Player receiver) throws IOException {
    responseBack = false;
    try (Socket clientSocket = createSocket(receiver)) {
      try (PrintWriter sendMessage = getWriterFromSocket(clientSocket)) {
        try (BufferedReader receiveResponse = getReaderFromSocket(clientSocket)) {
          sendMessage.println(message);
          String response = receiveResponse.readLine();
          responseBack = true;
          return response;
        }
      }
    }
  }

  public void sendMessageAsync(String message, Player receiver, ClientCallback callback) {
    responseBack = false;
    new Thread(() -> {
      try {
        String response = sendMessage(message, receiver);
        callback.handle(message, response);
      } catch (IOException e) {
        // Message timeout. This means the receiver either doesn't exist, or isn't listening
        try {
          callback.receiveFailedRequest(message);
        } catch (IOException ex) {
          logger.error("Failed handling message timeout");
        }

        logger.error("Timeout sending message to server", e);
      }
    }).start();
  }

  private boolean gotResponseBack() {
    return responseBack;
  }

  public void waitForResponse() {

    await().atMost(10, TimeUnit.SECONDS).until(this::gotResponseBack);
  }

  private BufferedReader getReaderFromSocket(Socket clientSocket) throws IOException {
    return new BufferedReader(
        new InputStreamReader(clientSocket.getInputStream()));
  }

  private PrintWriter getWriterFromSocket(Socket clientSocket) throws IOException {
    return new PrintWriter(clientSocket.getOutputStream(), true);
  }

  private Socket createSocket(Player receivingPlayer) throws IOException {
    return new Socket(receivingPlayer.getIP(), receivingPlayer.getPort());
  }
}
