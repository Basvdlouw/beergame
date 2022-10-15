package nl.ica.asd.logic.domain.builders;

import nl.ica.asd.logic.domain.Agent;

public final class AgentBuilder {

  private String businessRules;

  private AgentBuilder() {
  }

  public static AgentBuilder anAgent() {
    return new AgentBuilder();
  }

  public static AgentBuilder fromBaseAgent(Agent agent) {
    return new AgentBuilder().withBusinessRules(agent.getBusinessRules());
  }

  public AgentBuilder withBusinessRules(String businessRules) {
    this.businessRules = businessRules;
    return this;
  }

  public Agent build() {
    return new Agent(businessRules);
  }
}
