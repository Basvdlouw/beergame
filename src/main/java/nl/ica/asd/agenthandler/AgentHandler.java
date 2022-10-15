package nl.ica.asd.agenthandler;

import nl.ica.asd.agenthandler.exceptions.AgentException;
import nl.ica.asd.logic.domain.Business;
import nl.ica.asd.logic.domain.BusinessRules;

public interface AgentHandler {

  String getBusinessRules(Business business) throws AgentException;

  String getDefaultBusinessRules() throws AgentException;

  void validateBusinessRules(String script) throws AgentException;

  void setBusinessRules(BusinessRules businessRules) throws AgentException;

  void saveBusinessRules(BusinessRules businessRules) throws AgentException;

  int executeAgent(Business business, int round) throws AgentException;

  void delete();

  void reset();

}
