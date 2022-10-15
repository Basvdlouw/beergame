package nl.ica.asd;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import nl.ica.asd.frontend.gui.mainmenu.MainMenuSceneCreator;
import nl.ica.asd.frontend.gui.personalsettings.PersonalSettingsSceneCreator;
import nl.ica.asd.frontend.gui.scenecreator.SceneCreator;
import nl.ica.asd.network.communication.upnp.WeUPnPRouterKickStarterThread;
import nl.ica.asd.storage.applicationdatastorage.ApplicationDataAccess;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

public class Main extends Application {

  private static final WeldContainer weldContainer = new Weld().initialize();

  public static void main(String[] args) {
    launch(args);
  }

  public static WeldContainer getContainer() {
    return Main.weldContainer;
  }

  @Override
  public void start(Stage primaryStage) {
    new WeUPnPRouterKickStarterThread().start();

    ApplicationDataAccess applicationDataAccess = weldContainer.instance().select(
        ApplicationDataAccess.class)
        .get();
    SceneCreator scene;

    if (applicationDataAccess.getUsername().isEmpty()) {
      scene = weldContainer.instance().select(
          PersonalSettingsSceneCreator.class)
          .get();
    } else {
      scene = weldContainer.instance().select(
          MainMenuSceneCreator.class)
          .get();
    }

    primaryStage.setScene(scene.create());
    primaryStage.show();
    primaryStage.setTitle("Beer Distribution Game - ASD Groep 4");
    primaryStage.getIcons().add(new Image("icon.png"));

    primaryStage.setOnCloseRequest((WindowEvent windowEvent) -> {
      Platform.exit();
      System.exit(0);
    });
  }

  @Override
  public void stop() {
    weldContainer.shutdown();
  }
}