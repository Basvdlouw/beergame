package nl.ica.asd.network.communication.twophasecommit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.LogEntry;
import nl.ica.asd.network.failurehandler.LeaderElection;
import nl.ica.asd.network.playerlobby.ConnectedPlayers;
import nl.ica.asd.storage.exception.UnknownGameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Named("LogController")
public class LogController {

  private static final Logger logger = LoggerFactory.getLogger(LogController.class);


  private final LogEntryProcessor logEntryProcessor;
  private final List<LogEntry> logEntries;
  private int commitIndex;
  private final Map<Player, Integer> playerCommitIndexes;
  private final ConnectedPlayers connectedPlayers;


  @Inject
  public LogController(LogEntryProcessor logEntryProcessor, ConnectedPlayers connectedPlayers) {
    this.logEntryProcessor = logEntryProcessor;
    this.logEntries = new ArrayList<>();
    this.commitIndex = -1;
    playerCommitIndexes = new HashMap<>();
    this.connectedPlayers = connectedPlayers;
  }

  //Completely new logEntries should have a logIndex of -1 to indicate that the logIndex still has to be set
  synchronized void synchronizeLogEntries(List<LogEntry> logEntriesToAppend) {
    for (LogEntry logEntryToAppend : logEntriesToAppend) {
      if (logEntryToAppend.getLogIndex() != -1 &&
          logEntryToAppend.getLogIndex() < getNextLogIndex()
      ) {
        if (logEntries.get(logEntryToAppend.getLogIndex()).getElectionTerm() == logEntryToAppend
            .getElectionTerm()) {
          appendLogEntry(logEntryToAppend);
        } else {
          removeLogEntriesAfter(logEntryToAppend);
          appendLogEntry(setLogIndex(logEntryToAppend));
        }
      } else {
        appendLogEntry(setLogIndex(logEntryToAppend));
      }
    }
  }

  private synchronized LogEntry setLogIndex(LogEntry logEntryToAppend) {
    return logEntryToAppend
        .cloneWithNewLogIndexAndElectionTerm(getNextLogIndex(),
            logEntryToAppend.getElectionTerm());
  }

  private synchronized void appendLogEntry(LogEntry logEntryToAppend) {
    if (getNextLogIndex() <= logEntryToAppend.getLogIndex()) {
      logEntries.add(logEntryToAppend);
    } else {
      logEntries.set(logEntryToAppend.getLogIndex(), logEntryToAppend);
    }
  }

  private synchronized void addPlayerIfNotInConnectionListYet(Player player) {
    if (!playerCommitIndexes.containsKey(player)) {
      try {
        if (player.equals(connectedPlayers.getPlayerForSelf())) {
          playerCommitIndexes.put(player, getLastLogIndex());
        } else {
          playerCommitIndexes.put(player, -1);
        }
      } catch (IOException e) {
        logger.error("Could not get player for self", e);
      }
    }
  }

  synchronized void updateCommitIndex(Player player, int latestReceivedLogIndex)
      throws UnknownGameException {
    //This method should only be called by the leader
    playerCommitIndexes.put(player, latestReceivedLogIndex);
    //Get a list of commitIndex's and sort them
    List<Integer> commitIndexes = new ArrayList<>(playerCommitIndexes.values());
    Collections.sort(commitIndexes);
    //Update the commitIndex to the highest commitIndex that more than half of the nodes agree upon
    if (commitIndexes.get(((commitIndexes.size() + 1) / 2) - 1) >= this.getCommitIndex()) {
      updateCommitIndex(commitIndexes.get(commitIndexes.size() / 2));
    }
  }

  synchronized void updateCommitIndex(int leaderCommitIndex) throws UnknownGameException {
    if (leaderCommitIndex > commitIndex) {
      if (leaderCommitIndex > getLastLogIndex()) {
        commitAllEntriesBelowIndex(getLastLogIndex());
      } else {
        commitAllEntriesBelowIndex(leaderCommitIndex);
      }
    }
  }

  private synchronized void commitAllEntriesBelowIndex(int newCommitIndex)
      throws UnknownGameException {
    for (int logEntryIndex = this.commitIndex + 1; logEntryIndex <= newCommitIndex;
        logEntryIndex++) {
      commitLogEntry(logEntries.get(logEntryIndex));
    }
    commitIndex = newCommitIndex;
  }

  private synchronized void commitLogEntry(LogEntry logEntry) throws UnknownGameException {
    logEntryProcessor.update(logEntry);
  }

  private synchronized void removeLogEntriesAfter(LogEntry logEntry) {
    //for loop is in reversed order so rollback of entries can be easier implemented
    for (int logEntryIndex = getLastLogIndex(); logEntryIndex >= logEntry.getLogIndex();
        logEntryIndex--) {
      logEntries.remove(logEntryIndex);
    }
    if (this.commitIndex > logEntry.getLogIndex()) {
      this.commitIndex = logEntry.getLogIndex();
    }
  }

  synchronized boolean verifyElectionTermOfLogEntryMatches(int previousLogIndex,
      int previousLogElectionTerm) {
    return getLastLogIndex() == previousLogIndex
        && getLastLogElectionTerm() == previousLogElectionTerm;
  }

  synchronized int getLastLogIndex() {
    if (!logEntries.isEmpty()) {
      return logEntries.size() - 1;
    } else {
      return -1;
    }
  }

  synchronized int getLastLogElectionTerm() {
    if (!logEntries.isEmpty()) {
      return getLastLogEntry().getElectionTerm();
    } else {
      return -1;
    }
  }

  synchronized int getLastLogIndexForPlayer(Player player) {
    addPlayerIfNotInConnectionListYet(player);
    return playerCommitIndexes.get(player);
  }

  synchronized int getLastLogElectionTermForPlayer(Player player) {
    if (getLastLogIndexForPlayer(player) == -1) {
      return -1;
    }
    return logEntries.get(getLastLogIndexForPlayer(player)).getElectionTerm();
  }

  synchronized int getNextLogIndex() {
    return logEntries.size();
  }

  synchronized LogEntry getLastLogEntry() {
    if (!logEntries.isEmpty()) {
      return logEntries.get(getLastLogIndex());
    } else {
      throw new IllegalStateException("LastLogEntry cannot be retrieved when there are no logs");
    }
  }

  synchronized int getCommitIndex() {
    return commitIndex;
  }

  synchronized List<LogEntry> getUncommittedLogs() {
    return new ArrayList<>(logEntries.subList(getCommitIndex() + 1, getNextLogIndex()));
  }

  synchronized List<LogEntry> getLogsNotConfirmedByPlayer(Player player) {
    if (getLastLogIndexForPlayer(player) != -1) {
      return new ArrayList<>(
          logEntries.subList(getLastLogIndexForPlayer(player) + 1, getNextLogIndex()));
    }

    return new ArrayList<>(logEntries.subList(0, getNextLogIndex()));
  }

  synchronized List<LogEntry> assignLogIndexAndElectionTerm(
      List<LogEntry> logEntries,
      LeaderElection leaderElection
  ) {
    for (LogEntry logEntry : logEntries) {
      logEntries.remove(logEntry);
      logEntries.add(logEntry.cloneWithNewLogIndexAndElectionTerm(getNextLogIndex(),
          leaderElection.getElectionTerm()));
    }
    return logEntries;
  }

  public void reset() {
    System.out.println("reset function logController");
    logEntries.clear();
    commitIndex = -1;
    playerCommitIndexes.clear();
  }
}