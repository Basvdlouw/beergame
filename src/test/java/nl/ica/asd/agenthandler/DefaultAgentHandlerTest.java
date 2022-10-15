package nl.ica.asd.agenthandler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import nl.ica.asd.agenthandler.exceptions.AgentException;
import nl.ica.asd.agenthandler.tokens.StatementContainer;
import nl.ica.asd.agenthandler.tokens.VariableAssignment;
import nl.ica.asd.agenthandler.tokens.expressions.IntLiteral;
import nl.ica.asd.agenthandler.tokens.expressions.operations.MathOperation;
import nl.ica.asd.agenthandler.tokens.expressions.operations.MathOperatorType;
import nl.ica.asd.logic.domain.Business;
import nl.ica.asd.logic.domain.BusinessRules;
import nl.ica.asd.logic.statecalculator.BusinessState;
import nl.ica.asd.logic.statecalculator.StateCalculator;
import nl.ica.asd.network.communication.interfaces.SendBusinessRules;
import nl.ica.asd.storage.domain.GamePrices;
import nl.ica.asd.storage.exception.UnknownException;
import nl.ica.asd.storage.exception.UnknownGameException;
import nl.ica.asd.storage.gameconfig.GameConfig;
import nl.ica.asd.storage.gamedatastorage.GameDataAccess;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultAgentHandlerTest {

  @Mock
  private Business business;

  @Mock
  private GameDataAccess gameDataAccess;

  @Mock
  private AgentRunner defaultAgentRunner;

  @Mock
  private AgentRunner otherAgentRunner;

  @Mock
  private StateCalculator stateCalculator;

  @Mock
  private GameConfig gameConfig;

  @Mock
  private ASTHandler astHandler;

  @Mock
  private SendBusinessRules communication;

  private AgentHandler agentHandler;

  private String script = "If 5 < 10 ( order = 20 )";

  @Test
  void getBusinessRules_should_returnBusinessRulesFromRightPlayer()
      throws UnknownGameException, NoSuchFieldException, IllegalAccessException, AgentException {

    setUpForSetBusinessRules();
    when(gameDataAccess.getBusinessRules(any())).thenReturn(script);

    assertEquals(agentHandler.getBusinessRules(business), script);
  }

  @Test
  void setBusinessRules_should_callGameDataStorage()
      throws AgentException, NoSuchFieldException, IllegalAccessException {

    setUpForSetBusinessRules();
    final BusinessRules businessRules = new BusinessRules(business, script);

    doNothing().when(communication).sendBusinessRules(any());
    agentHandler.setBusinessRules(businessRules);
    verify(communication, times(1)).sendBusinessRules(any());
  }

  @Test
  void setBusinessRules_should_throwScriptErrorException_when_scriptIsNull()
      throws NoSuchFieldException, IllegalAccessException {

    setUpForSetBusinessRules();
    final BusinessRules businessRules = new BusinessRules(business, null);

    assertThrows(AgentException.class, () -> agentHandler.setBusinessRules(businessRules));
  }

  @Test
  void executeAgent_should_callCorrectAgentRunner_when_businessIsInMap()
      throws UnknownException, AgentException, NoSuchFieldException, IllegalAccessException {
    final Map<Business, AgentRunner> agentRunnerMap = new HashMap<>();
    agentRunnerMap.put(business, otherAgentRunner);

    mockAndExecuteAgentHandler(agentRunnerMap, false);

    verify(otherAgentRunner, atLeastOnce()).playTurn(any());
    verify(defaultAgentRunner, never()).playTurn(any());
  }

  @Test
  void executeAgent_should_callCorrectAgentRunner_when_noBusinessIsInMap()
      throws UnknownException, AgentException, NoSuchFieldException, IllegalAccessException {
    final Map<Business, AgentRunner> agentRunnerMap = new HashMap<>();

    mockAndExecuteAgentHandler(agentRunnerMap, false);

    verify(defaultAgentRunner, atLeastOnce()).playTurn(any());
    verify(otherAgentRunner, never()).playTurn(any());
  }

  @Test
  void executeAgent_should_setDefaultAgentRunnerAndCallIt_when_noBusinessIsInMap()
      throws UnknownException, AgentException, NoSuchFieldException, IllegalAccessException {
    final Map<Business, AgentRunner> agentRunnerMap = new HashMap<>();

    mockAndExecuteAgentHandler(agentRunnerMap, true);

    verify(otherAgentRunner, never()).playTurn(any());
  }

  @Test
  void executeAgent_should_callAgentRunnerPlayTurnWithCorrectBusinessInfo()
      throws AgentException, UnknownException, NoSuchFieldException, IllegalAccessException {
    final Map<Business, AgentRunner> agentRunnerMap = new HashMap<>();
    agentRunnerMap.put(business, otherAgentRunner);

    mockAndExecuteAgentHandler(agentRunnerMap, false);

    final Map<String, Integer> expectedBusinessInfo = new HashMap<>();
    expectedBusinessInfo.put("round", 5);
    expectedBusinessInfo.put("stock", 40);
    expectedBusinessInfo.put("openorders", 0);
    expectedBusinessInfo.put("outgoinggoods", 10);
    expectedBusinessInfo.put("incominggoods", 10);
    expectedBusinessInfo.put("incomingorders", 10);
    expectedBusinessInfo.put("budget", 10);
    expectedBusinessInfo.put("incominggoodsprice", 10);
    expectedBusinessInfo.put("outgoinggoodsprice", 10);
    expectedBusinessInfo.put("stockprice", 10);
    expectedBusinessInfo.put("openordersprice", 10);

    verify(otherAgentRunner, atLeastOnce()).playTurn(expectedBusinessInfo);
  }

  private void setUpForSetBusinessRules()
      throws NoSuchFieldException, IllegalAccessException {

    agentHandler = new DefaultAgentHandler();

    final Field gameDataAccessField = agentHandler.getClass().getDeclaredField("gameDataAccess");
    gameDataAccessField.setAccessible(true);
    gameDataAccessField.set(agentHandler, gameDataAccess);

    final Field communicationField = agentHandler.getClass().getDeclaredField("communication");
    communicationField.setAccessible(true);
    communicationField.set(agentHandler, communication);
  }

  private void mockAndExecuteAgentHandler(Map<Business, AgentRunner> agentRunnerMap,
      boolean testingDefaultAgentRunner)
      throws UnknownException, AgentException, NoSuchFieldException, IllegalAccessException {

    when(stateCalculator.getBusinessStateForBusiness(business.getUUID(), 5))
        .thenReturn(new BusinessState(40, 0, 10, 10, 10, 10, 10, 0, 0));
    when(gameConfig.getGamePricesForBusiness(any())).thenReturn(new GamePrices(10, 10, 10, 10));

    if (testingDefaultAgentRunner) {
      final StatementContainer root = new StatementContainer(0, 0);
      final VariableAssignment order = new VariableAssignment("order", 0, 0);
      final MathOperation multiplyMathOperation = new MathOperation(MathOperatorType.MULTIPLY, 0,
          0);
      multiplyMathOperation.addChild(new IntLiteral(2, 0, 0));
      multiplyMathOperation.addChild(new IntLiteral(4, 0, 0));

      order.addChild(multiplyMathOperation);
      root.addChild(order);

      when(astHandler.build(anyString())).thenReturn(new AST(root));
    }

    agentHandler = new DefaultAgentHandler();

    if (!testingDefaultAgentRunner) {
      final Field defaultAgentRunnerField = agentHandler.getClass()
          .getDeclaredField("defaultAgentRunner");
      defaultAgentRunnerField.setAccessible(true);
      defaultAgentRunnerField.set(agentHandler, defaultAgentRunner);
    } else {
      final Field astHandlerField = agentHandler.getClass().getDeclaredField("astHandler");
      astHandlerField.setAccessible(true);
      astHandlerField.set(agentHandler, astHandler);
    }

    final Field agentRunnerMapField = agentHandler.getClass().getDeclaredField("agentRunnerMap");
    agentRunnerMapField.setAccessible(true);
    agentRunnerMapField.set(agentHandler, agentRunnerMap);

    final Field stateCalculatorField = agentHandler.getClass().getDeclaredField("stateCalculator");
    stateCalculatorField.setAccessible(true);
    stateCalculatorField.set(agentHandler, stateCalculator);

    final Field gameConfigField = agentHandler.getClass().getDeclaredField("gameConfig");
    gameConfigField.setAccessible(true);
    gameConfigField.set(agentHandler, gameConfig);

    agentHandler.executeAgent(business, 5);
  }
}
