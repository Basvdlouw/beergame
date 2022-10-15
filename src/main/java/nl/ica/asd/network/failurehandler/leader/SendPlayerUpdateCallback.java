package nl.ica.asd.network.failurehandler.leader;

import nl.ica.asd.logic.domain.Player;

public interface SendPlayerUpdateCallback {

  void sendPlayerUpdate(Player player);
}
