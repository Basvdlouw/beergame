package nl.ica.asd.frontend.gui.playerlobby;

import javafx.fxml.FXMLLoader;
import javax.inject.Named;
import nl.ica.asd.frontend.gui.scenecreator.SceneCreator;
import nl.ica.asd.frontend.gui.scenecreator.SceneLoader;


@Named("PlayerLobbySceneCreator")
public class PlayerLobbySceneCreator extends SceneLoader implements SceneCreator {

  private static final String FXML_FILE = "PlayerLobby.fxml";

  public FXMLLoader createLoader() {
    return createLoader(FXML_FILE);
  }

}
