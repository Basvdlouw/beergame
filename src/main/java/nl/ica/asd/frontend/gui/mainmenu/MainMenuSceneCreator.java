package nl.ica.asd.frontend.gui.mainmenu;

import javafx.fxml.FXMLLoader;
import javax.inject.Named;
import nl.ica.asd.frontend.gui.scenecreator.SceneCreator;
import nl.ica.asd.frontend.gui.scenecreator.SceneLoader;

@Named("MainMenuSceneCreator")
public class MainMenuSceneCreator extends SceneLoader implements SceneCreator {

  private static final String FXML_FILE = "MainMenu.fxml";

  public FXMLLoader createLoader() {
    return createLoader(FXML_FILE);
  }
}