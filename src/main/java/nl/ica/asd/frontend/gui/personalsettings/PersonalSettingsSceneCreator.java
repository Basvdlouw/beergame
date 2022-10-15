package nl.ica.asd.frontend.gui.personalsettings;

import javafx.fxml.FXMLLoader;
import javax.inject.Named;
import nl.ica.asd.frontend.gui.scenecreator.SceneCreator;
import nl.ica.asd.frontend.gui.scenecreator.SceneLoader;

@Named("PersonalSettingsSceneCreator")
public class PersonalSettingsSceneCreator extends SceneLoader implements SceneCreator {

  private static final String FXML_FILE = "PersonalSettings.fxml";

  @Override
  public FXMLLoader createLoader() {
    return createLoader(FXML_FILE);
  }
}