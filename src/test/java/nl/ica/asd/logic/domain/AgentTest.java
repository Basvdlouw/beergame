package nl.ica.asd.logic.domain;

import nl.ica.asd.util.ObjectTester;
import org.junit.jupiter.api.Test;

class AgentTest {

  @Test
  void runObjectTesterOnAgent() {
    new ObjectTester<>(
        new Agent("order = 5"),
        new Agent("order = 5"),
        new Agent("order = 10")
    ).testEquals().testHashCode().testToString();
  }
}