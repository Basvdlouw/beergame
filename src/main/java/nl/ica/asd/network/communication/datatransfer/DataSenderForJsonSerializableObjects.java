package nl.ica.asd.network.communication.datatransfer;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Named;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.network.communication.clientserver.Client;
import nl.ica.asd.network.communication.clientserver.ClientCallback;
import nl.ica.asd.network.communication.datatransferobjects.DataTransferMessage;
import nl.ica.asd.network.communication.datatransferobjects.DataTransferObject;
import nl.ica.asd.network.communication.twophasecommit.Messenger.CallBackHandler;
import nl.ica.asd.network.playerlobby.ConnectedPlayers;

@Named("DataSenderForJsonSerializableObjects")
public class DataSenderForJsonSerializableObjects implements ClientCallback {

  @Named("Messenger")
  private CallBackHandler callBackHandler;
  private final ObjectMapper objectMapper;
  @Named("ConnectedPlayers")
  private final ConnectedPlayers connectedPlayers;

  @Inject
  public DataSenderForJsonSerializableObjects(
      ConnectedPlayers connectedPlayers
  ) {
    this.objectMapper = new ObjectMapper();
    this.connectedPlayers = connectedPlayers;
  }


  public void sendObjectAsSerializedData(Player target, DataTransferObject objectToBeSend,
      CallBackHandler callBackHandler)
      throws IOException {
    this.callBackHandler = callBackHandler;
    final Client client = new Client();
    final DataTransferMessage dataTransferMessage = new DataTransferMessage(
        connectedPlayers.getPlayerForSelf(),
        objectToBeSend,
        target
    );
    client.sendMessageAsync(objectMapper.writeValueAsString(dataTransferMessage), target, this);
  }

  @Override
  public void handle(String originalMessage, String responseFromServer)
      throws IOException {
    final DataTransferMessage originalMessageObject = objectMapper
        .readValue(originalMessage, DataTransferMessage.class);
    final DataTransferMessage responseFromServerObject = objectMapper
        .readValue(responseFromServer, DataTransferMessage.class);
    callBackHandler.receiveResponse(
        originalMessageObject.getPayload(),
        responseFromServerObject.getPayload(),
        responseFromServerObject.getSender()
    );
  }

  @Override
  public void receiveFailedRequest(String originalMessage) throws IOException {
    callBackHandler
        .receiveFailedRequest(
            objectMapper.readValue(originalMessage, DataTransferMessage.class).getPayload());
  }
}
