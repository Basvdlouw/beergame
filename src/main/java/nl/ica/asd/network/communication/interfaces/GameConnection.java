package nl.ica.asd.network.communication.interfaces;

import java.io.IOException;
import nl.ica.asd.logic.domain.Player;

public interface GameConnection {

  void connectToGame(String ipAddress, String userName) throws IOException;

  void leaveGame(Player player);
}
