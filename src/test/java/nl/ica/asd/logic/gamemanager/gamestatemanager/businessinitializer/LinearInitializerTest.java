package nl.ica.asd.logic.gamemanager.gamestatemanager.businessinitializer;

import static nl.ica.asd.util.assertionsHelpers.assertEmpty;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import nl.ica.asd.agenthandler.AgentHandler;
import nl.ica.asd.agenthandler.exceptions.AgentException;
import nl.ica.asd.logic.domain.BeerGame;
import nl.ica.asd.logic.domain.Business;
import nl.ica.asd.util.TestHelpers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LinearInitializerTest {

  private BeerGame beerGame;
  private List<Business> businesses;

  private AgentHandler agentHandler;

  private LinearInitializer linearInitializer;

  @BeforeEach
  public void setup() throws NoSuchFieldException, IllegalAccessException {
    agentHandler = mock(AgentHandler.class);
    beerGame = TestHelpers.createTestingBeerGame();
    businesses = beerGame.getBusinesses();
    linearInitializer = new LinearInitializer();

    final Field agentHandlerField = linearInitializer.getClass().getDeclaredField("agentHandler");
    agentHandlerField.setAccessible(true);
    agentHandlerField.set(linearInitializer, agentHandler);
  }

  @Test
  public void initialize_should_returnEmptyList_when_exceptionIsThrown() throws AgentException {
    when(agentHandler.getDefaultBusinessRules()).thenThrow(AgentException.class);
    assertEmpty(linearInitializer.initialize(beerGame, Collections.emptyList()));
  }

//  @Test
//  public void initialize_should_returnListWithFactoryAndWholesaleAgent_when_warehouseAndRetailAreSet()
//      throws AgentException {
//    final int chosenBusinessIndexOne = 1;
//    final int chosenBusinessIndexTwo = 3;
//    final int missingBusinessIndexOne = 0;
//    final int missingBusinessIndexTwo = 2;
//
//    when(agentHandler.getDefaultBusinessRules()).thenReturn("order = 10");
//    final List<Business> initialBusinesses = new ArrayList<>();
//    initialBusinesses.add(businesses.get(chosenBusinessIndexOne));
//    initialBusinesses.add(businesses.get(chosenBusinessIndexTwo));
//
//    final List<Business> resultBusinesses = linearInitializer
//        .initialize(beerGame, initialBusinesses);
//
//    assertNotEmpty(resultBusinesses);
//
//    assertPresent(businesses.get(chosenBusinessIndexOne), resultBusinesses);
//    assertPresent(businesses.get(chosenBusinessIndexTwo), resultBusinesses);
//    assertNotPresent(businesses.get(missingBusinessIndexOne), resultBusinesses);
//    assertNotPresent(businesses.get(missingBusinessIndexTwo), resultBusinesses);
//
//    assertNotNull(resultBusinesses.get(chosenBusinessIndexOne).getPlayer());
//    assertNotNull(resultBusinesses.get(chosenBusinessIndexTwo).getPlayer());
//    assertNull(resultBusinesses.get(missingBusinessIndexOne).getPlayer());
//    assertNull(resultBusinesses.get(missingBusinessIndexTwo).getPlayer());
//  }
}
