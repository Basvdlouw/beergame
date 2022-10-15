package nl.ica.asd.network.failurehandler.leaderelection;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ElectionTimer {

  private static final Logger logger = LoggerFactory.getLogger(ElectionTimer.class);

  private static final int DELAY = 3000;
  private final ScheduledExecutorService schedulerTimeout = Executors.newScheduledThreadPool(1);
  private ScheduledFuture<?> future;
  private final ElectionController electionController;
  private final Random rnd = new Random();

  ElectionTimer(ElectionController electionController) {
    this.electionController = electionController;
  }

  public void startAndResetTimeout() {
    stopTimeout();

    int timeoutDuration =
        DELAY + (rnd.nextInt(DELAY));
    Runnable startElection = () -> {
      try {
        electionController.startElection();
      } catch (IOException e) {
        logger.error("Failed to start Leader election", e.getCause());
      }
    };
    future = schedulerTimeout.schedule(startElection, timeoutDuration, TimeUnit.MILLISECONDS);
  }

  public void stopTimeout() {
    if (future != null) {
      future.cancel(true);
    }
  }

}
