package nl.ica.asd.network.communication.clientserver;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import nl.ica.asd.network.communication.datatransferobjects.DataTransferMessage;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.Vote;
import nl.ica.asd.util.TryAgain;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@Tag("IntegrationTest")
class ServerTest {

  private static final String HOST = "localhost";

  @Test
  @ExtendWith(TryAgain.class)
  void startServer() throws Exception {
    final Server server = new Server(HOST, Server.DEFAULT_PORT + 1, (msg) -> "");
    server.start();
    server.awaitInitialization();
    try (Socket socket = new Socket(HOST, Server.DEFAULT_PORT + 1)) {
      assertTrue(socket.isConnected());
    }
    server.shutdown();
    server.awaitShutdown();
  }

  @Test
  @ExtendWith(TryAgain.class)
  void stopServer() throws IOException {
    final Server server = new Server(HOST, Server.DEFAULT_PORT + 2, (msg) -> "");
    server.start();
    server.awaitInitialization();
    if (server.isRunning()) {
      try (Socket socket = new Socket(HOST, Server.DEFAULT_PORT + 2)) {
        assertTrue(socket.isConnected());
      }
    }
    server.shutdown();
    server.awaitShutdown();

    await().atLeast(150, TimeUnit.MILLISECONDS);
    assertThrows(ConnectException.class, () -> new Socket(HOST, Server.DEFAULT_PORT + 2));
  }

  @Test
  @ExtendWith(TryAgain.class)
  void testSendAndReceive() throws IOException, InterruptedException {

    final String messageToReceive = "java > c#";
    final Server server = new Server(HOST, Server.DEFAULT_PORT + 3, (msg) -> messageToReceive);
    server.start();
    server.awaitInitialization();
    if (server.isRunning()) {
      try (Socket socket = new Socket(HOST, Server.DEFAULT_PORT + 3)) {
        assertTrue(socket.isConnected());
        final PrintWriter sendMessage = new PrintWriter(socket.getOutputStream(), true);
        final BufferedReader receiveMessage = new BufferedReader(
            new InputStreamReader(socket.getInputStream()));
        sendMessage.println(new ObjectMapper()
            .writeValueAsString(new DataTransferMessage(null, new Vote(45, true), null)));
        assertEquals(messageToReceive, receiveMessage.readLine());
      }
    }
    server.shutdown();
    server.awaitShutdown();
  }
}