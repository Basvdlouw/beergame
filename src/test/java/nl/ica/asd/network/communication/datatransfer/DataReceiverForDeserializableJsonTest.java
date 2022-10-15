package nl.ica.asd.network.communication.datatransfer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import nl.ica.asd.network.communication.datatransferobjects.DataTransferObject;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.Vote;
import nl.ica.asd.network.communication.twophasecommit.Receiver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


class DataReceiverForDeserializableJsonTest {

  @Mock
  private DataTransferObject dataTransferObject;

  @Mock
  private Receiver receiver;

  private ObjectMapper objectMapper;

  private DataReceiverForDeserializableJson dataReceiverForDeserializableJson;

  @BeforeEach
  void setup() {
    objectMapper = new ObjectMapper();
    MockitoAnnotations.initMocks(this);
    dataReceiverForDeserializableJson = new DataReceiverForDeserializableJson(receiver);
  }

  @Test
  void handleReceivedData_should_returnStringWithReceiverAndPayload_when_receivingSerializedMessage()
      throws IOException {
    final String message =
        "{\"sender\":{\"username\":\"user4\",\"businessUUID\":\"a87ff679-a2f3-371d-9181-a67b7542122"
            + "c\",\"businessType\":\"FACTORY\",\"ip\":\"123.123.123.4\",\"port\":444,\"playerStatu"
            + "s\":\"CONNECTED_AND_ACTIVE\"},\"payload\":{\"@dataTransferObjectType\":\"VoteRequest"
            + "\",\"electionTerm\":4,\"candidate\":{\"username\":\"user4\",\"businessUUID\":\"a87ff"
            + "679-a2f3-371d-9181-a67b7542122c\",\"businessType\":\"FACTORY\",\"ip\":\"123.123.123."
            + "4\",\"port\":444,\"playerStatus\":\"CONNECTED_AND_ACTIVE\"},\"lastLogIndex\":4,\"las"
            + "tLogElectionTerm\":4},\"receiver\":{\"username\":\"user5\",\"businessUUID\":\"e4da3b"
            + "7f-bbce-3345-9777-2b0674a318d5\",\"businessType\":\"FACTORY\",\"ip\":\"123.123.123.5"
            + "\",\"port\":555,\"playerStatus\":\"CONNECTED_AND_ACTIVE\"}}\n";

    final DataTransferObject vote = new Vote(1, true);

    final String expectedResult =
        "{\"sender\":{\"username\":\"user5\",\"businessUUID\":\"e4da3b7f-bbce-3345-9777-2b0674a318d"
            + "5\",\"businessType\":\"FACTORY\",\"playerStatus\":\"CONNECTED_AND_ACTIVE\",\"ip\":\""
            + "123.123.123.5\",\"port\":555},\"payload\":{\"@dataTransferObjectType\":\"Vote\",\"el"
            + "ectionTerm\":1,\"voteGranted\":true},\"receiver\":{\"username\":\"user4\",\"business"
            + "UUID\":\"a87ff679-a2f3-371d-9181-a67b7542122c\",\"businessType\":\"FACTORY\",\"playe"
            + "rStatus\":\"CONNECTED_AND_ACTIVE\",\"ip\":\"123.123.123.4\",\"port\":444}}";

    when(receiver.receiveData(any(), any(DataTransferObject.class)))
        .thenReturn(vote);
    assertEquals(expectedResult, dataReceiverForDeserializableJson.handleReceivedData(message));
  }

  @Test
  void handleReceivedData_should_returnExceptionInSerializedString_when_serializedMessageIsNotADataTransferMessage()
      throws IOException {
    final String serializedMessage = "123";
    when(receiver.receiveData(any(), any(DataTransferObject.class)))
        .thenReturn(dataTransferObject);
    assertEquals(
        "{\"@dataTransferObjectType\":\"SerializationExceptionResponse\",\"errorMessage\":"
            + "\"Cannot construct instance of `nl.ica.asd.network.communication.datatransferobjects"
            + ".DataTransferMessage` (although at least one Creator exists): no int/Int-argument co"
            + "nstructor/factory method to deserialize from Number value (123)\\n at [Source: (Stri"
            + "ng)\\\"123\\\"; line: 1, column: 1]\"}",
        dataReceiverForDeserializableJson.handleReceivedData(serializedMessage));
  }
}