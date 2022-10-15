package nl.ica.asd.network.playerlobby;

import java.io.IOException;
import java.util.Set;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.network.failurehandler.leader.SendPlayerUpdateCallback;
import nl.ica.asd.network.playerlobby.exceptions.PlayerNotFoundException;
import nl.ica.asd.network.playerlobby.exceptions.UsernameAlreadyInUseException;
import nl.ica.asd.storage.exception.UnknownGameException;
import nl.ica.asd.storage.gameconfig.GameConfig;

public interface ConnectedPlayers {

  Set<Player> getAllPlayers();

  Set<Player> getPlayersWhoAreConnected();

  void updatePlayers(Player... players) throws PlayerNotFoundException, UnknownGameException;

  void resetPlayers(Set<Player> playerLobby) throws UnknownGameException;

  void playerConnected(Player player) throws UsernameAlreadyInUseException, UnknownGameException;

  void setPlayerForSelf(Player player);

  Player getPlayerForSelf() throws IOException;

  Set<Player> getPlayersNotRefreshedInLast(long timeout);

  void refreshPlayerTimestamp(Player player, SendPlayerUpdateCallback callback)
      throws PlayerNotFoundException;

  boolean allPlayersHaveABusiness();

  boolean isFull(GameConfig gameConfig);
}
