package nl.ica.asd.frontend.gui.gamefinder;


import javafx.fxml.FXMLLoader;
import javax.inject.Named;
import nl.ica.asd.frontend.gui.scenecreator.SceneCreator;
import nl.ica.asd.frontend.gui.scenecreator.SceneLoader;

@Named("GameFinderSceneCreator")
public class GameFinderSceneCreator extends SceneLoader implements SceneCreator {

  private static final String FXML_FILE = "GameFinder.fxml";

  public FXMLLoader createLoader() {
    return createLoader(FXML_FILE);
  }
}
