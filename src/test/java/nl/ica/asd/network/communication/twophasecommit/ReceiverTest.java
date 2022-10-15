package nl.ica.asd.network.communication.twophasecommit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.network.communication.DataTransferObjectGenerator;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.AppendEntry;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.AppendEntryResult;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.InitialConnectionRequest;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.InitialConnectionResponse;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.SerializationExceptionResponse;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.Vote;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.VoteRequest;
import nl.ica.asd.network.communication.gameinitialisation.ConnectionResult;
import nl.ica.asd.storage.domain.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ReceiverTest {

  @InjectMocks
  private Receiver sut;

  @Mock
  private ExternalConnectionEndpoint externalConnectionEndpoint;

  @BeforeEach
  void setUp() {
    externalConnectionEndpoint = mock(ExternalConnectionEndpoint.class);

    MockitoAnnotations.initMocks(this);
  }

  @Test
  void receiveData_should_returnProperResponse() throws IOException {
    Player player = DataTransferObjectGenerator.getPlayer(1);

    Set<Player> players = new HashSet<>();
    GameData gameData = mock(GameData.class);

    AppendEntry appendEntry = mock(AppendEntry.class);
    VoteRequest voteRequest = mock(VoteRequest.class);
    InitialConnectionRequest initialConnectionRequest = mock(InitialConnectionRequest.class);

    AppendEntryResult appendEntryResult = mock(AppendEntryResult.class);
    Vote vote = mock(Vote.class);
    InitialConnectionResponse initialConnectionResponse = new InitialConnectionResponse(players,
        gameData, ConnectionResult.SUCCESS);

    when(externalConnectionEndpoint.receiveAppendEntry(player, appendEntry))
        .thenReturn(appendEntryResult);
    when(externalConnectionEndpoint.receiveVoteRequest(player, voteRequest))
        .thenReturn(vote);
    when(externalConnectionEndpoint
        .receiveConnectionRequest(initialConnectionRequest))
        .thenReturn(initialConnectionResponse);

    assertEquals(appendEntryResult, sut.receiveData(player, appendEntry));
    assertEquals(vote, sut.receiveData(player, voteRequest));
    assertEquals(initialConnectionResponse, sut.receiveData(player, initialConnectionRequest));
    assertEquals(SerializationExceptionResponse.class,
        sut.receiveData(player, new SerializationExceptionResponse("")).getClass());
  }

  @Test
  void receiveData_should_throwIllegalStateException_when_DataTransferObjectIsNotRecognized() {
    Player player = DataTransferObjectGenerator.getPlayer(1);
    Vote vote = mock(Vote.class);

    assertThrows(IllegalStateException.class, () -> sut.receiveData(player, vote));
  }
}