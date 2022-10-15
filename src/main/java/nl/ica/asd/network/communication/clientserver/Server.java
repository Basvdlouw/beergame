package nl.ica.asd.network.communication.clientserver;

import static org.awaitility.Awaitility.await;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server extends Thread {

  private ServerSocket serverSocket;
  private boolean running = false;

  private boolean successfullyShutdown = false;
  private final int port;
  private final String ipAddress;
  public static final int DEFAULT_PORT = 6666;
  private static final int DEFAULT_BACKLOG = 50;
  private static final Logger logger = LoggerFactory.getLogger(Server.class);

  private final IncomingDataHandler incomingDataHandler;

  public Server(String ipAddress, int port, IncomingDataHandler incomingDataHandler) {
    this.port = port;
    this.ipAddress = ipAddress;
    this.incomingDataHandler = incomingDataHandler;
  }

  private void openServerSocket() throws IOException {
    this.serverSocket = new ServerSocket(port, DEFAULT_BACKLOG,
        InetAddress.getByName(ipAddress));

    //weUPnPRouter.addPortMapping();
  }

  private void runServer() {
    while (running) {
      try {
        Socket socket = this.serverSocket.accept();
        new ServerThread(socket).start();
      } catch (IOException e) {
        logger.error("Error accepting client requests", e);
      }
    }
    successfullyShutdown = true;
  }


  public void shutdown() throws IOException {
    this.serverSocket.close();
    this.running = false;

    //weUPnPRouter.removePortMapping();
  }

  public void awaitInitialization() {
    await().atMost(40, TimeUnit.SECONDS).until(() -> running);
  }

  public void awaitShutdown() {
    await().atMost(10, TimeUnit.SECONDS).until(() -> successfullyShutdown);
  }

  public boolean isRunning() {
    return running;
  }

  @Override
  public void run() {
    try {
      openServerSocket();
      running = true;
      runServer();
    } catch (IOException e) {
      logger.error("Connection Exception", e);
    }
  }


  private class ServerThread extends Thread {

    private final Socket clientSocket;

    private ServerThread(Socket serverSocket) {
      this.clientSocket = serverSocket;
    }

    @Override
    public void run() {
      try (BufferedReader receiveMessage = new BufferedReader(
          new InputStreamReader(clientSocket.getInputStream()))) {
        try (PrintWriter sendMessage = new PrintWriter(clientSocket.getOutputStream(), true)) {
          String receivedMessage = receiveMessage.readLine();
          String response = incomingDataHandler.handleReceivedData(receivedMessage);
          sendMessage.println(response);
        }
      } catch (IOException e) {
        logger.error("IOException on ServerThreadResponse", e);
      }
    }
  }
}
