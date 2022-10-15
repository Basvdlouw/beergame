package nl.ica.asd.frontend.gui.service;

import java.util.Observable;
import javax.enterprise.context.ApplicationScoped;
import nl.ica.asd.frontend.gui.service.dto.PlayTurnState;
import nl.ica.asd.frontend.gui.service.dto.PlayerLobbyState;
import nl.ica.asd.logic.postgameprocessor.dto.BusinessRoundState;
import nl.ica.asd.network.communication.gameinitialisation.ConnectionResult;
import nl.ica.asd.network.playerlobby.PlayerLobby;

@ApplicationScoped
public class SimpleGuiService extends Observable implements GuiService {

  @Override
  public void updatePlayTurnState(PlayTurnState playTurnState) {
    setChanged();
    notifyObservers(playTurnState);
  }

  @Override
  public void updatePlayerLobbyState(PlayerLobbyState playerLobbyState) {
    setChanged();
    notifyObservers(playerLobbyState);
  }

  @Override
  public void updatePlayerLobby(PlayerLobby playerLobby) {
    setChanged();
    notifyObservers(playerLobby);
  }

  @Override
  public void updateEvaluateGameBusiness(BusinessRoundState businessRoundState) {
    setChanged();
    notifyObservers(businessRoundState);
  }

  @Override
  public void updateJoinIsConnected(ConnectionResult connectionResult) {
    setChanged();
    notifyObservers(connectionResult);
  }

}
