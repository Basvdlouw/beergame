package nl.ica.asd.frontend.gui.agentconfiguration;

import javafx.fxml.FXMLLoader;
import javax.inject.Named;
import nl.ica.asd.frontend.gui.scenecreator.SceneLoader;

@Named("AgentConfigurationSceneCreator")
public class AgentConfigurationSceneCreator extends SceneLoader {

  private static final String FXML_FILE = "AgentConfiguration.fxml";

  public FXMLLoader createLoader() {
    return createLoader(FXML_FILE);
  }
}