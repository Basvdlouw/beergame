package nl.ica.asd.logic.domain.businesses;

import java.util.HashMap;
import java.util.UUID;
import nl.ica.asd.logic.domain.Agent;
import nl.ica.asd.logic.domain.BusinessType;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.logic.domain.PlayerStatus;
import nl.ica.asd.util.ComparableTester;
import nl.ica.asd.util.ObjectTester;
import org.junit.jupiter.api.Test;

class FactoryBusinessTest {

//  @Test
//  void runObjectTesterOnFactoryBusiness() {
//    Player player = new Player("player", new UUID(0, 0), BusinessType.FACTORY,
//        PlayerStatus.CONNECTED_AND_ACTIVE, "localhost", 1234);
//    new ObjectTester<>(
//        new FactoryBusiness(0, 0, new HashMap<>(), new Agent(""), new UUID(0, 1), player, UUID.randomUUID()),
//        new FactoryBusiness(0, 0, new HashMap<>(), new Agent(""), new UUID(0, 1), player, UUID.randomUUID()),
//        new FactoryBusiness(0, 0, new HashMap<>(), new Agent(""), new UUID(0, 2), player, UUID.randomUUID())
//    ).testEquals().testHashCode().testToString();
//  }

  @Test
  void runComparableTesterOnFactoryBusiness() {
    Player player = new Player("player", new UUID(0, 0), BusinessType.FACTORY,
        PlayerStatus.CONNECTED_AND_ACTIVE, "localhost", 1234);
    new ComparableTester<>(
        new FactoryBusiness(0, 0, new HashMap<>(), new Agent(""), new UUID(0, 2), player, UUID.randomUUID()),
        new FactoryBusiness(0, 0, new HashMap<>(), new Agent(""), new UUID(0, 2), player, UUID.randomUUID())
    ).testCompareTo(
        new FactoryBusiness(0, 0, new HashMap<>(), new Agent(""), new UUID(0, 1), player, UUID.randomUUID()),
        new FactoryBusiness(0, 0, new HashMap<>(), new Agent(""), new UUID(0, 3), player, UUID.randomUUID())
    );
  }
}