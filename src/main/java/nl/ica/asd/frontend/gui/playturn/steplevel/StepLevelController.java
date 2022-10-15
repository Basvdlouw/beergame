package nl.ica.asd.frontend.gui.playturn.steplevel;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.util.converter.NumberStringConverter;
import javax.enterprise.inject.Default;
import nl.ica.asd.frontend.gui.converters.StepLevelStringConverter;
import nl.ica.asd.frontend.gui.scenecreator.SceneController;
import nl.ica.asd.frontend.gui.scenecreator.SceneLoaderInitializable;

@Default
public class StepLevelController extends SceneController<StepLevelViewModel> implements
    SceneLoaderInitializable {

  @FXML
  private Label stepLabel;

  @FXML
  private Label stepValueLabel;

  @Override
  public void sceneLoaderInitialize(URL location, ResourceBundle resources) {
    stepLabel.textProperty()
        .bindBidirectional(viewModel.getStepProperty(), new StepLevelStringConverter(resources));

    stepValueLabel.textProperty()
        .bindBidirectional(viewModel.getStepValueProperty(),
            new NumberStringConverter(resources.getLocale()));
  }
}
