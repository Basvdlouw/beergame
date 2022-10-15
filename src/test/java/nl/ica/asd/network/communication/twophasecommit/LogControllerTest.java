package nl.ica.asd.network.communication.twophasecommit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.network.communication.DataTransferObjectGenerator;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.LogEntry;
import nl.ica.asd.network.playerlobby.ConnectedPlayers;
import nl.ica.asd.storage.exception.UnknownGameException;
import nl.ica.asd.util.DomainObjectGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;


public class LogControllerTest {

  @Mock
  private LogEntryProcessor logEntryProcessor;

  @InjectMocks
  private LogController sut;
  @InjectMocks
  private ConnectedPlayers connectedPlayers;

  private Field playerCommitIndexes;
  private Field logEntries;

  @BeforeEach
  public void init() throws NoSuchFieldException {
    logEntryProcessor = mock(LogEntryProcessor.class);
    connectedPlayers = mock(ConnectedPlayers.class);
    sut = new LogController(logEntryProcessor, connectedPlayers);

    playerCommitIndexes = sut.getClass().getDeclaredField("playerCommitIndexes");
    playerCommitIndexes.setAccessible(true);

    logEntries = sut.getClass().getDeclaredField("logEntries");
    logEntries.setAccessible(true);

  }


  @Test
  void addLogEntries_should_addASingleEntryListWithoutErrors() {
    List<LogEntry> logEntries = new ArrayList<>();
    logEntries.add(DataTransferObjectGenerator.getLogEntries(1).get(0));
    sut.synchronizeLogEntries(logEntries);
    assertEquals(logEntries, sut.getUncommittedLogs());
  }

  @Test
  void addLogEntries_should_addMultipleEntriesWithoutErrors() {
    List<LogEntry> logEntries = new ArrayList<>(DataTransferObjectGenerator.getLogEntries(4));
    sut.synchronizeLogEntries(logEntries);
    assertEquals(logEntries, sut.getUncommittedLogs());
  }

  @Test
  void addLogEntries_should_addEntryWithoutIndexWithoutErrors() {
    List<LogEntry> logEntries = new ArrayList<>();
    logEntries.add(DataTransferObjectGenerator.getOrderUpdate(1, -2));
    assertEquals(-1, sut.getLastLogIndex());
    sut.synchronizeLogEntries(logEntries);
    List<LogEntry> result = sut.getUncommittedLogs();
    assertEquals(0, sut.getLastLogIndex());
    assertEquals(logEntries.get(0).getElectionTerm(), result.get(0).getElectionTerm());
    assertEquals(0, result.get(0).getLogIndex());
  }

  @Test
  void addLogEntries_should_removeOldEntries_when_NewOnesCollideWithIt() {
    List<LogEntry> logEntries = new ArrayList<>(DataTransferObjectGenerator.getLogEntries(10));
    sut.synchronizeLogEntries(logEntries);
    assertEquals(9, sut.getLastLogIndex());
    List<LogEntry> collidingList = new ArrayList<>();
    collidingList.add(DataTransferObjectGenerator.getOrderUpdate(11, -5));
    collidingList.add(DataTransferObjectGenerator.getOrderUpdate(12, -5));
    sut.synchronizeLogEntries(collidingList);
    List<LogEntry> result = sut.getUncommittedLogs();
    assertEquals(7, sut.getLastLogIndex());
    assertEquals(8, result.size());
    assertEquals(11, result.get(6).getElectionTerm());
    assertEquals(12, result.get(7).getElectionTerm());
  }

  @Test
  void addLogEntries_should_removeOverwriteOldEntries_when_NewOnesCollideWithIt()
      throws IOException {
    List<LogEntry> logEntries = new ArrayList<>(DataTransferObjectGenerator.getLogEntries(10));
    sut.synchronizeLogEntries(logEntries);
    assertEquals(9, sut.getLastLogIndex());
    List<LogEntry> collidingList = DataTransferObjectGenerator.getLogEntries(10);
    sut.synchronizeLogEntries(collidingList);
    when(connectedPlayers.getPlayerForSelf()).thenReturn(DomainObjectGenerator.getPlayer(1));
    List<LogEntry> result = sut.getUncommittedLogs();
    assertEquals(9, sut.getLastLogIndex());
    assertEquals(10, result.size());
    assertEquals(6, result.get(6).getElectionTerm());
    assertEquals(9, result.get(9).getElectionTerm());
  }

