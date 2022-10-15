package nl.ica.asd.logic.domain.businesses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import nl.ica.asd.logic.domain.Agent;
import nl.ica.asd.logic.domain.BusinessType;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.logic.domain.PlayerStatus;
import nl.ica.asd.util.ObjectTester;
import org.junit.jupiter.api.Test;

class RetailBusinessTest {

  @Test
  void runObjectTesterOnRetailBusiness() {
    Player player = new Player("player", new UUID(0, 0), BusinessType.RETAIL,
        PlayerStatus.CONNECTED_AND_ACTIVE, "localhost", 1234);
    new ObjectTester<>(
        new RetailBusiness(0, 0, new HashMap<>(), new Agent(""), new UUID(0, 1), player,
            new ArrayList<>(), UUID.randomUUID()),
        new RetailBusiness(0, 0, new HashMap<>(), new Agent(""), new UUID(0, 1), player,
            new ArrayList<>(), UUID.randomUUID()),
        new RetailBusiness(0, 0, new HashMap<>(), new Agent(""), new UUID(0, 2), player,
            new ArrayList<>(), UUID.randomUUID())
    ).testEquals().testHashCode().testToString();
  }
}