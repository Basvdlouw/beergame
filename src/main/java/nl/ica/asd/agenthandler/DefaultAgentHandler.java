package nl.ica.asd.agenthandler;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import nl.ica.asd.agenthandler.exceptions.AgentException;
import nl.ica.asd.logic.domain.Business;
import nl.ica.asd.logic.domain.BusinessRules;
import nl.ica.asd.logic.statecalculator.BusinessState;
import nl.ica.asd.logic.statecalculator.StateCalculator;
import nl.ica.asd.logic.watchers.BusinessrulesChangedWatcher;
import nl.ica.asd.logic.watchers.events.BusinessrulesChangedEvent;
import nl.ica.asd.network.communication.interfaces.SendBusinessRules;
import nl.ica.asd.storage.domain.GamePrices;
import nl.ica.asd.storage.exception.UnknownException;
import nl.ica.asd.storage.exception.UnknownGameException;
import nl.ica.asd.storage.gameconfig.GameConfig;
import nl.ica.asd.storage.gamedatastorage.GameDataAccess;

@Named("AgentHandler")
@Singleton
public class DefaultAgentHandler implements AgentHandler {

  private static List<BusinessrulesChangedWatcher> businessrulesChangedWatchers = new ArrayList<>();
  private Map<Business, AgentRunner> agentRunnerMap = new HashMap<>();
  private AgentRunner defaultAgentRunner;
  private ASTHandler astHandler;

  @Inject
  private GameConfig gameConfig;

  @Inject
  private StateCalculator stateCalculator;

  @Inject
  private GameDataAccess gameDataAccess;

  @Inject
  private SendBusinessRules communication;

  DefaultAgentHandler() {
    this.astHandler = new ASTHandler();
  }

  @Override
  public String getBusinessRules(Business business) throws AgentException {
    try {
      final String businessRules = gameDataAccess.getBusinessRules(business);
      if (businessRules != null) {
        return businessRules;
      }
      return getDefaultBusinessRules();
    } catch (UnknownGameException e) {
      throw new AgentException("Failed to fetch business rules from data access.", e);
    }
  }

  @Override
  public String getDefaultBusinessRules() throws AgentException {
    StringBuilder result = new StringBuilder();

    final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    final URL url = classLoader.getResource("defaultBusinessRules.txt");

    if (url == null) {
      throw new AgentException("Default Business Rules not found.");
    }
    File file;
    try {
      file = new File(URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8.toString()));
    } catch (UnsupportedEncodingException e) {
      throw new AgentException("Failed to read Default Business Rules: ", e);
    }

    try (Scanner scanner = new Scanner(file)) {
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        result.append(line).append("\n");
      }
    } catch (IOException e) {
      throw new AgentException("Failed to read Default Business Rules: ", e);
    }

    return result.toString();
  }

  @Override
  public void validateBusinessRules(String script) throws AgentException {
    buildAst(script);
  }

  @Override
  public void setBusinessRules(BusinessRules businessRules) throws AgentException {
    validateBusinessRules(businessRules.getScript());
    communication.sendBusinessRules(businessRules);
  }

  private static void onBusinessrulesChanged(BusinessrulesChangedEvent event) {
    DefaultAgentHandler.businessrulesChangedWatchers
        .forEach(watcher -> watcher.onBusinessrulesChanged(event));
  }

  @Override
  public int executeAgent(Business business, int round) throws AgentException {
    try {
      int order = getAgentRunner(business).playTurn(createBusinessInfo(business, round));
      if (order < 0) {
        return 0;
      } else {
        return order;
      }
    } catch (UnknownException e) {
      throw new AgentException("Failed to fetch business info for business.", e);
    }
  }

  private AST buildAst(String script) throws AgentException {
    try {
      return astHandler.build(script);
    } catch (NullPointerException e) {
      throw new AgentException("An unexpected exception occurred.", e);
    }
  }

  private AgentRunner getAgentRunner(Business business) throws AgentException {
    if (defaultAgentRunner == null) {
      final String defaultBusinessRules = getDefaultBusinessRules();
      final AST defaultAST = astHandler.build(defaultBusinessRules);
      this.defaultAgentRunner = new AgentRunner(defaultAST);
    }
    return agentRunnerMap.getOrDefault(business, defaultAgentRunner);
  }

  public static void subscribeBusinessrulesChanged(BusinessrulesChangedWatcher watcher) {
    DefaultAgentHandler.businessrulesChangedWatchers.add(watcher);
  }

  public static void unsubscribeBusinessrulesChanged(BusinessrulesChangedWatcher watcher) {
    DefaultAgentHandler.businessrulesChangedWatchers.remove(watcher);
  }

  @Override
  public void saveBusinessRules(BusinessRules businessRules) throws AgentException {
    try {
      final AST ast = buildAst(businessRules.getScript());
      gameDataAccess.saveBusinessRules(businessRules);
      agentRunnerMap.put(businessRules.getBusiness(), new AgentRunner(ast));
      onBusinessrulesChanged(new BusinessrulesChangedEvent());
    } catch (UnknownException e) {
      throw new AgentException(
          "Unexpected exception when saving business rules to data access.", e
      );
    }
  }

  private Map<String, Integer> createBusinessInfo(Business business, int round)
      throws UnknownException {
    final Map<String, Integer> businessInfo = new HashMap<>();

    businessInfo.put(BusinessInfo.ROUND.getKey(), round);

    final BusinessState businessState = stateCalculator
        .getBusinessStateForBusiness(business.getUUID(), round);
    businessInfo.put(BusinessInfo.STOCK.getKey(), businessState.getStock());
    businessInfo.put(BusinessInfo.OPENORDERS.getKey(), businessState.getOpenOrders());
    businessInfo.put(BusinessInfo.OUTGOINGGOODS.getKey(), businessState.getOutgoingGoods());
    businessInfo.put(BusinessInfo.INCOMINGGOODS.getKey(), businessState.getIncomingGoods());
    businessInfo.put(BusinessInfo.INCOMINGORDERS.getKey(), businessState.getIncomingOrders());
    businessInfo.put(BusinessInfo.BUDGET.getKey(), businessState.getBudget());

    final GamePrices gamePrices = gameConfig.getGamePricesForBusiness(business.getBusinessType());
    businessInfo.put(BusinessInfo.INCOMINGGOODSPRICE.getKey(), gamePrices.getIncomingGoodsPrice());
    businessInfo.put(BusinessInfo.OUTGOINGGOODSPRICE.getKey(), gamePrices.getOutgoingGoodsPrice());
    businessInfo.put(BusinessInfo.STOCKPRICE.getKey(), gamePrices.getStockPrice());
    businessInfo.put(BusinessInfo.OPENORDERSPRICE.getKey(), gamePrices.getOpenOrdersPrice());

    return businessInfo;
  }

  @Override
  public void delete() {
    agentRunnerMap.clear();
  }

  public void reset(){
    businessrulesChangedWatchers.clear();
    agentRunnerMap.clear();
  }
}


