package nl.ica.asd.network.communication.twophasecommit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.UUID;
import nl.ica.asd.agenthandler.AgentHandler;
import nl.ica.asd.agenthandler.exceptions.AgentException;
import nl.ica.asd.logic.domain.GameState;
import nl.ica.asd.logic.domain.actions.OrderAction;
import nl.ica.asd.logic.gamemanager.GameManager;
import nl.ica.asd.network.communication.DataTransferObjectGenerator;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.logentrydatatypes.BusinessUpdate;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.logentrydatatypes.GameStateUpdate;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.logentrydatatypes.OrderUpdate;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.logentrydatatypes.PlayerUpdate;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.logentrydatatypes.UpdateType;
import nl.ica.asd.network.communication.gameinitialisation.InitializeGame;
import nl.ica.asd.network.playerlobby.ConnectedPlayers;
import nl.ica.asd.network.playerlobby.PlayerLobby;
import nl.ica.asd.network.playerlobby.exceptions.PlayerNotFoundException;
import nl.ica.asd.network.playerlobby.exceptions.UsernameAlreadyInUseException;
import nl.ica.asd.storage.exception.UnknownGameException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

class LogEntryProcessorTest {

  @InjectMocks
  private LogEntryProcessor logEntryProcessor;

  private ConnectedPlayers connectedPlayers;
  private GameManager gameManager;
  private AgentHandler agentHandler;
  private InitializeGame initializeGame;

  @BeforeEach
  void setup() {
    connectedPlayers = spy(new PlayerLobby(null, null, null, null, null));
    gameManager = mock(GameManager.class);
    agentHandler = mock(AgentHandler.class);
    initializeGame = mock(InitializeGame.class);
    logEntryProcessor = new LogEntryProcessor(connectedPlayers, gameManager, agentHandler,
        initializeGame);
    MockitoAnnotations.initMocks(this);
  }

  @Test
  void updatePlayer_Should_BeCalled_When_PlayerUpdateIsPassedTrough()
      throws PlayerNotFoundException, UnknownGameException {
    PlayerUpdate entry = DataTransferObjectGenerator.getPlayerUpdate(1, 0);
    doNothing().when(connectedPlayers).updatePlayers(any());
    logEntryProcessor.update(entry);
    verify(connectedPlayers).updatePlayers(entry.getPlayer());
  }

  @Test
  void playerConnected_Should_BeCalled_When_PlayerUpdateIsPassedTroughAndPlayerDoesntExist()
      throws PlayerNotFoundException, UsernameAlreadyInUseException, UnknownGameException {
    PlayerUpdate playerupdate = DataTransferObjectGenerator.getPlayerUpdate(1, 0);
    doThrow(PlayerNotFoundException.class).when(connectedPlayers).updatePlayers(any());
    doNothing().when(connectedPlayers).playerConnected(((playerupdate).getPlayer()));
    logEntryProcessor.update(playerupdate);
    verify(connectedPlayers).playerConnected(((playerupdate).getPlayer()));
  }

  @Test
  void playerConnected_Should_GiveAnException_When_PlayerAlreadyExists()
      throws PlayerNotFoundException, UsernameAlreadyInUseException, UnknownGameException {

    PlayerUpdate playerupdate = DataTransferObjectGenerator.getPlayerUpdate(1, 0);

    doThrow(PlayerNotFoundException.class).when(connectedPlayers).updatePlayers(any());
    doThrow(UsernameAlreadyInUseException.class).when(connectedPlayers).playerConnected(any());
    logEntryProcessor.update(playerupdate);
    assertThrows(UsernameAlreadyInUseException.class,
        () -> connectedPlayers.playerConnected((playerupdate).getPlayer()));
  }

  @Test
  void saveCurrentOrderActions_Should_BeCalled_When_OrderUpdateIsPassedTrough()
      throws UnknownGameException {
    ArgumentCaptor<ArrayList<OrderAction>> argument = ArgumentCaptor.forClass(ArrayList.class);
    OrderUpdate orderUpdate = DataTransferObjectGenerator.getOrderUpdate(1, 0);
    doNothing().when(gameManager).saveCurrentOrderActions(any());
    logEntryProcessor.update(orderUpdate);
    verify(gameManager).saveCurrentOrderActions(argument.capture());
    assertEquals(orderUpdate.getOrder(), argument.getValue().get(0));
  }

  @Test
  void update_should_callUpdateGameStateInGameManager_when_logEntryIsGameStateUpdate()
      throws UnknownGameException {
    GameStateUpdate entry = new GameStateUpdate(1, 1, UpdateType.SET, GameState.STARTED,
        UUID.nameUUIDFromBytes("1".getBytes()));

    logEntryProcessor.update(entry);

    verify(gameManager, times(1)).updateGameState(GameState.STARTED);
  }

  @Test
  void update_should_updateBusinessRules() throws AgentException, UnknownGameException {
    BusinessUpdate entry = DataTransferObjectGenerator.getBusinessUpdate(1, 0);

    logEntryProcessor.update(entry);

    verify(agentHandler, times(1)).saveBusinessRules(entry.getBusinessRules());
  }

  @Test
  void update_should_retryUpdateIfItFails() throws UnknownGameException, AgentException {
    BusinessUpdate entry = DataTransferObjectGenerator.getBusinessUpdate(1, 0);

    doThrow(new AgentException("")).when(agentHandler).saveBusinessRules(entry.getBusinessRules());

    logEntryProcessor.update(entry);

    verify(agentHandler, times(2)).saveBusinessRules(entry.getBusinessRules());
  }
}