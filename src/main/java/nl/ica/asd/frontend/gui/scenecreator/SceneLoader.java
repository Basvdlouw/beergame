package nl.ica.asd.frontend.gui.scenecreator;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javax.inject.Inject;
import nl.ica.asd.frontend.languageservice.LanguageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SceneLoader implements SceneCreator {

  @Inject
  private CDIControllerFactory controllerFactory;

  @Inject
  private LanguageService languageService;

  private static final Logger logger = LoggerFactory.getLogger(SceneLoader.class);

  public FXMLLoader createLoader(String fxmlFile) {
    FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
    loader.setControllerFactory(controllerFactory);
    loader.setResources(languageService.getLanguageBundle());

    return loader;
  }

  public Scene create() {
    Parent root = null;

    try {
      root = createLoader().load();
    } catch (IOException e) {
      logger.error("Failed to load scene.", e);
    }

    return new Scene(Objects.requireNonNull(root));
  }

  public Scene create(Object viewModel) {
    Parent root = null;

    try {
      FXMLLoader loader = createLoader();
      root = loader.load();

      SceneController sceneController = loader.getController();
      sceneController.setViewModel(viewModel);

      if (sceneController instanceof SceneLoaderInitializable) {
        ResourceBundle resources = loader.getResources();
        URL url = loader.getLocation();

        ((SceneLoaderInitializable) sceneController).sceneLoaderInitialize(url, resources);
      }
    } catch (IOException e) {
      logger.error("Failed to load scene.", e);
    }

    return new Scene(Objects.requireNonNull(root));
  }
}
