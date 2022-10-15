package nl.ica.asd.network.communication.interfaces;

import nl.ica.asd.logic.domain.GameState;

public interface SendGameStateUpdate {

  void sendGameStateUpdate(GameState gameState);

}
