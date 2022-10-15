package nl.ica.asd.frontend.gui.evaluate.linear.business;

import javafx.fxml.FXMLLoader;
import javax.inject.Named;
import nl.ica.asd.frontend.gui.scenecreator.SceneCreator;
import nl.ica.asd.frontend.gui.scenecreator.SceneLoader;

@Named("BusinessBlockSceneCreator")
public class BusinessBlockSceneCreator extends SceneLoader implements SceneCreator {

  private static final String FXML_FILE = "BusinessBlock.fxml";

  public FXMLLoader createLoader() {
    return createLoader(FXML_FILE);
  }
}