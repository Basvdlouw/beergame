package nl.ica.asd.network.communication.twophasecommit;

import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Named;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.network.communication.datatransferobjects.DataTransferObject;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.AppendEntry;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.InitialConnectionRequest;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.SerializationExceptionResponse;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.VoteRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("Receiver")
public class Receiver {

  private final ExternalConnectionEndpoint externalConnectionEndpoint;
  private static final Logger logger = LoggerFactory.getLogger(Receiver.class);

  @Inject
  public Receiver(
      ExternalConnectionEndpoint externalConnectionEndpoint
  ) {
    this.externalConnectionEndpoint = externalConnectionEndpoint;
  }

  public DataTransferObject receiveData(Player sender, DataTransferObject dataTransferObject) {
    try {
      if (dataTransferObject.getClass() == AppendEntry.class) {
        return externalConnectionEndpoint
            .receiveAppendEntry(sender, (AppendEntry) dataTransferObject);
      } else if (dataTransferObject.getClass() == VoteRequest.class) {
        return externalConnectionEndpoint
            .receiveVoteRequest(sender, (VoteRequest) dataTransferObject);
      } else if (dataTransferObject.getClass() == InitialConnectionRequest.class) {
        return externalConnectionEndpoint
            .receiveConnectionRequest((InitialConnectionRequest) dataTransferObject);
      } else if (dataTransferObject.getClass() == SerializationExceptionResponse.class) {
        logger.error("The sent message couldn't be deserialized by the receiver");
        return new SerializationExceptionResponse(
            "The message couldn't be deserialized, no way to handle this.");
      }
    } catch (IOException e) {
      logger.error("Failed to get own IP address");
    }
    throw new IllegalStateException(
        "Object received is not recognized as one of the available options");

  }
}
