package nl.ica.asd.network.communication.datatransfer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Named;
import nl.ica.asd.network.communication.clientserver.IncomingDataHandler;
import nl.ica.asd.network.communication.datatransferobjects.DataTransferMessage;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.SerializationExceptionResponse;
import nl.ica.asd.network.communication.twophasecommit.Receiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataReceiverForDeserializableJson implements IncomingDataHandler {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(DataReceiverForDeserializableJson.class);
  @Named("Receiver")
  private final Receiver receiver;

  private final ObjectMapper objectMapper;

  @Inject
  DataReceiverForDeserializableJson(Receiver receiver) {
    this.receiver = receiver;
    this.objectMapper = new ObjectMapper();
  }

  @Override
  public String handleReceivedData(String message) {
    final DataTransferMessage dataTransferMessage;
    try {
      dataTransferMessage = objectMapper.readValue(message, DataTransferMessage.class);
      return objectMapper
          .writeValueAsString(new DataTransferMessage(dataTransferMessage.getReceiver(),
                  receiver.receiveData(
                      dataTransferMessage.getSender(),
                      dataTransferMessage.getPayload()
                  ),
              dataTransferMessage.getSender()
              )
          );
    } catch (IOException e) {
      try {
        return objectMapper
            .writeValueAsString(new SerializationExceptionResponse(e.getMessage()));
      } catch (JsonProcessingException e1) {
        final Exception exception = new Exception(
            "Could not generate a message to respond to the client about the failure to deserialize his message",
            e1.getCause());
        LOGGER.error(exception.getMessage());
      }
    }
    return "";
  }
}