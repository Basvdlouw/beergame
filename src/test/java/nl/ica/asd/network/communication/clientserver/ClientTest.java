package nl.ica.asd.network.communication.clientserver;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import nl.ica.asd.logic.domain.BusinessType;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.logic.domain.PlayerStatus;
import nl.ica.asd.network.communication.datatransferobjects.DataTransferMessage;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.Vote;
import nl.ica.asd.util.TryAgain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@Tag("IntegrationTest")
class ClientTest {

  private static final String HOST = "localhost";

  private static final int PORT = 1934;

  private static final Player PLAYER = new Player("ryan", UUID.randomUUID(), BusinessType.FACTORY,
      PlayerStatus.CONNECTED_AND_ACTIVE,
      HOST, PORT);

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
  }

  @Test
  @ExtendWith(TryAgain.class)
  void testSendMessageToServer() throws IOException {
    final Server server = new Server(HOST, PORT, (received) -> "test");

    server.start();
    server.awaitInitialization();
    final Client client = new Client();

    final String response = client
        .sendMessage(objectMapper
                .writeValueAsString(new DataTransferMessage(PLAYER, new Vote(0, true), PLAYER)),
            PLAYER);

    assertEquals("test", response);
    server.shutdown();
    server.awaitShutdown();
  }

  @Test
  @ExtendWith(TryAgain.class)
  void testFailMessageToServer() throws IOException {
    final Server server = new Server(HOST, PORT, (received) -> "testFail");
    server.start();

    server.awaitInitialization();
    Client client = new Client();

    final String response = client
        .sendMessage(objectMapper
                .writeValueAsString(new DataTransferMessage(PLAYER, new Vote(0, true), PLAYER)),
            PLAYER);

    assertNotEquals("test", response);
    server.shutdown();
    server.awaitShutdown();
  }

  @Test
  @ExtendWith(TryAgain.class)
  void testSendMessageAsync() throws IOException {
    final Server server = new Server(HOST, PORT, (received) -> {
      await().atLeast(500, TimeUnit.MILLISECONDS);
      return "Hi, sorry that I took so long.";
    });

    server.start();
    server.awaitInitialization();

    final AtomicReference<Boolean> gotMessageBack = new AtomicReference<>(false);

    final Client client = new Client();

    client.sendMessageAsync(
        objectMapper.writeValueAsString(new DataTransferMessage(
            PLAYER,
            new Vote(0, true),
            PLAYER)),
        PLAYER,
        new TestCallBackHandler(gotMessageBack));

    assertFalse(gotMessageBack.get());

    client.waitForResponse();

    server.shutdown();
    server.awaitShutdown();
    assertTrue(gotMessageBack.get());
  }


  private class TestCallBackHandler implements ClientCallback {

    private AtomicReference<Boolean> gotMessageBack;

    TestCallBackHandler(AtomicReference<Boolean> gotMessageBack) {
      this.gotMessageBack = gotMessageBack;
    }

    @Override
    public void handle(String originalMessage, String responseFromServer) {
      gotMessageBack.set(true);
    }

    @Override
    public void receiveFailedRequest(String originalMessage) {
      // Not used
    }
  }
}