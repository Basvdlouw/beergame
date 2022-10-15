package nl.ica.asd.frontend.gui.scenecreator;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;
import javax.inject.Inject;
import nl.ica.asd.frontend.gui.service.GuiService;
import nl.ica.asd.logic.watchers.Watcher;

public abstract class SceneController<T> {

  protected T viewModel;

  @Inject
  protected GuiService guiService;

  public void setViewModel(T viewModel) {
    this.viewModel = viewModel;
  }

  protected void unload() {
    guiService.deleteObservers();

    if (this instanceof Watcher) {
      ((Watcher) this).unSubscribeAll();
    }
  }

  protected void switchScene(SceneCreator sceneCreator, ActionEvent action) {
    Node source = (Node) action.getSource();
    Stage stage = (Stage) source.getScene().getWindow();
    switchScene(sceneCreator, stage);
  }

  protected void switchScene(SceneCreator sceneCreator, Stage stage) {
    unload();
    stage.setScene(sceneCreator.create());
  }

  protected void switchScene(SceneCreator sceneCreator, ActionEvent action, Object model) {
    Node source = (Node) action.getSource();
    Stage stage = (Stage) source.getScene().getWindow();
    switchScene(sceneCreator, stage, model);
  }

  protected void switchScene(SceneCreator sceneCreator, Stage stage, Object model) {
    unload();
    stage.setScene(sceneCreator.create(model));
  }

}