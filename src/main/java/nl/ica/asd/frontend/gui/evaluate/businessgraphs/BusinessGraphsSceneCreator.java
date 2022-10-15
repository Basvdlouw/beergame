package nl.ica.asd.frontend.gui.evaluate.businessgraphs;

import javafx.fxml.FXMLLoader;
import javax.inject.Named;
import nl.ica.asd.frontend.gui.scenecreator.SceneCreator;
import nl.ica.asd.frontend.gui.scenecreator.SceneLoader;

@Named("BusinessGraphsSceneCreator")
public class BusinessGraphsSceneCreator extends SceneLoader implements SceneCreator {

  private static final String FXML_FILE = "BusinessGraphs.fxml";

  public FXMLLoader createLoader() {
    return createLoader(FXML_FILE);
  }
}
