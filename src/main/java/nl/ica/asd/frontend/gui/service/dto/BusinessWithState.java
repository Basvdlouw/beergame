package nl.ica.asd.frontend.gui.service.dto;

import java.util.UUID;
import nl.ica.asd.logic.postgameprocessor.dto.BusinessRoundState;

public class BusinessWithState {

  private UUID uuid;
  private BusinessRoundState state;
  private int round;

  public BusinessWithState(UUID uuid, BusinessRoundState state, int round) {
    this.uuid = uuid;
    this.state = state;
    this.round = round;
  }

  public UUID getUuid() {
    return uuid;
  }

  public BusinessRoundState getState() {
    return state;
  }

  public int getRound() {
    return round;
  }
}