  @Test
  void addLogEntries_should_lowerCommitIndex_when_committedEntriesAreRemoved()
      throws UnknownGameException {
    List<LogEntry> logEntries = new ArrayList<>(DataTransferObjectGenerator.getLogEntries(10));
    sut.synchronizeLogEntries(logEntries);
    sut.updateCommitIndex(8);
    assertEquals(9, sut.getLastLogIndex());
    assertEquals(8, sut.getCommitIndex());
    List<LogEntry> collidingList = new ArrayList<>();
    collidingList.add(DataTransferObjectGenerator.getOrderUpdate(6, -2));
    sut.synchronizeLogEntries(collidingList);
    assertEquals(4, sut.getLastLogIndex());
    assertEquals(4, sut.getCommitIndex());
  }

  @Test
  void updateCommitIndexIfNeeded_should_updateCommitIndex_when_halfOfAllNodesAreAboveTheNewCommitIndexConfirmation()
      throws UnknownGameException {
    List<Player> players = new ArrayList<>();
    //start of with 4 players (this is exclusive the leader)
    for (int i = 0; i < 4; i++) {
      players.add(DataTransferObjectGenerator.getPlayer(i));
    }

    List<LogEntry> logEntries = new ArrayList<>(DataTransferObjectGenerator.getLogEntries(10));
    sut.synchronizeLogEntries(logEntries);

    //start off with a set of nodes that all have no committed messages
    for (int i = 0; i < 4; i++) {
      sut.updateCommitIndex(players.get(i), -1);
    }
    assertEquals(-1, sut.getCommitIndex());
    sut.updateCommitIndex(players.get(0), 0);
    assertEquals(-1, sut.getCommitIndex());
    sut.updateCommitIndex(players.get(1), 1);
    assertEquals(0, sut.getCommitIndex());
    sut.updateCommitIndex(players.get(2), 2);
    assertEquals(1, sut.getCommitIndex());
    sut.updateCommitIndex(players.get(3), 3);
    assertEquals(2, sut.getCommitIndex());
    sut.updateCommitIndex(players.get(0), 4);
    assertEquals(3, sut.getCommitIndex());
  }

  @Test
  void updateCommitIndex_should_updateTheCommitIndex() throws UnknownGameException {
    sut.synchronizeLogEntries(DataTransferObjectGenerator.getLogEntries(10));
    assertEquals(-1, sut.getCommitIndex());
    sut.updateCommitIndex(4);
    assertEquals(4, sut.getCommitIndex());
  }

  @Test
  void updateCommitIndex_should_updateTheCommitIndex_when_givenCommitIndexIsHigherThanLogIndex()
      throws UnknownGameException {
    sut.synchronizeLogEntries(DataTransferObjectGenerator.getLogEntries(10));
    assertEquals(-1, sut.getCommitIndex());
    sut.updateCommitIndex(20);
    assertEquals(9, sut.getCommitIndex());
  }

  @Test
  void verifyElectionTermOfLogEntryMatches() {
    sut.synchronizeLogEntries(DataTransferObjectGenerator.getLogEntries(5));
    assertTrue(sut.verifyElectionTermOfLogEntryMatches(4, 4));
  }

  @Test
  void getLastLogElectionTerm_should_returnNegativeOne_when_thereAreNoLogs() {
    assertEquals(-1, sut.getLastLogElectionTerm());
  }

  @Test
  void getLastLogIndexForPlayer_should_returnNegativeOne_when_playerIsNotInConnectedListYet() {
    Player player = DomainObjectGenerator.getPlayer(1);

    assertEquals(-1, sut.getLastLogIndexForPlayer(player));
  }

  @Test
  void getLastLogIndexForPlayer_should_returnLogIndexOfPlayer_when_playerIsInConnectedList()
      throws UnknownGameException {
    Player player = DomainObjectGenerator.getPlayer(1);
    sut.getLastLogIndexForPlayer(player);
    List<LogEntry> logEntries = DataTransferObjectGenerator.getLogEntries(10);
    sut.synchronizeLogEntries(logEntries);
    sut.updateCommitIndex(player, 5);

    assertEquals(5, sut.getLastLogIndexForPlayer(player));
  }

  @Test
  void getLastLogElectionTermForPlayer_should_returnNegativeOne_when_playerIsNotInConnectedListYet() {
    Player player = DomainObjectGenerator.getPlayer(1);

    assertEquals(-1, sut.getLastLogElectionTermForPlayer(player));
  }

  @Test
  void getLastLogElectionTermForPlayer_should_returnLogIndexLogElectionTermOfPlayer_when_playerIsInConnectedList()
      throws UnknownGameException {
    Player player = DomainObjectGenerator.getPlayer(1);
    sut.getLastLogIndexForPlayer(player);
    List<LogEntry> logEntries = DataTransferObjectGenerator.getLogEntries(10);
    sut.synchronizeLogEntries(logEntries);
    sut.updateCommitIndex(player, 5);

    assertEquals(5, sut.getLastLogElectionTermForPlayer(player));
  }
}