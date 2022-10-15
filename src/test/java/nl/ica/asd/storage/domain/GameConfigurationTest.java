package nl.ica.asd.storage.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import nl.ica.asd.logic.domain.GameVisibilityType;
import nl.ica.asd.logic.domain.SupplyChainType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class GameConfigurationTest {

  private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private final static GameConfiguration CONFIGURATION = new GameConfiguration(10, 10,
      SupplyChainType.LINEAR, 0, GameVisibilityType.VISIBLE, null);
  private final static String FIXTURE_PATH =
      "fixtures" + File.separator + "game-configuration.json";

  @Test
  public void when_serializingToJSON_should_equalExpectedString() throws IOException {
    final ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    final InputStream inputStream = classloader
        .getResourceAsStream(FIXTURE_PATH);

    final String expected = OBJECT_MAPPER.writeValueAsString(
        OBJECT_MAPPER.readValue(inputStream, GameConfiguration.class));
    Assertions.assertEquals(OBJECT_MAPPER.writeValueAsString(CONFIGURATION), expected);
  }

  @Test
  public void when_deserializingFromJSON_should_equalGameDataObject() throws IOException {
    final ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    final InputStream inputStream = classloader
        .getResourceAsStream(FIXTURE_PATH);

    Assertions
        .assertEquals(OBJECT_MAPPER.readValue(inputStream, GameConfiguration.class).getMaxPlayers(),
            CONFIGURATION.getMaxPlayers());
  }
}