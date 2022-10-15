package nl.ica.asd.network.communication.interfaces;

import java.util.Set;
import nl.ica.asd.logic.domain.Player;

public interface SendPlayerListUpdate {

  void sendPlayerListUpdate(Set<Player> playerUpdate);

}
