package nl.ica.asd.network.communication.twophasecommit;

import java.io.IOException;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.AppendEntry;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.AppendEntryResult;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.InitialConnectionRequest;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.InitialConnectionResponse;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.Vote;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.VoteRequest;

public interface ExternalConnectionEndpoint {

  AppendEntryResult receiveAppendEntry(Player sender, AppendEntry appendEntry) throws IOException;

  Vote receiveVoteRequest(Player player, VoteRequest voteRequest);

  InitialConnectionResponse receiveConnectionRequest(
      InitialConnectionRequest initialConnectionRequest);
}
