package nl.ica.asd.logic.watchers.events;

import java.util.List;
import nl.ica.asd.frontend.gui.service.dto.PlayTurnState;
import nl.ica.asd.logic.domain.Business;

public class NextRoundStartedEvent extends Event {

  private final List<Business> businesses;
  private final PlayTurnState playTurnState;


  public NextRoundStartedEvent(List<Business> businesses, PlayTurnState playTurnState) {
    this.playTurnState = playTurnState;
    this.businesses = businesses;
  }

  public List<Business> getBusinesses() {
    return businesses;
  }

  public PlayTurnState getPlayTurnState() {
    return playTurnState;
  }

}
