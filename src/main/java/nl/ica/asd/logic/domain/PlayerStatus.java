package nl.ica.asd.logic.domain;

public enum PlayerStatus {
  CONNECTED_AND_ACTIVE(false, true),
  LEFT_BY_ACCIDENT(true, true),
  LEFT_ON_PURPOSE(true, false),
  AGENT_ACTIVATED(true, true);

  public final boolean isPlayedByAgent;
  public final boolean isSendingHeartbeatsToNode;

  PlayerStatus(boolean isPlayedByAgent, boolean isSendingHeartbeatsToNode) {
    this.isPlayedByAgent = isPlayedByAgent;
    this.isSendingHeartbeatsToNode = isSendingHeartbeatsToNode;
  }

  public boolean isPlayedByAgent() {
    return isPlayedByAgent;
  }

  public boolean isSendingHeartbeatsToNode() {
    return isSendingHeartbeatsToNode;
  }
}
