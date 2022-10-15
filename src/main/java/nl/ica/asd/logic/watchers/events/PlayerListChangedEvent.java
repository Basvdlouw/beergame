package nl.ica.asd.logic.watchers.events;

import java.util.Set;
import nl.ica.asd.logic.domain.Player;

public class PlayerListChangedEvent extends Event {

  public Set<Player> getPlayers() {
    return players;
  }

  private final Set<Player> players;

  public PlayerListChangedEvent(Set<Player> players) {
    this.players = players;
  }
}
