package nl.ica.asd.logic.gamemanager.gamestatemanager;

import static nl.ica.asd.util.MockitoHelpers.once;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import nl.ica.asd.agenthandler.AgentHandler;
import nl.ica.asd.agenthandler.exceptions.AgentException;
import nl.ica.asd.logic.domain.BeerGame;
import nl.ica.asd.logic.domain.BusinessType;
import nl.ica.asd.logic.domain.GameState;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.logic.domain.PlayerStatus;
import nl.ica.asd.logic.gamemanager.ordermanager.OrderManager;
import nl.ica.asd.logic.statecalculator.StateCalculator;
import nl.ica.asd.logic.watchers.NextRoundStartedWatcher;
import nl.ica.asd.logic.watchers.Watcher;
import nl.ica.asd.logic.watchers.events.Event;
import nl.ica.asd.logic.watchers.events.NextRoundStartedEvent;
import nl.ica.asd.network.playerlobby.PlayerLobby;
import nl.ica.asd.storage.exception.UnknownGameException;
import nl.ica.asd.storage.gamedatastorage.GameDataAccess;
import nl.ica.asd.util.TestHelpers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SetupGameStateManagerTest {

  @Mock
  StateCalculator stateCalculator;
  @Mock
  GameDataAccess gameDataAccess;
  @Mock
  AgentHandler agentHandler;
  @Mock
  PlayerLobby playerLobby;
  @Mock
  OrderManager orderManager;

  @InjectMocks
  private SetupGameStateManager setupGameStateManager;

//  @Test
//  void updateGameState_should_throwAgentException_when_noDefaultBusinessRulesSet()
//      throws UnknownGameException, AgentException, IOException {
//    final BeerGame beerGame = TestHelpers.createTestingBeerGame();
//    final GameState state = GameState.SETUP;
//    final Set<Player> players = new HashSet<>();
//
//    final Player player = new Player("FEM Student", UUID.randomUUID(), BusinessType.FACTORY,
//        PlayerStatus.CONNECTED_AND_ACTIVE, "1",
//        1);
//
//    players.add(player);
//    when(playerLobby.getAllPlayers()).thenReturn(players);
//    when(playerLobby.getPlayerForSelf()).thenReturn(player);
//    when(gameDataAccess.getBeerGame()).thenReturn(beerGame);
//
//    setupGameStateManager.updateGameState(state);
//    verify(playerLobby, once()).getAllPlayers();
//  }

//  @Test
//  void updateGameState_should_generateFirstRoundInfo_when_gameStateIsSetup()
//      throws UnknownGameException, IOException {
//    final BeerGame beerGame = TestHelpers.createTestingBeerGame();
//    final GameState state = GameState.SETUP;
//    final Set<Player> players = new HashSet<>();
//
//    final Player player = new Player("FEM Student", UUID.randomUUID(), BusinessType.FACTORY,
//        PlayerStatus.CONNECTED_AND_ACTIVE, "1",
//        1);
//
//    players.add(player);
//
//    when(playerLobby.getAllPlayers()).thenReturn(players);
//    when(playerLobby.getPlayerForSelf()).thenReturn(player);
//    when(gameDataAccess.getBeerGame()).thenReturn(beerGame);
//
//    setupGameStateManager.updateGameState(state);
//
//    verify(playerLobby, once()).getAllPlayers();
//    verify(gameDataAccess, once()).getBeerGame();
//    verify(gameDataAccess, once()).setCurrentGame(any(BeerGame.class));
//    verify(gameDataAccess, once()).saveBeerGameSettings(any(BeerGame.class));
//  }

  @Test
  void isOwnWatcher_shouldReturnTrue_when_watcherIsGameFinishedWatcher() {
    Watcher watcher = new MockWatcher();
    assertTrue(setupGameStateManager.isOwnWatcher(watcher));
  }

  private class MockWatcher implements NextRoundStartedWatcher {

    @Override
    public void subscribeAll() {
    }

    @Override
    public void unSubscribeAll() {
    }

    @Override
    public <E extends Event> void onEvent(E event) {
    }

    @Override
    public void onNextRoundStarted(NextRoundStartedEvent event) {

    }
  }
}
