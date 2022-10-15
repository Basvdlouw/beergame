package nl.ica.asd.storage.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

public class GameData {

  private final String name;
  private final Date date;
  private final GameConfiguration settings;

  @JsonCreator
  public GameData(@JsonProperty("name") String name, @JsonProperty("date") Date date,
      @JsonProperty("settings") GameConfiguration settings) {
    this.name = name;
    this.date = date;
    this.settings = settings;
  }

  public String getName() {
    return name;
  }

  public Date getDate() {
    return date;
  }

  public GameConfiguration getSettings() {
    return settings;
  }
}
