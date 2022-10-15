package nl.ica.asd.frontend.gui.evaluate.graphs.linear;

import javafx.fxml.FXMLLoader;
import javax.inject.Named;
import nl.ica.asd.frontend.gui.scenecreator.SceneLoader;

@Named("GraphLinearGameSceneCreator")
public class GraphLinearGameSceneCreator extends SceneLoader {

  private static final String FXML_FILE = "GraphLinearGame.fxml";

  public FXMLLoader createLoader() {
    return createLoader(FXML_FILE);
  }
}
