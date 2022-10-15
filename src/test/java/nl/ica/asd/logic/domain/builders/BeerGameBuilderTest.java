package nl.ica.asd.logic.domain.builders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.GregorianCalendar;
import nl.ica.asd.logic.domain.BeerGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class BeerGameBuilderTest {

  @Mock
  private BeerGame beerGame;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.initMocks(this);
    when(beerGame.getName()).thenReturn("BeerTestingGame");
    when(beerGame.getDateTime()).thenReturn(new GregorianCalendar(2018, 2, 12).getTime());
  }

  @Test
  void aBeerGame_should_returnNotNull() {
    assertNotNull(BeerGameBuilder.aBeerGame());
  }


  @Test
  void when_changedVariablesInBuilder_fromBaseBeerGame_should_buildBeerGameWithNewVariables() {
    final String newName = new StringBuilder(beerGame.getName()).reverse().toString();
    final BeerGame buildBeerGame = BeerGameBuilder.fromBaseBeerGame(beerGame).withName(newName)
        .build();
    assertNotEquals(beerGame.getName(), buildBeerGame.getName(),
        "names of beergames should not be the same");
    assertEquals(newName, buildBeerGame.getName(),
        String.format("new beergame name should be %s", newName));
    assertEquals(beerGame.getDateTime(), buildBeerGame.getDateTime(),
        "date is altered but should be the same");
  }
}
