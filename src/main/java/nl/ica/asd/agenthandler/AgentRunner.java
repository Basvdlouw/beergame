package nl.ica.asd.agenthandler;

import java.util.Map;

class AgentRunner {

  private final AST ast;

  AgentRunner(AST ast) {
    this.ast = ast;
  }

  int playTurn(Map<String, Integer> businessInfo) {
    ast.getRoot().execute(businessInfo);
    return businessInfo.getOrDefault("order", 0);
  }

}
