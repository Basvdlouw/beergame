package nl.ica.asd.frontend.gui.scenecreator;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

public interface SceneCreator {

  /**
   * Returns the loader of what to display
   *
   * @return FXMLLoader
   */
  FXMLLoader createLoader();

  /**
   * Returns a scene to display
   *
   * @return Scene
   */
  Scene create();

  /**
   * Returns a scene based on the fxmlFile and sets the viewModel
   *
   * @param model Any object you want to use in the new scene
   * @return Scene
   */
  Scene create(Object model);
}
