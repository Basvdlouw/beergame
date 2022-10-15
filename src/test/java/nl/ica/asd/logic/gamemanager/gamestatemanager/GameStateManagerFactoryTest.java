package nl.ica.asd.logic.gamemanager.gamestatemanager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import nl.ica.asd.logic.domain.GameState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

//import javax.annotation.PostConstruct;
//import javax.enterprise.context.ApplicationScoped;
//import javax.enterprise.inject.Instance;
//import javax.enterprise.util.AnnotationLiteral;
//import javax.inject.Inject;
//import javax.inject.Named;
//import nl.ica.asd.logic.domain.GameState;
//
//@ApplicationScoped
@ExtendWith(MockitoExtension.class)
public class GameStateManagerFactoryTest {

  @Mock
  private SetupGameStateManager setupManager;
  @Mock
  private StartedGameStateManager startedManager;
  @Mock
  private FinishedGameStateManager finishedManager;

  @InjectMocks
  private GameStateManagerFactory gameStateManagerFactory;

  @Test
  void getGameStateManager_shouldReturnSetupManager_whenGameStateIsSetup() {
    assertEquals(setupManager, gameStateManagerFactory.getGameStateManager(GameState.SETUP));
  }

  @Test
  void getGameStateManager_shouldReturnStartedManager_whenGameStateIsStarted() {
    assertEquals(startedManager, gameStateManagerFactory.getGameStateManager(GameState.STARTED));
  }

  @Test
  void getGameStateManager_shouldReturnFinishedManager_whenGameStateIsFinished() {
    assertEquals(finishedManager, gameStateManagerFactory.getGameStateManager(GameState.FINISHED));
  }

  @Test
  void getGameStateManager_shouldThrowNullPointerException_whenGameStateIsNotSet() {
    assertThrows(NullPointerException.class,
        () -> gameStateManagerFactory.getGameStateManager(null));
  }

}