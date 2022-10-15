package nl.ica.asd.logic.gamemanager.gamestatemanager.businessinitializer;

import static org.junit.jupiter.api.Assertions.assertThrows;

import nl.ica.asd.logic.domain.SupplyChainType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BusinessInitializerContextTest {

  private BusinessInitializerContext businessInitializerContext;

  @BeforeEach
  public void setup() {
    businessInitializerContext = new BusinessInitializerContext();
  }

  @Test
  public void setBusinessInitializer_should_throwUnsupportedOperationException_when_typeIsNetwork() {
    assertThrows(UnsupportedOperationException.class,
        () -> businessInitializerContext.setBusinessInitializer(SupplyChainType.NETWORK));
  }

  @Test
  public void setBusinessInitializer_should_throwNullPointerException_when_typeIsNull() {
    assertThrows(NullPointerException.class,
        () -> businessInitializerContext.setBusinessInitializer(null));
  }
}
