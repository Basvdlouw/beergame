package nl.ica.asd.agenthandler;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import javax.inject.Inject;
import nl.ica.asd.agenthandler.exceptions.AgentException;
import nl.ica.asd.agenthandler.exceptions.ScriptErrorException;
import nl.ica.asd.logic.domain.Business;
import nl.ica.asd.logic.domain.BusinessRules;
import nl.ica.asd.logic.statecalculator.BusinessState;
import nl.ica.asd.logic.statecalculator.StateCalculator;
import nl.ica.asd.network.communication.interfaces.SendBusinessRules;
import nl.ica.asd.storage.domain.GamePrices;
import nl.ica.asd.storage.exception.UnknownBusinessException;
import nl.ica.asd.storage.exception.UnknownGameException;
import nl.ica.asd.storage.gameconfig.GameConfig;
import nl.ica.asd.storage.gamedatastorage.GameDataAccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AgentHandlerComponentTest {

  @Inject
  private AgentHandler agentHandler;

  @Mock
  private GameDataAccess gameDataAccess;

  @Mock
  private StateCalculator stateCalculator;

  @Mock
  private GameConfig gameConfig;

  @Mock
  private Business business;

  @Mock
  private SendBusinessRules communication;

  @BeforeEach
  void setUp() throws NoSuchFieldException, IllegalAccessException {
    agentHandler = new DefaultAgentHandler();

    final Field gameDataAccessField = agentHandler.getClass().getDeclaredField("gameDataAccess");
    gameDataAccessField.setAccessible(true);
    gameDataAccessField.set(agentHandler, gameDataAccess);

    final Field stateCalculatorField = agentHandler.getClass().getDeclaredField("stateCalculator");
    stateCalculatorField.setAccessible(true);
    stateCalculatorField.set(agentHandler, stateCalculator);

    final Field gameConfigField = agentHandler.getClass().getDeclaredField("gameConfig");
    gameConfigField.setAccessible(true);
    gameConfigField.set(agentHandler, gameConfig);

    final Field communicationField = agentHandler.getClass().getDeclaredField("communication");
    communicationField.setAccessible(true);
    communicationField.set(agentHandler, communication);
  }

  @Test
  void agentHandler_should_returnBusinessRules_when_businessHasOwnRules()
      throws AgentException, UnknownGameException {

    final String expected = "test";

    when(gameDataAccess.getBusinessRules(business)).thenReturn(expected);

    final String result = agentHandler.getBusinessRules(business);

    assertEquals(expected, result);
  }

  @Test
  void agentHandler_should_returnDefaultBusinessRules_when_businessDoesNotHaveOwnRules()
      throws AgentException {

    final String expected = "Order = 35 - (Stock - OpenOrders + IncomingGoods - IncomingOrders)\n"
        + "\n"
        + "if Order * IncomingGoodsPrice > Budget (\n"
        + "    Order = (Budget / IncomingGoodsPrice) - 3\n"
        + ")\n";

    final String result = agentHandler.getBusinessRules(business);

    assertEquals(expected, result);
  }

  @Test
  void agentHandler_should_sendBusinessRulesToGameDataAccess_when_businessRulesAreCorrect()
      throws AgentException {
    final String script = "if 20 > 5 and (true or false) (\n"
        + "    order = 5\n"
        + ") else (\n"
        + "    if true (\n"
        + "        order = 2 * 4 / 30 + 4 - 13\n"
        + "    )\n"
        + ")";
    final BusinessRules businessRules = new BusinessRules(business, script);
    agentHandler.setBusinessRules(businessRules);

    verify(communication, times(1)).sendBusinessRules(businessRules);
  }

  @Test
  void agentHandler_should_throwScriptErrorException_when_businessRulesAreIncorrectOnSet() {
    final String script = "Dit is een fout script";
    final BusinessRules businessRules = new BusinessRules(business, script);

    assertThrows(ScriptErrorException.class, () -> agentHandler.setBusinessRules(businessRules));
  }

  @Test
  void agentHandler_should_executeDefaultAgent_when_businessHasNoOwnBusinessRules()
      throws AgentException, UnknownBusinessException, UnknownGameException {
    final int round = 5;
    when(stateCalculator.getBusinessStateForBusiness(business.getUUID(), round))
        .thenReturn(new BusinessState(20, 0, 20, 10, 20, 1000000000, 3, 0, 0));
    when(gameConfig.getGamePricesForBusiness(business.getBusinessType()))
        .thenReturn(new GamePrices(70, 100, 15, 20));

    assertEquals(25, agentHandler.executeAgent(business, round));
  }

  @Test
  void agentHandler_should_executeBusinessAgent_when_businessHasItsOwnBusinessRules()
      throws AgentException, UnknownBusinessException, UnknownGameException {
    final int round = 5;
    final String script = "if 20 > 5 and (true or false) (\n"
        + "    order = 40\n"
        + ") else (\n"
        + "    if true (\n"
        + "        order = 2 * 4 / 30 + 4 - 13\n"
        + "    )\n"
        + ")";
    final BusinessRules businessRules = new BusinessRules(business, script);
    agentHandler.saveBusinessRules(businessRules);
    when(stateCalculator.getBusinessStateForBusiness(business.getUUID(), round))
        .thenReturn(new BusinessState(40, 0, 20, 10, 20, 1000000000, 3, 0, 0));
    when(gameConfig.getGamePricesForBusiness(business.getBusinessType()))
        .thenReturn(new GamePrices(70, 100, 15, 20));

    assertEquals(40, agentHandler.executeAgent(business, round));
  }

  @Test
  void agentHandler_should_notThrowException_when_businessRulesAreCorrectOnValidate() {
    final String script = "if 20 > 5 and (true or false) (\n"
        + "    order = 40\n"
        + ") else (\n"
        + "    if true (\n"
        + "        order = 2 * 4 / 30 + 4 - 13\n"
        + "    )\n"
        + ")";

    assertDoesNotThrow(() -> agentHandler.validateBusinessRules(script));
  }

  @Test
  void agentHandler_should_throwScriptErrorException_when_businessRulesAreIncorrectOnValidate() {
    final String script = "Okee Wimell! Veel plezier met je muziek";

    assertThrows(ScriptErrorException.class, () -> agentHandler.validateBusinessRules(script));
  }
}
