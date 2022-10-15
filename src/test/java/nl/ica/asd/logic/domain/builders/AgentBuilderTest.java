package nl.ica.asd.logic.domain.builders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import nl.ica.asd.logic.domain.Agent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AgentBuilderTest {

  @Mock
  private Agent agent;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.initMocks(this);
    when(agent.getBusinessRules()).thenReturn("testingrule = 1");
  }

  @Test
  void anAgent_should_returnNotNull() {
    assertNotNull(AgentBuilder.anAgent());
  }

  @Test
  void when_changedVariablesInBuilder_fromBaseAgent_should_buildAgentWithNewVariables() {
    final String newBusinessRule = new StringBuilder(agent.getBusinessRules()).reverse().toString();
    final Agent buildAgent = AgentBuilder.fromBaseAgent(agent).withBusinessRules(newBusinessRule)
        .build();
    assertNotEquals(agent.getBusinessRules(), buildAgent.getBusinessRules(),
        "rules of agents should not be the same");
    assertEquals(newBusinessRule, buildAgent.getBusinessRules(),
        String.format("new agent businessrule should be %s", newBusinessRule));
  }

}
