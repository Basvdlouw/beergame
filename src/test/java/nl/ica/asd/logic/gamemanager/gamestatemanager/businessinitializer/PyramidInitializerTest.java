package nl.ica.asd.logic.gamemanager.gamestatemanager.businessinitializer;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import nl.ica.asd.logic.domain.BeerGame;
import nl.ica.asd.logic.domain.Business;
import nl.ica.asd.logic.domain.SupplyChainType;
import nl.ica.asd.logic.domain.businesses.FactoryBusiness;
import nl.ica.asd.logic.domain.businesses.RegionalWarehouseBusiness;
import nl.ica.asd.logic.domain.businesses.RetailBusiness;
import nl.ica.asd.logic.domain.businesses.WholesaleBusiness;
import nl.ica.asd.storage.exception.UnknownGameException;
import nl.ica.asd.util.TestHelpers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PyramidInitializerTest {

  private static final BusinessInitializerContext businessInitializerContext = new BusinessInitializerContext();

  @BeforeAll
  public static void init() {
    businessInitializerContext.setBusinessInitializer(SupplyChainType.PYRAMID);
  }

  @Test
  void initialize_should_throwException_whenBusinessesAndStatesAreNull()
      throws UnknownGameException {
    BeerGame beerGame = TestHelpers.createTestingBeerGame();
    assertThrows(Exception.class, () -> businessInitializerContext.initialize(null, null));
  }

  @Test
  void initialize_should_throwException_whenBusinessesNull()
      throws UnknownGameException {
    BeerGame beerGame = TestHelpers.createTestingBeerGame();
    assertThrows(Exception.class,
        () -> businessInitializerContext.initialize(beerGame, null));
  }

  @Test
  void initialize_should_throwException_whenBusinessStateNull()
      throws UnknownGameException {
    BeerGame beerGame = TestHelpers.createTestingBeerGame();
    assertThrows(Exception.class, () -> businessInitializerContext.initialize(null,
        beerGame.getBusinesses()));
  }

//  @Test
//  void initialize_should_listWithBusinesses_whenCalled()
//      throws UnknownGameException {
//    BeerGame beerGame = TestHelpers.createTestingBeerGame();
//
//    List<Business> businessList = businessInitializerContext.initialize(beerGame,
//        beerGame.getBusinesses());
//
//    assertNotNull(businessList);
//
//    //test the factory
//    FactoryBusiness factoryBusiness = (FactoryBusiness) businessList.get(0);
//    assertNotNull(factoryBusiness);
//    assertTrue(factoryBusiness instanceof FactoryBusiness);
//
//    //test regionalwarehouse under the factory
//    RegionalWarehouseBusiness regionalWarehouseBusiness = (RegionalWarehouseBusiness) businessList
//        .get(1);
//    assertNotNull(regionalWarehouseBusiness);
//    assertTrue(regionalWarehouseBusiness instanceof RegionalWarehouseBusiness);
//
//    //test wholesale under the regionalwarehouse
//    WholesaleBusiness wholesaleBusiness = (WholesaleBusiness) businessList.get(2);
//    assertNotNull(wholesaleBusiness);
//    assertTrue(wholesaleBusiness instanceof WholesaleBusiness);
//
//    //test retail under the wholesale
//    RetailBusiness retailBusiness = (RetailBusiness) businessList.get(3);
//    assertNotNull(regionalWarehouseBusiness);
//    assertTrue(retailBusiness instanceof RetailBusiness);
//  }
}
