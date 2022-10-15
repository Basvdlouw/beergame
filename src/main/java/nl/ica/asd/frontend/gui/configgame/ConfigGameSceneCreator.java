package nl.ica.asd.frontend.gui.configgame;

import javafx.fxml.FXMLLoader;
import javax.inject.Named;
import nl.ica.asd.frontend.gui.scenecreator.SceneCreator;
import nl.ica.asd.frontend.gui.scenecreator.SceneLoader;

@Named("ConfigGameSceneCreator")
public class ConfigGameSceneCreator extends SceneLoader implements SceneCreator {

  private static final String FXML_FILE = "ConfigGame.fxml";

  public FXMLLoader createLoader() {
    return createLoader(FXML_FILE);
  }

}
