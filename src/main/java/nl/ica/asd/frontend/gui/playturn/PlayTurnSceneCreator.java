package nl.ica.asd.frontend.gui.playturn;

import javafx.fxml.FXMLLoader;
import javax.inject.Named;
import nl.ica.asd.frontend.gui.scenecreator.SceneCreator;
import nl.ica.asd.frontend.gui.scenecreator.SceneLoader;

@Named("PlayTurnSceneCreator")
public class PlayTurnSceneCreator extends SceneLoader implements SceneCreator {

  private static final String FXML_FILE = "PlayTurn.fxml";

  @Override
  public FXMLLoader createLoader() {
    return createLoader(FXML_FILE);
  }
}