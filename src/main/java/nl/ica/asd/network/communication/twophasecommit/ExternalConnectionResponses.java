package nl.ica.asd.network.communication.twophasecommit;


import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.AppendEntry;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.AppendEntryResult;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.InitialConnectionRequest;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.InitialConnectionResponse;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.Vote;
import nl.ica.asd.network.failurehandler.LeaderElection;

public interface ExternalConnectionResponses {

  void receiveVote(Vote vote, Player sender,
      LeaderElection leaderElection);

  void receiveAppendEntryResult(AppendEntry appendEntry, AppendEntryResult dataTransferObject,
      Player responder, LeaderElection leaderElection);

  void receiveConnectionResponse(
      InitialConnectionRequest originalRequest, InitialConnectionResponse connectionResponse);

  void receiveConnectionFailed();
}
