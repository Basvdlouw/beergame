package nl.ica.asd.frontend.gui.evaluate.gameselect;

import javafx.fxml.FXMLLoader;
import javax.inject.Named;
import nl.ica.asd.frontend.gui.scenecreator.SceneCreator;
import nl.ica.asd.frontend.gui.scenecreator.SceneLoader;

@Named("EvaluateGameSelectSceneCreator")
public class EvaluateGameSelectSceneCreator extends SceneLoader implements SceneCreator {

  private static final String FXML_FILE = "EvaluateGameSelect.fxml";

  public FXMLLoader createLoader() {
    return createLoader(FXML_FILE);
  }
}