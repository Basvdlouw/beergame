package nl.ica.asd.frontend.gui.evaluate.linear;

import javafx.fxml.FXMLLoader;
import javax.inject.Named;
import nl.ica.asd.frontend.gui.scenecreator.SceneCreator;
import nl.ica.asd.frontend.gui.scenecreator.SceneLoader;

@Named("EvaluateLinearGameSceneCreator")
public class EvaluateLinearGameSceneCreator extends SceneLoader implements SceneCreator {

  private static final String FXML_FILE = "EvaluateLinearGame.fxml";

  public FXMLLoader createLoader() {
    return createLoader(FXML_FILE);
  }
}
