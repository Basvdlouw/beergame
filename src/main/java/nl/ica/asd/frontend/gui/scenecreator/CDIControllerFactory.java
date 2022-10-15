package nl.ica.asd.frontend.gui.scenecreator;

import javafx.util.Callback;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

@ApplicationScoped
public class CDIControllerFactory implements Callback<Class<?>, Object> {

  @Inject
  private Instance<Object> instance;

  @Override
  public Object call(Class<?> type) {
    return instance.select(type).get();
  }
}