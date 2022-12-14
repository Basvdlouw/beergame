package nl.ica.asd.logic.postgameprocessor.dto;

import java.util.Date;

public class PlayedGame {

  private String name;

  private Date date;

  public PlayedGame(String name, Date date) {
    this.name = name;
    this.date = date;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }
}
