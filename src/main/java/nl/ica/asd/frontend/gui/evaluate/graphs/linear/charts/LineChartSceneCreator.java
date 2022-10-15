package nl.ica.asd.frontend.gui.evaluate.graphs.linear.charts;

import javafx.fxml.FXMLLoader;
import javax.inject.Named;
import nl.ica.asd.frontend.gui.scenecreator.SceneLoader;

@Named("LineChartSceneCreator")
public class LineChartSceneCreator extends SceneLoader {

  private static final String FXML_FILE = "LineChart.fxml";

  public FXMLLoader createLoader() {
    return createLoader(FXML_FILE);
  }
}
