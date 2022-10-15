package nl.ica.asd.network.failurehandler;

import static org.mockito.Mockito.after;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class HeartbeatTimerTest {

  private static final int INITIAL_DELAY = 0;
  private static final int DELAY = 1000;

  @Mock
  private FailureHandler failureHandler;

  @BeforeEach
  void setUp() {
    failureHandler = mock(FailureHandler.class);
  }

  @Test
  void startTimeout_should_call_sendHeartbeat_in_FailureHandler() {

    HeartbeatTimer heartbeatTimer = new HeartbeatTimer(failureHandler);

    verify(failureHandler, after(INITIAL_DELAY + 100).atLeastOnce()).sendHeartbeat();
  }

  @Test
  void startTimeout_should_call_sendHeartbeat_in_FailureHandler_multiple_times_after_delays() {

    HeartbeatTimer heartbeatTimer = new HeartbeatTimer(failureHandler);

    verify(failureHandler, after(INITIAL_DELAY + (DELAY * 3)).atLeast(3)).sendHeartbeat();
  }
}
