package nl.ica.asd.logic.domain;

import java.util.UUID;
import nl.ica.asd.util.ComparableTester;
import nl.ica.asd.util.ObjectTester;
import org.junit.jupiter.api.Test;

class PlayerTest {

  @Test
  void runObjectTesterOnPlayer() {
    new ObjectTester<>(
        new Player("Player 1", new UUID(0, 1), BusinessType.NOT_SET,
            PlayerStatus.CONNECTED_AND_ACTIVE, "localhost", 1234),
        new Player("Player 1", new UUID(0, 1), BusinessType.NOT_SET,
            PlayerStatus.CONNECTED_AND_ACTIVE, "localhost", 1234),
        new Player("Player 2", new UUID(0, 1), BusinessType.NOT_SET,
            PlayerStatus.CONNECTED_AND_ACTIVE, "localhost", 1234)
    ).testEquals().testHashCode().testToString();
  }

  @Test
  void runComparableTesterOnPlayer() {
    new ComparableTester<>(
        new Player("Player ABC", new UUID(0, 1), BusinessType.NOT_SET,
            PlayerStatus.CONNECTED_AND_ACTIVE, "localhost", 1234),
        new Player("Player ABC", new UUID(0, 1), BusinessType.NOT_SET,
            PlayerStatus.CONNECTED_AND_ACTIVE, "localhost", 1234)
    ).testCompareTo(
        new Player("Player AAC", new UUID(0, 1), BusinessType.NOT_SET,
            PlayerStatus.CONNECTED_AND_ACTIVE, "localhost", 1234),
        new Player("Player BBC", new UUID(0, 1), BusinessType.NOT_SET,
            PlayerStatus.CONNECTED_AND_ACTIVE, "localhost", 1234)
    );
  }
}