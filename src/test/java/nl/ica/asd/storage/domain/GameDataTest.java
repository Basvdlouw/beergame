package nl.ica.asd.storage.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import nl.ica.asd.logic.domain.GameVisibilityType;
import nl.ica.asd.logic.domain.SupplyChainType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameDataTest {

  private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private final static String FIXTURE_PATH = "fixtures" + File.separator + "game-data.json";

  private GameConfiguration configuration;

  @BeforeEach
  public void setup() {
    configuration = new GameConfiguration(10, 10, SupplyChainType.LINEAR, 0,
        GameVisibilityType.VISIBLE, null);
  }

  @Test
  public void when_serializingToJSON_should_equalExpectedString() throws IOException {
    final java.util.Date date = new Date(1544569200000L);
    final GameData data = new GameData("FEM BeerGame", date, configuration);

    final ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    final InputStream inputStream = classloader
        .getResourceAsStream(FIXTURE_PATH);

    final String expected = OBJECT_MAPPER.writeValueAsString(
        OBJECT_MAPPER.readValue(inputStream, GameData.class));
    Assertions.assertEquals(OBJECT_MAPPER.writeValueAsString(data), expected);
  }

  @Test
  public void when_deserializingFromJSON_should_equalGameDataObject() throws IOException {
    final java.util.Date date = new Date(1544569200000L);
    final GameData data = new GameData("FEM BeerGame", date, configuration);

    final ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    final InputStream inputStream = classloader
        .getResourceAsStream(FIXTURE_PATH);

    Assertions.assertEquals(OBJECT_MAPPER.readValue(inputStream, GameData.class).getName(),
        data.getName());
  }
}
