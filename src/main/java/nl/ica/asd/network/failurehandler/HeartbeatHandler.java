package nl.ica.asd.network.failurehandler;

import java.io.IOException;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.AppendEntry;
import nl.ica.asd.network.failurehandler.exceptions.UnknownNodeException;

public interface HeartbeatHandler {

  void receiveHeartbeat(Player player, AppendEntry heartbeat)
      throws UnknownNodeException, IOException;

  void startSendingHeartbeats() throws IOException;

  void reset();
}
