package nl.ica.asd.network.communication.interfaces;

import java.util.Set;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.network.failurehandler.LeaderElection;

public interface SendHeartbeat {

  void sendHeartbeat(Set<Player> targetsForHeartbeat, LeaderElection leaderElection);

}
