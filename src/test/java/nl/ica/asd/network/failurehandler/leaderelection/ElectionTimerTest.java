package nl.ica.asd.network.failurehandler.leaderelection;

import static org.mockito.Mockito.after;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class ElectionTimerTest {

  private static final int DELAY = 3000;
  private ElectionTimer electionTimer;

  @Mock
  private ElectionController electionController;

  @BeforeEach
  void setUp() {
    electionController = mock(ElectionController.class);
    electionTimer = new ElectionTimer(electionController);
  }

  @Test
  void startTimeout_should_call_sendHeartbeat_in_FailureHandler() throws IOException {

    electionTimer.startAndResetTimeout();

    verify(electionController, after((DELAY * 2) + 1000).atLeastOnce()).startElection();
  }

  @Test
  void startTimeout_should_not_call_sendHeartbeat_in_FailureHandler_when_timer_gets_reset()
      throws IOException {

    electionTimer.startAndResetTimeout();

    verify(electionController, after(DELAY).never()).startElection();

    electionTimer.startAndResetTimeout();

    verify(electionController, after(DELAY).never()).startElection();
  }

  @Test
  void startTimeout_should_call_sendHeartbeat_in_FailureHandler_when_timer_gets_reset_and_runs_out()
      throws IOException {

    electionTimer.startAndResetTimeout();

    verify(electionController, after(DELAY).never()).startElection();

    electionTimer.startAndResetTimeout();

    verify(electionController, after((DELAY * 2) + 1000).atLeastOnce()).startElection();
  }

  @AfterEach
  void afterEach() {
    electionTimer.stopTimeout();
  }
}
