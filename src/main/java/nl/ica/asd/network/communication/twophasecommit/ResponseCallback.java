package nl.ica.asd.network.communication.twophasecommit;

import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.network.communication.datatransferobjects.DataTransferObject;

public interface ResponseCallback {

  void receiveResponse(DataTransferObject originalMessage, DataTransferObject responseMessage,
      Player sender);

  void receiveFailedRequest(DataTransferObject originalMessage);
}
