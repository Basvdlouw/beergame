package nl.ica.asd.logic.gamemanager.gamestatemanager;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import nl.ica.asd.logic.domain.GameState;

@ApplicationScoped
public class GameStateManagerFactory {

  @Inject
  @Named("SetupManager")
  private GameStateManager setupManager;
  @Inject
  @Named("StartedManager")
  private GameStateManager startedManager;
  @Inject
  @Named("FinishedManager")
  private GameStateManager finishedManager;

  @Inject
  @PostConstruct
  public void init(Instance<Object> instance) {
    this.setupManager = instance.select(GameStateManager.class, new NamedAnnotation("SetupManager"))
        .get();
    this.startedManager = instance
        .select(GameStateManager.class, new NamedAnnotation("StartedManager")).get();
    this.finishedManager = instance
        .select(GameStateManager.class, new NamedAnnotation("FinishedManager")).get();
  }

  public GameStateManager getGameStateManager(@NotNull GameState gameState) {
    switch (gameState) {
      case SETUP:
        return setupManager;
      case STARTED:
        return startedManager;
      case FINISHED:
        return finishedManager;
      default:
        return null;
    }
  }

  private class NamedAnnotation extends AnnotationLiteral<Named> implements Named {

    private final String value;

    private NamedAnnotation(final String value) {
      this.value = value;
    }

    public String value() {
      return value;
    }

    @Override
    public boolean equals(Object other) {
      if (this == other) {
        return true;
      }
      if (other == null || getClass() != other.getClass()) {
        return false;
      }

      NamedAnnotation that = (NamedAnnotation) other;
      return value.equals(that.value);
    }

    @Override
    public int hashCode() {
      return value.hashCode();
    }
  }

}
