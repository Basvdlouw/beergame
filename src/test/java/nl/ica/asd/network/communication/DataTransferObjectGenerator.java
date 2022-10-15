package nl.ica.asd.network.communication;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import nl.ica.asd.network.communication.datatransferobjects.DataTransferMessage;
import nl.ica.asd.network.communication.datatransferobjects.DataTransferObject;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.AppendEntry;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.AppendEntryResult;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.SerializationExceptionResponse;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.Vote;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.VoteRequest;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.LogEntry;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.logentrydatatypes.BusinessUpdate;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.logentrydatatypes.GameStateUpdate;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.logentrydatatypes.OrderUpdate;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.logentrydatatypes.PlayerUpdate;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.logentrydatatypes.UpdateType;
import nl.ica.asd.util.DomainObjectGenerator;

public class DataTransferObjectGenerator extends DomainObjectGenerator {


  public static DataTransferMessage getDataTransferMessage(Integer seed, boolean invert) {
    DataTransferObject dataTransferObject = getDataTransferObject(seed);
    if (!invert) {
      return new DataTransferMessage(getPlayer(seed), dataTransferObject, getPlayer(seed + 1));
    } else {
      return new DataTransferMessage(getPlayer(seed + 1), dataTransferObject, getPlayer(seed));
    }
  }

  public static DataTransferObject getDataTransferObject(Integer seed) {
    switch (Math.abs(seed % 5)) {
      case 0:
        return getAppendEntry(seed);
      case 1:
        return getAppendEntryResult(seed);
      case 2:
        return getSerializationExceptionResponse(seed);
      case 3:
        return getVote(seed);
      case 4:
        return getVoteRequest(seed);
    }
    throw new IllegalStateException();
  }

  public static AppendEntry getAppendEntry(Integer seed) {
    Integer amountOfOrders = Math.abs(seed) + 1;
    return new AppendEntry(seed, getPlayer(seed), seed, seed, getLogEntries(amountOfOrders),
        seed);
  }

  public static AppendEntryResult getAppendEntryResult(Integer seed) {
    return new AppendEntryResult(seed, seed, seed % 2 == 0);
  }

  public static SerializationExceptionResponse getSerializationExceptionResponse(Integer seed) {
    return new SerializationExceptionResponse(seed.toString());
  }

  public static Vote getVote(Integer seed) {
    return new Vote(seed, seed % 2 == 0);
  }

  public static VoteRequest getVoteRequest(Integer seed) {
    return new VoteRequest(seed, getPlayer(seed), seed, seed);
  }

  public static List<LogEntry> getLogEntries(Integer amount) {
    List<LogEntry> logEntries = new ArrayList<>();
    for (int i = 0; i < amount; i++) {
      logEntries.add(getLogEntry(i));
    }
    return logEntries;
  }

  public static LogEntry getLogEntry(Integer seed) {
    switch (Math.abs(seed % 4)) {
      case 0:
        return getBusinessUpdate(seed, 0);
      case 1:
        return getGameStateUpdate(seed, 0);
      case 2:
        return getOrderUpdate(seed, 0);
      case 3:
        return getPlayerUpdate(seed, 0);

    }
    throw new IllegalStateException();
  }

  public static BusinessUpdate getBusinessUpdate(Integer seed, Integer logIndexOffset) {
    return new BusinessUpdate(seed + logIndexOffset, seed, UpdateType.SET, getBusinessRules(seed),
        UUID.nameUUIDFromBytes(seed.toString().getBytes()));
  }

  public static GameStateUpdate getGameStateUpdate(Integer seed, Integer logIndexOffset) {
    return new GameStateUpdate(seed + logIndexOffset, seed, UpdateType.SET, getGameState(seed),
        UUID.nameUUIDFromBytes(seed.toString().getBytes()));
  }

  public static OrderUpdate getOrderUpdate(Integer seed, int logIndexOffset) {
    return new OrderUpdate(seed + logIndexOffset, seed, UpdateType.SET, getOderAction(seed),
        UUID.nameUUIDFromBytes(seed.toString().getBytes()));
  }

  public static PlayerUpdate getPlayerUpdate(Integer seed, int logIndexOffset) {
    return new PlayerUpdate(seed + logIndexOffset, seed, UpdateType.SET, getPlayer(seed),
        UUID.nameUUIDFromBytes(seed.toString().getBytes()));
  }
}
