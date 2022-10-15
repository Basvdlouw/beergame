package nl.ica.asd.logic.gamemanager.ordermanager;

import static nl.ica.asd.util.MockitoHelpers.once;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import nl.ica.asd.logic.domain.Action;
import nl.ica.asd.logic.domain.BeerGame;
import nl.ica.asd.logic.domain.Business;
import nl.ica.asd.logic.domain.actions.DeliveryAction;
import nl.ica.asd.logic.domain.actions.OrderAction;
import nl.ica.asd.logic.domain.businesses.FactoryBusiness;
import nl.ica.asd.logic.domain.businesses.RegionalWarehouseBusiness;
import nl.ica.asd.logic.domain.businesses.RetailBusiness;
import nl.ica.asd.logic.domain.businesses.WholesaleBusiness;
import nl.ica.asd.storage.exception.UnknownBusinessException;
import nl.ica.asd.storage.exception.UnknownGameException;
import nl.ica.asd.storage.gamedatastorage.GameDataAccess;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DefaultOrderCalculatorTest {

  @Mock
  private GameDataAccess gameDataAccess;

  @InjectMocks
  private DefaultOrderCalculator orderCalculator;

  @Test
  void splitOrderAction_should_listOf10Actions_when_retailHas3Upper()
      throws UnknownBusinessException, UnknownGameException {
    final int amount = 3;
    final int nOrder = 100;

    final List<WholesaleBusiness> wholesaleBusinesses = new ArrayList<>();
    for (int i = 0; i < amount; i++) {
      final UUID uuid = UUID.randomUUID();
      final WholesaleBusiness business = new WholesaleBusiness(10 * i, 10 * i, null, null, uuid,
          null, null);
      wholesaleBusinesses.add(business);
    }

    final UUID retailUUID = UUID.randomUUID();
    final Business retail = new RetailBusiness(10, 10, null, null, retailUUID, null,
        wholesaleBusinesses, UUID.randomUUID());

    doReturn(retail).when(gameDataAccess).getBusinessByUUID(retailUUID);

    final List<OrderAction> result = orderCalculator
        .splitOrderAction(new OrderAction(nOrder, retail.getUUID(), null));

    int resultSum = 0;

    for (Action action : result) {
      resultSum += action.getAmount();
    }
    assertEquals(nOrder, resultSum);
    assertEquals(amount, result.size());
  }

  @Test
  void splitOrderAction_should_listOf10Actions_when_wholesaleHas2Upper()
      throws UnknownBusinessException, UnknownGameException {
    final int amount = 2;
    final int nOrder = 100;

    final List<RegionalWarehouseBusiness> regionalWarehouseBusinesses = new ArrayList<>();
    for (int i = 0; i < amount; i++) {
      final UUID uuid = UUID.randomUUID();
      final RegionalWarehouseBusiness business = new RegionalWarehouseBusiness(10 * i, 10 * i, null,
          null,
          uuid, null, null);
      regionalWarehouseBusinesses.add(business);
    }

    final UUID uuid = UUID.randomUUID();
    final Business wholesale = new WholesaleBusiness(10, 10, null, null, uuid, null,
        regionalWarehouseBusinesses);

    doReturn(wholesale).when(gameDataAccess).getBusinessByUUID(uuid);

    final List<OrderAction> result = orderCalculator
        .splitOrderAction(new OrderAction(nOrder, wholesale.getUUID(), null));

    int resultSum = 0;

    for (Action action : result) {
      resultSum += action.getAmount();
    }
    assertEquals(nOrder, resultSum);
    assertEquals(amount, result.size());
  }

  @Test
  void splitOrderAction_should_listOf10Actions_when_retailHas2Upper()
      throws UnknownBusinessException, UnknownGameException {

    final int amount = 2;
    final int nOrder = 100;

    final List<FactoryBusiness> factoryBusinesses = new ArrayList<>();
    for (int i = 0; i < amount; i++) {
      final FactoryBusiness business = new FactoryBusiness(10 * i, 10 * i, null, null,
          UUID.randomUUID(), null,
          UUID.randomUUID());
      factoryBusinesses.add(business);
    }

    final UUID uuid = UUID.randomUUID();
    final Business regional = new RegionalWarehouseBusiness(10, 10, null, null, uuid,
        null,
        factoryBusinesses);

    doReturn(regional).when(gameDataAccess).getBusinessByUUID(uuid);

    final List<OrderAction> result = orderCalculator
        .splitOrderAction(new OrderAction(nOrder, regional.getUUID(), null));

    int resultSum = 0;

    for (Action action : result) {
      resultSum += action.getAmount();
    }
    assertEquals(nOrder, resultSum);
    assertEquals(amount, result.size());
  }

//  @Test
//  void calculateDeliveryActionsFirstInFirstOut_should_returnEmptyListOfDeliveryOrders_when_noneAreFound()
//      throws UnknownGameException {
//    final List<Action> orderActions = new ArrayList<>();
//
//    when(gameDataAccess.getBusinesses()).thenReturn(new ArrayList<>());
//
//    final List<DeliveryAction> deliveryActions = orderCalculator
//        .calculateDeliveryActionsFirstInFirstOut(null, orderActions, false);
//
//    assertEquals(0, deliveryActions.size());
//    verify(gameDataAccess, once()).getBusinesses();
//  }

//  @Test
//  void calculateDeliveryActionsFirstInFirstOut_should_returnOneDeliveryOrderForEveryBusiness()
//      throws UnknownGameException, UnknownBusinessException {
//    final BeerGame fullBeerGame = TestHelpers.createTestingBeerGame();
//    final List<Action> orderActions = new ArrayList<>(); //getOrderActionsFromGame(fullBeerGame);
//    when(gameDataAccess.getBusinesses()).thenReturn(fullBeerGame.getBusinesses());
//
//    final List<DeliveryAction> deliveryActions = orderCalculator
//        .calculateDeliveryActionsFirstInFirstOut(null, orderActions, false);
//
//    assertEquals(4, deliveryActions.size());
//    verify(gameDataAccess, once()).getBusinesses();
//  }

//  @Test
//  void calculateDeliveryActionsEvenly_should_returnEmptyListOfDeliveryOrders_when_noneAreFound()
//      throws UnknownGameException {
//    final List<OrderAction> orderActions = new ArrayList<>();
//
//    when(gameDataAccess.getBusinesses()).thenReturn(new ArrayList<>());
//
//    final List<DeliveryAction> deliveryActions = orderCalculator
//        .calculateDeliveryActionsEvenly(orderActions);
//
//    assertEquals(0, deliveryActions.size());
//    verify(gameDataAccess, once()).getBusinesses();
//  }

//  @Test
//  void calculateDeliveryActionsEvenly_should_ReturnOneDeliveryOrderForEveryBusiness()
//      throws UnknownGameException, UnknownBusinessException {
//    final BeerGame fullBeerGame = TestHelpers.createTestingBeerGame();
//    final List<OrderAction> orderActions = new ArrayList<>();// getOrderActionsFromGame(fullBeerGame);
//    when(gameDataAccess.getBusinesses()).thenReturn(fullBeerGame.getBusinesses());
//
//    final List<DeliveryAction> deliveryActions = orderCalculator
//        .calculateDeliveryActionsEvenly(orderActions);
//
//    assertEquals(4, deliveryActions.size());
//    verify(gameDataAccess, once()).getBusinesses();
//  }

  private List<OrderAction> getOrderActionsFromGame(@NotNull BeerGame beerGame)
      throws UnknownBusinessException, UnknownGameException {
    final List<OrderAction> orderActions = new ArrayList<>();
    for (Business business : beerGame.getBusinesses()) {

      for (Action action : business.getActionsPerRound().get(beerGame.getCurrentRound() - 1)) {
        if (action instanceof OrderAction) {
          orderActions.add((OrderAction) action);
        }
      }
    }
    return orderActions;
  }
}