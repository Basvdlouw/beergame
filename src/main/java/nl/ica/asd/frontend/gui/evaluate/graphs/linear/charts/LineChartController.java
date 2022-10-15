package nl.ica.asd.frontend.gui.evaluate.graphs.linear.charts;

import java.net.URL;
import java.util.ResourceBundle;
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

public class LineChartController extends SceneController<LineChartViewModel> implements
    Initializable {

  @FXML
  LineChart chart;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    chart.setOnMouseClicked(event -> openPopUp(resources));
  }

  private void openPopUp(ResourceBundle resources) {
    Stage popup = new Stage();
    popup.setFullScreen(true);
    AnchorPane pane = new AnchorPane();

    NumberAxis xAxis = new NumberAxis();
    NumberAxis yAxis = new NumberAxis();

    LineChart<Number, Number> clonedChart = new LineChart<>(xAxis, yAxis);
    clonedChart.getXAxis().setLabel(resources.getString("graphlineargame.weeks"));
    clonedChart.getYAxis().setLabel(resources.getString("graphlineargame.inventory"));
    pane.getChildren().add(clonedChart);

    for (int i = 0; i < 4; i++) {
      XYChart.Series series = (XYChart.Series) chart.getData().get(i);
      XYChart.Series clonedSeries = new XYChart.Series();
      clonedSeries.setName(series.getName());
      clonedChart.getData().add(clonedSeries);

      // You need to manually clone the data points. If you bind the dataproperty weird this happen. Same thing happens if you would do:
      // clonedChart.getData.addAll(chart.getData())

      series.getData().forEach(data -> {
        XYChart.Data dataCasted = (XYChart.Data) data;
        clonedSeries.getData()
            .add(new XYChart.Data<>(dataCasted.getXValue(), dataCasted.getYValue()));
      });
    }

    ((NumberAxis)clonedChart.getXAxis()).forceZeroInRangeProperty().setValue(false);
    ((NumberAxis)clonedChart.getXAxis()).setLowerBound(1);
    ((NumberAxis)clonedChart.getXAxis()).setUpperBound(((NumberAxis) chart.getXAxis()).getUpperBound());
    ((NumberAxis)clonedChart.getXAxis()).setTickUnit(1);
    clonedChart.getXAxis().setAutoRanging(false);

    AnchorPane.setBottomAnchor(clonedChart, 0.0);
    AnchorPane.setTopAnchor(clonedChart, 0.0);
    AnchorPane.setLeftAnchor(clonedChart, 0.0);
    AnchorPane.setRightAnchor(clonedChart, 0.0);

    Scene s = new Scene(pane);

    s.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
      if (e.getCode().equals(KeyCode.ESCAPE)) {
        ((Stage) s.getWindow()).close();
      }
    });

    popup.initModality(Modality.APPLICATION_MODAL);
    popup.setScene(s);
    popup.show();
  }
}
