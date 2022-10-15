package nl.ica.asd.frontend.gui.evaluate.businessgraphs.charts;

import javafx.fxml.FXMLLoader;
import javax.inject.Named;
import nl.ica.asd.frontend.gui.scenecreator.SceneCreator;
import nl.ica.asd.frontend.gui.scenecreator.SceneLoader;

@Named("BusinessChartsSceneCreator")
public class BusinessChartsSceneCreator extends SceneLoader implements SceneCreator {

  private static final String FXML_FILE = "BusinessCharts.fxml";

  public FXMLLoader createLoader() {
    return createLoader(FXML_FILE);
  }
}
