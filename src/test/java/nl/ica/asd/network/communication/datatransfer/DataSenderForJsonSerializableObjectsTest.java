package nl.ica.asd.network.communication.datatransfer;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import nl.ica.asd.logic.domain.BusinessType;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.logic.domain.PlayerStatus;
import nl.ica.asd.logic.domain.actions.OrderAction;
import nl.ica.asd.network.communication.DataTransferObjectGenerator;
import nl.ica.asd.network.communication.datatransferobjects.DataTransferObject;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.AppendEntry;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.Vote;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.LogEntry;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.logentrydatatypes.BusinessUpdate;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.logentrydatatypes.OrderUpdate;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.logentries.logentrydatatypes.UpdateType;
import nl.ica.asd.network.communication.twophasecommit.Messenger.CallBackHandler;
import nl.ica.asd.network.playerlobby.ConnectedPlayers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class DataSenderForJsonSerializableObjectsTest {

  private DataSenderForJsonSerializableObjects sut;

  @Mock
  private CallBackHandler callBackHandler;
  @Mock
  private ConnectedPlayers connectedPlayers;

  private ObjectMapper objectMapper;

  @BeforeEach
  void setup() throws NoSuchFieldException, IllegalAccessException {
    MockitoAnnotations.initMocks(this);
    callBackHandler = mock(CallBackHandler.class);
    connectedPlayers = mock(ConnectedPlayers.class);
    sut = new DataSenderForJsonSerializableObjects(connectedPlayers);
    objectMapper = new ObjectMapper();
  }

  @Test
  void serializedstring_Should_containTypeOfDataTransferObject_when_ObjectIsSerialized()
      throws JsonProcessingException {
    final DataTransferObject obj = new Vote(1, true);
    final String json = objectMapper.writeValueAsString(obj);
    assertTrue(json.contains("@dataTransferObjectType"));
  }

  @Test
  void serializedstring_Should_containListOfDataTransferObjects_When_AppendEntryWithListIsSerialized()
      throws JsonProcessingException {

    final List<LogEntry> a = new ArrayList<>();
    a.add(
        new BusinessUpdate(1, 1, UpdateType.SET, DataTransferObjectGenerator.getBusinessRules(1),
            UUID.nameUUIDFromBytes("1".getBytes())));
    a.add(new OrderUpdate(1, 1, UpdateType.SET, new OrderAction(1, null, null),
        UUID.nameUUIDFromBytes("1".getBytes())));

    final DataTransferObject obj = new AppendEntry(1,
        new Player("bla", UUID.randomUUID(), BusinessType.FACTORY,
            PlayerStatus.CONNECTED_AND_ACTIVE, "123", 1),
        1, 1, a, 8);
    final String json = objectMapper.writeValueAsString(obj);
    assertTrue(json.contains("BusinessUpdate"));
  }
}