package nl.ica.asd.network.communication.interfaces;

import java.io.IOException;
import java.util.Set;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.network.communication.gameinitialisation.ConnectionResult;
import nl.ica.asd.storage.domain.GameData;
import nl.ica.asd.storage.exception.UnknownGameException;

public interface GameCreator {

  void createGame() throws IOException, UnknownGameException;

  void joinLobby(Player self, Set<Player> players, GameData gameData,
      ConnectionResult connectionResult) throws UnknownGameException;

}
