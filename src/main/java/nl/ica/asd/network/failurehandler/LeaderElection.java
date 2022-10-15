package nl.ica.asd.network.failurehandler;

import java.io.IOException;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.Vote;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.VoteRequest;
import nl.ica.asd.network.failurehandler.exceptions.NoLeaderException;
import nl.ica.asd.network.failurehandler.leaderelection.ElectionState;

public interface LeaderElection {

  int getElectionTerm();

  Player getLeader() throws NoLeaderException;

  ElectionState getElectionState();

  Vote receiveVoteRequest(VoteRequest voteRequest, int lastLogIndex, int lastLogElectionTerm);

  void receiveVote(Vote vote, Player sender) throws IOException;

  boolean isLeader();
}
