package nl.ica.asd.logic.gamemanager.gamestatemanager.businessinitializer;

import java.util.Collections;
import java.util.List;
import javax.validation.constraints.NotNull;
import nl.ica.asd.logic.domain.BeerGame;
import nl.ica.asd.logic.domain.Business;
import nl.ica.asd.logic.domain.SupplyChainType;

public class BusinessInitializerContext {

  private BusinessInitializer businessInitializer;

  public void setBusinessInitializer(@NotNull SupplyChainType supplyChainType) {
    switch (supplyChainType) {
      case LINEAR:
        businessInitializer = new LinearInitializer().init();
        break;
      case PYRAMID:
        businessInitializer = new PyramidInitializer().init();
        break;
      case NETWORK:
        throw new UnsupportedOperationException();
      default:
        businessInitializer = null;
    }
  }

  public List<Business> initialize(BeerGame beerGame, List<Business> businesses) {
    if (businessInitializer != null) {
      List<Business> businessList = businessInitializer.initialize(beerGame, businesses);
      System.out.println("BusinessInit context sizelist:" +businessList.size());
      System.out.println(businessList.get(0).getPlayer());
      return businessList;
    }
    return Collections.emptyList();
  }
}
