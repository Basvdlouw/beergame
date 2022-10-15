package nl.ica.asd.logic.gamemanager.ordermanager;

import static nl.ica.asd.util.MockitoHelpers.once;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import nl.ica.asd.logic.domain.Action;
import nl.ica.asd.logic.domain.BeerGame;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.logic.domain.actions.OrderAction;
import nl.ica.asd.logic.gamemanager.gamestatemanager.GameStateManagerFactory;
import nl.ica.asd.logic.gamemanager.gamestatemanager.StartedGameStateManager;
import nl.ica.asd.logic.statecalculator.StateCalculator;
import nl.ica.asd.logic.watchers.OrderPlacedWatcher;
import nl.ica.asd.logic.watchers.Watcher;
import nl.ica.asd.logic.watchers.events.Event;
import nl.ica.asd.logic.watchers.events.OrderPlacedEvent;
import nl.ica.asd.network.communication.interfaces.SendGameStateUpdate;
import nl.ica.asd.network.communication.interfaces.SendOrder;
import nl.ica.asd.network.playerlobby.PlayerLobby;
import nl.ica.asd.storage.exception.UnknownBusinessException;
import nl.ica.asd.storage.exception.UnknownGameException;
import nl.ica.asd.storage.gamedatastorage.GameDataAccess;
import nl.ica.asd.util.TestHelpers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DefaultOrderManagerTest {

  @Mock
  private SendOrder sendOrder;
  @Mock
  private GameStateManagerFactory gameStateManagerFactory;
  @Mock
  private StartedGameStateManager startedGameStateManager;
  @Mock
  private GameDataAccess gameDataAccess;
  @Mock
  private PlayerLobby playerLobby;
  @Mock
  private StateCalculator stateCalculator;
  @Mock
  private SendGameStateUpdate sendGameStateUpdate;

  @InjectMocks
  private DefaultOrderManager defaultOrderManager;

  private BeerGame beerGame;
  private Set<Player> players;

  @BeforeEach
  public void setup() {
    beerGame = TestHelpers.createTestingBeerGame();
    players = beerGame.getPlayers();
  }

  @Test
  void placeOrder_should_placeOrder_when_playerFound() throws UnknownGameException {
    final Player player = beerGame.getBusinesses().get(1).getPlayer();
    players.add(player);
    when(gameDataAccess.getPlayerList()).thenReturn(players);
    when(gameDataAccess.getBeerGame()).thenReturn(beerGame);
    defaultOrderManager.placeOrder(player.getUsername(), 90);
    verify(sendOrder, once()).sendOrder(any(OrderAction.class));
  }

  @Test
  void placeOrder_should_notSendOrder_when_playerFoundButNoGameFound() throws UnknownGameException {
    when(gameDataAccess.getPlayerList()).thenReturn(players);
    defaultOrderManager.placeOrder("UNKNOWN username", 90);
    verify(sendOrder, never()).sendOrder(any(OrderAction.class));
  }

  @Test
  void placeOrder_should_sendOrder() throws UnknownGameException {
    final Player player = beerGame.getBusinesses().get(2).getPlayer();
    when(gameDataAccess.getPlayerList()).thenReturn(players);
    when(gameDataAccess.getBeerGame()).thenReturn(beerGame);
    defaultOrderManager.placeOrder(player.getUsername(), 90);
    verify(sendOrder, once()).sendOrder(any(OrderAction.class));
  }

  @Test
  void placeOrder_should_throwUnknownGameExceptionAndThereforNotSendOrder_when_beerGameSet()
      throws UnknownGameException {
    when(gameDataAccess.getPlayerList()).thenReturn(players);
    when(gameDataAccess.getBeerGame()).thenThrow(UnknownGameException.class);
    defaultOrderManager.placeOrder(players.iterator().next().getUsername(), 99);
    verify(sendOrder, never()).sendOrder(any(OrderAction.class));
  }

  @Test
  void saveCurrentOrderActions_should_notSaveOrderActions_when_ordersAreNullOrEmpty()
      throws UnknownGameException {
    DefaultOrderManager spy = spy(defaultOrderManager);
    doNothing().when(spy).onEvent(any());
    spy.saveCurrentOrderActions(null);
    spy.saveCurrentOrderActions(Collections.emptyList());
    verify(gameDataAccess, never()).saveCurrentActions(null, 1);
  }

//  @Test
//  void saveCurrentOrderActions_should_saveOrderActions() throws UnknownGameException {
//    final BeerGame startingBeergame = BeerGameBuilder
//        .fromBaseBeerGame(TestHelpers.createTestingBeerGame())
//        .withCurrentRound(1).build();
//
//    when(gameDataAccess.getBeerGame()).thenReturn(startingBeergame);
//    when(gameDataAccess.getCurrentRound()).thenReturn(1);
//    when(gameStateManagerFactory.getGameStateManager(GameState.STARTED))
//        .thenReturn(startedGameStateManager);
//
//    defaultOrderManager.saveCurrentOrderActions(
//        startingBeergame.getBusinesses().get(0).getActionsPerRound().get(1));
//    verify(gameDataAccess, once()).saveCurrentActions(anyList(), anyInt());
//  }

  @Test
  void getAllActionBusiness_should_returnMapOfActions()
      throws UnknownBusinessException, UnknownGameException {
    final Map<Integer, List<Action>> factoryActions = beerGame.getBusinesses().get(0)
        .getActionsPerRound();
    when(gameDataAccess.getAllActionsPerBusiness(any())).thenReturn(factoryActions);
    final Map<Integer, List<Action>> actualActions = defaultOrderManager
        .getAllActionsBusiness(any());
    assertEquals(factoryActions, actualActions);
    verify(gameDataAccess, once()).getAllActionsPerBusiness(any());
  }

  @Test
  void getAllActionBusiness_should_returnNullOnException()
      throws UnknownBusinessException, UnknownGameException {
    when(gameDataAccess.getAllActionsPerBusiness(any())).thenThrow(UnknownBusinessException.class);
    final Map<Integer, List<Action>> actualActions = defaultOrderManager
        .getAllActionsBusiness(any());
    assertNull(actualActions);
    verify(gameDataAccess, once()).getAllActionsPerBusiness(any());
  }

//  @Test
//  void checkOrderProgress_should_notCallNextGameState_when_notEveryPlayerHasPlacedAnOrder()
//      throws UnknownGameException, IOException, UnknownBusinessException {
//    final Business businessToReplace = beerGame.getBusinesses().get(0);
//    final Business businessWithoutOrders = businessToReplace.getBusinessType()
//        .getBuilderFromBusiness(businessToReplace)
//        .withActionsPerRound(Collections.emptyMap())
//        .build();
//
//    final List<Business> businesses = beerGame.getBusinesses();
//    businesses.remove(businessToReplace);
//    businesses.add(businessWithoutOrders);
//    final BeerGame playGame = BeerGameBuilder.fromBaseBeerGame(beerGame)
//        .withCurrentRound(1)
//        .withBusinesses(businesses)
//        .build();
//
//    when(gameDataAccess.getBeerGame()).thenReturn(playGame);
//
//    defaultOrderManager.checkOrderProgress();
//
//    verify(gameDataAccess, once()).getBeerGame();
//    verify(sendGameStateUpdate, never()).sendGameStateUpdate(any());
//    verify(gameStateManagerFactory, never()).getGameStateManager(any());
//  }


  @Test
  void checkOrderProgress_should_throwUnknownGameException_when_getBeerGameReturnsNull()
      throws UnknownGameException {
    when(gameDataAccess.getBeerGame()).thenThrow(UnknownGameException.class);
    defaultOrderManager.checkOrderProgress();
    verify(gameDataAccess, once()).getBeerGame();
    verify(sendGameStateUpdate, never()).sendGameStateUpdate(any());
    verify(gameStateManagerFactory, never()).getGameStateManager(any());
  }

  @Test
  void isOwnWatcher_shouldReturnTrue_when_watcherIsGameFinishedWatcher() {
    Watcher watcher = new MockWatcher();
    assertTrue(defaultOrderManager.isOwnWatcher(watcher));
  }

  private class MockWatcher implements OrderPlacedWatcher {

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
    public void onOrderPlaced(OrderPlacedEvent event) {

    }
  }
}
