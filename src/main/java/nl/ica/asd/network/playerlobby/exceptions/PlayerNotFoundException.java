package nl.ica.asd.network.playerlobby.exceptions;

import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.network.failurehandler.exceptions.UnknownNodeException;

public class PlayerNotFoundException extends UnknownNodeException {

  private final Player player;

  public PlayerNotFoundException(Player player) {
    super(player.getIP());
    this.player = player;
  }

  public Player getPlayer() {
    return player;
  }
}
