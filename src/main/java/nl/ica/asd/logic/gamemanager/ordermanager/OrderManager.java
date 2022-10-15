package nl.ica.asd.logic.gamemanager.ordermanager;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import nl.ica.asd.agenthandler.exceptions.AgentException;
import nl.ica.asd.logic.domain.Action;
import nl.ica.asd.storage.exception.UnknownGameException;

public interface OrderManager {

  void placeOrder(String username, int amount);

  void saveCurrentOrderActions(List<? extends Action> actions) throws UnknownGameException;

  Map<Integer, List<Action>> getAllActionsBusiness(UUID uuid);

  void saveActions(Map<Integer, List<Action>> businessActionsMap);

  void processAgentOrders() throws UnknownGameException, AgentException;

  void processRetailOrders() throws UnknownGameException;

  void checkOrderProgress();
}
