package nl.ica.asd.frontend.gui.evaluate.businessgraphs.charts;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import nl.ica.asd.frontend.gui.scenecreator.SceneController;

public class BusinessChartsController extends SceneController implements Initializable {

  @FXML
  LineChart chart;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    chart.setOnMouseClicked(event -> this.openPopUp());
  }

  private void openPopUp() {
    Stage popup = new Stage();
    popup.setFullScreen(true);
    AnchorPane pane = new AnchorPane();
    LineChart<Number, Number> clonedChart = createClonedChart();
    pane.getChildren().add(clonedChart);

    AnchorPane.setBottomAnchor(clonedChart, 0.0);
    AnchorPane.setTopAnchor(clonedChart, 0.0);
    AnchorPane.setLeftAnchor(clonedChart, 0.0);
    AnchorPane.setRightAnchor(clonedChart, 0.0);

    Scene scene = new Scene(pane);

    scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
      if (event.getCode().equals(KeyCode.ESCAPE)) {
        Stage s = (Stage) scene.getWindow();
        s.close();
      }
    });

    popup.initModality(Modality.APPLICATION_MODAL);
    popup.setScene(scene);
    popup.show();
  }

  public LineChart<Number, Number> createClonedChart() {
    NumberAxis xAxis = new NumberAxis();
    NumberAxis yAxis = new NumberAxis();

    LineChart<Number, Number> clonedChart = new LineChart<>(xAxis, yAxis);

    ObservableList seriesList = chart.getData();
    Optional<XYChart.Series> serieOptional = seriesList.stream().findAny();

    if (serieOptional.isPresent()) {

      seriesList.stream().forEach(s -> {
        XYChart.Series serie = (XYChart.Series) s;
        XYChart.Series clonedSerie = new XYChart.Series();
        clonedChart.getData().add(clonedSerie);
        clonedChart.setLegendVisible(false);

        // You need to manually clone the data points. If you bind the dataproperty weird this happen. Same thing happens if you would do:
        // clonedChart.getData.addAll(chart.getData())

        serie.getData().forEach(data -> {
          XYChart.Data dataCasted = (XYChart.Data) data;
          clonedSerie.getData()
                  .add(new XYChart.Data<>(dataCasted.getXValue(), dataCasted.getYValue()));
        });
      });
    }

    ((NumberAxis)clonedChart.getXAxis()).forceZeroInRangeProperty().setValue(false);
    ((NumberAxis)clonedChart.getXAxis()).setLowerBound(1);
    ((NumberAxis)clonedChart.getXAxis()).setUpperBound(((NumberAxis) chart.getXAxis()).getUpperBound());
    ((NumberAxis)clonedChart.getXAxis()).setTickUnit(1);
    clonedChart.getXAxis().setAutoRanging(false);

    return clonedChart;
  }
}
