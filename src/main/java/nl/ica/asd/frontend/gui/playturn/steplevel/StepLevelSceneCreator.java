package nl.ica.asd.frontend.gui.playturn.steplevel;

import javafx.fxml.FXMLLoader;
import javax.inject.Named;
import nl.ica.asd.frontend.gui.scenecreator.SceneCreator;
import nl.ica.asd.frontend.gui.scenecreator.SceneLoader;

@Named("StepLevelSceneCreator")
public class StepLevelSceneCreator extends SceneLoader implements SceneCreator {

  private static final String FXML_FILE = "StepLevel.fxml";

  @Override
  public FXMLLoader createLoader() {
    return createLoader(FXML_FILE);
  }
}
