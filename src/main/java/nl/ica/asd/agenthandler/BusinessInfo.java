package nl.ica.asd.agenthandler;

public enum BusinessInfo {
  ROUND("round"),
  STOCK("stock"),
  OPENORDERS("openorders"),
  OUTGOINGGOODS("outgoinggoods"),
  INCOMINGGOODS("incominggoods"),
  INCOMINGORDERS("incomingorders"),
  BUDGET("budget"),
  INCOMINGGOODSPRICE("incominggoodsprice"),
  OUTGOINGGOODSPRICE("outgoinggoodsprice"),
  STOCKPRICE("stockprice"),
  OPENORDERSPRICE("openordersprice");

  private String key;

  BusinessInfo(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  public static boolean contains(String searchedKey) {
    for (BusinessInfo b : BusinessInfo.values()) {
      if (b.getKey().equals(searchedKey)) {
        return true;
      }
    }
    return false;
  }
}
