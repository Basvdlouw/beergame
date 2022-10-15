package nl.ica.asd.logic.gamemanager;

import static nl.ica.asd.util.MockitoHelpers.once;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import nl.ica.asd.logic.gamemanager.gamestatemanager.FinishedGameStateManager;
import nl.ica.asd.logic.gamemanager.gamestatemanager.GameStateManagerFactory;
import nl.ica.asd.logic.gamemanager.ordermanager.OrderManager;
import nl.ica.asd.logic.gamemanager.playermanager.PlayerManager;
import nl.ica.asd.storage.exception.UnknownGameException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GameManagerHandlerTest {

  @Mock
  private GameStateManagerFactory gameStateManagerFactory;
  @Mock
  private FinishedGameStateManager finishedGameStateManager;
  @Mock
  private OrderManager orderManager;
  @Mock
  private PlayerManager playerManager;

  @InjectMocks
  private GameManagerHandler gameManagerHandler;

  @Test
  void placeOrder_should_callOrderManager() {
    gameManagerHandler.placeOrder("username", 1);
    verify(orderManager, once()).placeOrder(anyString(), anyInt());
  }

  @Test
  void saveCurrentOrderActions_should_callOrderManager() throws UnknownGameException {
    gameManagerHandler.saveCurrentOrderActions(Collections.emptyList());
    verify(orderManager, once()).saveCurrentOrderActions(anyList());
  }

  @Test
  void getAllActionsBusiness_should_callOrderManager() throws UnknownGameException {
    gameManagerHandler.getAllActionsBusiness(any());
    verify(orderManager, once()).getAllActionsBusiness(any());
  }

  @Test
  void saveActions_should_callOrderManager() throws UnknownGameException {
    gameManagerHandler.saveActions(anyMap());
    verify(orderManager, once()).saveActions(anyMap());
  }

  @Test
  void setPlayerList_should_callPlayerManager() throws UnknownGameException {
    gameManagerHandler.setPlayerList(anySet());
    verify(playerManager, once()).setPlayerList(anySet());
  }

  @Test
  void updateGameState_should_callGameStateFactory() throws UnknownGameException {
    when(gameStateManagerFactory.getGameStateManager(any())).thenReturn(finishedGameStateManager);
    gameManagerHandler.updateGameState(any());
    verify(gameStateManagerFactory, once()).getGameStateManager(any());
    verify(finishedGameStateManager, once()).updateGameState(any());
  }
}
