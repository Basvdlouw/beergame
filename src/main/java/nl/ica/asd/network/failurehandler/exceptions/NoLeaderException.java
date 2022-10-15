package nl.ica.asd.network.failurehandler.exceptions;

import nl.ica.asd.network.failurehandler.leaderelection.ElectionState;

public class NoLeaderException extends Exception {

  private final ElectionState state;
  private final int electionTerm;

  public NoLeaderException(ElectionState state, int electionTerm) {
    super("No leader. State: " + state.name() + " ElectionTerm: " + electionTerm);
    this.electionTerm = electionTerm;
    this.state = state;
  }

  public ElectionState getState() {
    return state;
  }

  public int getElectionTerm() {
    return electionTerm;
  }
}
