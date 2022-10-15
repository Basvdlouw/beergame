package nl.ica.asd.network.failurehandler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class HeartbeatTimer {

  private static final int INITIAL_DELAY = 0;
  private static final int DELAY = 100;

  private final ScheduledExecutorService scheduledHeartbeat = Executors.newScheduledThreadPool(1);

  private final FailureHandler failureHandler;

  HeartbeatTimer(FailureHandler failureHandler) {
    this.failureHandler = failureHandler;
    startTimeOut();
  }

  private void startTimeOut() {
    Runnable sendHeartbeat = failureHandler::sendHeartbeat;
    scheduledHeartbeat
        .scheduleAtFixedRate(sendHeartbeat, INITIAL_DELAY, DELAY, TimeUnit.MILLISECONDS);
  }

  public void stopTimer() {
    scheduledHeartbeat.shutdown();
  }
}
