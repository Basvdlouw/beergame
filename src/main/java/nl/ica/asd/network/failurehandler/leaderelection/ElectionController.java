package nl.ica.asd.network.failurehandler.leaderelection;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.AppendEntry;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.Vote;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.VoteRequest;
import nl.ica.asd.network.communication.interfaces.SendVoteRequests;
import nl.ica.asd.network.failurehandler.LeaderElection;
import nl.ica.asd.network.failurehandler.exceptions.NoLeaderException;
import nl.ica.asd.network.playerlobby.ConnectedPlayers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Named("ElectionController")
public class ElectionController implements LeaderElection {

  @Inject
  private ConnectedPlayers connectedPlayers;
  @Inject
  private SendVoteRequests sendVoteRequests;

  private final ElectionTimer electionTimer;

  private Player leader;
  private int electionTerm;
  private ElectionState state;
  private Set<Player> votesReceived;
  private Player votedFor;
  private int votedForTerm;

  private static Logger logger = LoggerFactory.getLogger(ElectionController.class);

  @Inject
  public ElectionController() {
    this.electionTimer = new ElectionTimer(this);

    electionTerm = 0;
    state = ElectionState.FOLLOWER;
    votesReceived = new HashSet<>();
  }

  public void receiveHeartbeat(AppendEntry heartbeat) {
    if (heartbeat.getElectionTerm() > electionTerm) {
      becomeFollower(heartbeat.getLeader(), heartbeat.getElectionTerm());
    } else if (heartbeat.getElectionTerm() == electionTerm) {
      if (state == ElectionState.CANDIDATE) {
        becomeFollower(heartbeat.getLeader(), heartbeat.getElectionTerm());
      } else {
        electionTimer.startAndResetTimeout();
      }
    }
  }

  void startElection() throws IOException {
    if (!isLeader()) {

      electionTerm++;
      state = ElectionState.CANDIDATE;
      votesReceived = new HashSet<>();
      Player playerForSelf = connectedPlayers.getPlayerForSelf();
      votesReceived.add(playerForSelf);
      checkIfElectionIsWon();
      votedFor = playerForSelf;
      votedForTerm = electionTerm;

      if (logger.isInfoEnabled()) {
        logger.info(String.format("Election started, new term is %d", electionTerm));
      }

      sendVoteRequests.sendVoteRequests(connectedPlayers.getAllPlayers(), this,
          connectedPlayers.getPlayerForSelf());
    }
    electionTimer.startAndResetTimeout();
  }

  @Override
  public boolean isLeader() {
    return state == ElectionState.LEADER;
  }


  @Override
  public int getElectionTerm() {
    return electionTerm;
  }

  @Override
  public Player getLeader() throws NoLeaderException {
    if (leader == null) {
      throw new NoLeaderException(state, electionTerm);
    }
    return leader;
  }

  @Override
  public ElectionState getElectionState() {
    return state;
  }

  @Override
  public Vote receiveVoteRequest(VoteRequest voteRequest, int lastLogIndex,
      int lastLogElectionTerm) {

    if (logger.isInfoEnabled()) {
      logger.info(String.format(
          "Vote request received from %s for election term %d. My last log index is %d and theirs is %d",
          voteRequest.getCandidate().getUsername(), voteRequest.getElectionTerm(), lastLogIndex,
          voteRequest.getLastLogIndex()));
    }

    if (voteRequest.getElectionTerm() > electionTerm
        && voteRequest.getLastLogIndex() >= lastLogIndex
        && (voteRequest.getElectionTerm() > votedForTerm ||
        (voteRequest.getElectionTerm() == votedForTerm &&
            (votedFor == null || voteRequest.getCandidate().equals(votedFor))))) {

      state = ElectionState.FOLLOWER;
      electionTimer.startAndResetTimeout();
      votedFor = voteRequest.getCandidate();
      votedForTerm = voteRequest.getElectionTerm();

      if (logger.isInfoEnabled()) {
        logger.info(String.format(
            "Vote request received for election term %d during election term %d from %s. Voted for. VotedForTerm is %d",
            voteRequest.getElectionTerm(), electionTerm, voteRequest.getCandidate().getUsername(),
            votedForTerm));
      }

      return new Vote(voteRequest.getElectionTerm(), true);
    }

    if (logger.isInfoEnabled()) {
      logger.info(String.format(
          "Vote request received for election term %d during election term %d from %s. Voted against",
          voteRequest.getElectionTerm(), electionTerm, voteRequest.getCandidate().getUsername()));
    }
    return new Vote(voteRequest.getElectionTerm(), false);
  }


  @Override
  public void receiveVote(Vote vote, Player sender) throws IOException {
    if (logger.isInfoEnabled()) {
      logger.info(String.format(
          "New vote received from player with username %s, in election term: %s and vote granted: %s. Current state is %s, term is %s and playerVote already received: %s",
          sender.getUsername(), vote.getElectionTerm(), vote.isVoteGranted(), state, electionTerm,
          playerVoteAlreadyReceived(sender)));
    }

    if (state == ElectionState.CANDIDATE
        && vote.isVoteGranted()
        && vote.getElectionTerm() == electionTerm
        && !playerVoteAlreadyReceived(sender)) {

      votesReceived.add(sender);

      checkIfElectionIsWon();
    }
  }

  private void checkIfElectionIsWon() throws IOException {
    if (votesReceived.size() > ((double) connectedPlayers.getAllPlayers().size() / 2)) {
      if (logger.isInfoEnabled()) {
        logger.info(String.format(
            "Enough votes received to become leader %d/%d.",
            votesReceived.size(), connectedPlayers.getAllPlayers().size()));
      }
      becomeLeader(electionTerm);
    }
  }

  private boolean playerVoteAlreadyReceived(Player sender) {
    for (Player player : votesReceived) {
      if (player.equals(sender)) {
        return true;
      }
    }
    return false;
  }

  private void becomeFollower(Player newLeader, int newElectionTerm) {
    leader = newLeader;
    electionTerm = newElectionTerm;
    votesReceived = new HashSet<>();
    state = ElectionState.FOLLOWER;
    votedFor = null;

    if (logger.isInfoEnabled()) {
      logger.info(String.format("Change of leader. New leader is %s. New election term is %d",
          newLeader.getUsername(), electionTerm));
    }

    electionTimer.startAndResetTimeout();
  }

  public void becomeLeader(int newElectionTerm) throws IOException {
    leader = connectedPlayers.getPlayerForSelf();
    state = ElectionState.LEADER;
    votesReceived = new HashSet<>();
    electionTerm = newElectionTerm;
    votedFor = null;

    if (logger.isInfoEnabled()) {
      logger.info(String.format(
          "This node is now the leader of term %d",
          electionTerm));
    }

    electionTimer.startAndResetTimeout();
  }

  public void reset(){
    leader = null;
    electionTerm = 0;
    state = ElectionState.FOLLOWER;
    votesReceived = new HashSet<>();
    votedFor = null;
    votedForTerm = 0;
    electionTimer.stopTimeout();
  }
}
