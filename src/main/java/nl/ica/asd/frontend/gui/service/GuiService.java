package nl.ica.asd.frontend.gui.service;

import java.util.Observer;
import nl.ica.asd.frontend.gui.service.dto.PlayTurnState;
import nl.ica.asd.frontend.gui.service.dto.PlayerLobbyState;
import nl.ica.asd.logic.postgameprocessor.dto.BusinessRoundState;
import nl.ica.asd.network.communication.gameinitialisation.ConnectionResult;
import nl.ica.asd.network.playerlobby.PlayerLobby;


public interface GuiService {

  void addObserver(Observer observer);

  void deleteObservers();

  void updatePlayTurnState(PlayTurnState playTurnState);

  void updatePlayerLobbyState(PlayerLobbyState playerLobbyState);

  void updatePlayerLobby(PlayerLobby playerLobby);

  void updateEvaluateGameBusiness(BusinessRoundState businessRoundState);

  void updateJoinIsConnected(ConnectionResult connectionResult);
}
