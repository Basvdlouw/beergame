package nl.ica.asd.frontend.gui.evaluate.linear.business;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.converter.NumberStringConverter;
import nl.ica.asd.frontend.gui.scenecreator.SceneController;
import nl.ica.asd.frontend.gui.scenecreator.SceneLoaderInitializable;
import nl.ica.asd.logic.domain.businesses.FactoryBusiness;
import nl.ica.asd.logic.domain.businesses.RegionalWarehouseBusiness;
import nl.ica.asd.logic.domain.businesses.RetailBusiness;
import nl.ica.asd.logic.domain.businesses.WholesaleBusiness;

public class BusinessBlockController extends SceneController<BusinessBlockViewModel> implements
    SceneLoaderInitializable {

  @FXML
  private Label level;

  @FXML
  private Label outgoingGoods;

  @FXML
  private Label incomingGoods;

  @FXML
  private Label incomingOrders;

  @FXML
  private Label placedOrders;

  @FXML
  private Label budgetValue;

  @FXML
  private Label stockValue;

  @FXML
  private Label incordersValue;

  @FXML
  private AnchorPane outgoingArrowsPane;


  @Override
  public void sceneLoaderInitialize(URL location, ResourceBundle resources) {
    if (viewModel.getBusiness() instanceof FactoryBusiness) {
      level.setText(resources.getString("businessblock.factory"));
      outgoingArrowsPane.setVisible(false);
    } else if (viewModel.getBusiness() instanceof RegionalWarehouseBusiness) {
      level.setText(resources.getString("businessblock.regionalwarehouse"));
    } else if (viewModel.getBusiness() instanceof RetailBusiness) {
      level.setText(resources.getString("businessblock.retailer"));
    } else if (viewModel.getBusiness() instanceof WholesaleBusiness) {
      level.setText(resources.getString("businessblock.wholesale"));
    }

    outgoingGoods.textProperty()
        .bindBidirectional(viewModel.getOutgoingGoodsProperty(), new NumberStringConverter());
    incomingGoods.textProperty()
        .bindBidirectional(viewModel.getIncomingGoodsProperty(), new NumberStringConverter());
    incomingOrders.textProperty()
        .bindBidirectional(viewModel.getIncomingOrdersProperty(), new NumberStringConverter());
    placedOrders.textProperty()
        .bindBidirectional(viewModel.getPlacedOrdersProperty(), new NumberStringConverter());
    budgetValue.textProperty()
        .bindBidirectional(viewModel.getBudgetProperty(), new NumberStringConverter());
    stockValue.textProperty()
        .bindBidirectional(viewModel.getStockProperty(), new NumberStringConverter());
    incordersValue.textProperty()
        .bindBidirectional(viewModel.getOpenOrdersroperty(), new NumberStringConverter());


  }
}