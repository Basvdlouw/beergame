package nl.ica.asd.frontend.gui.agentconfiguration;

import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import nl.ica.asd.logic.domain.Business;

public class AgentConfigurationViewModel {

  private BooleanProperty errorVisibleProperty = new SimpleBooleanProperty(false);

  private BooleanProperty defaultVisibleProperty = new SimpleBooleanProperty(true);

  private ListProperty<String> businessRulesErrorProperty = new SimpleListProperty<>();

  public BooleanProperty getErrorVisibleProperty() {
    return errorVisibleProperty;
  }

  public BooleanProperty getDefaultVisibleProperty() {
    return defaultVisibleProperty;
  }

  public ListProperty<String> getBusinessRulesErrorProperty() {
    return businessRulesErrorProperty;
  }

  private Business business;

  public AgentConfigurationViewModel(Business business) {
    this.business = business;
  }

  public Business getBusiness() {
    return this.business;
  }

  public void setBusinessRulesErrorProperty(List<String> businessRulesError) {
    businessRulesErrorProperty.set(FXCollections.observableArrayList(businessRulesError));
  }

  public void doBusinessRulesErrorIndicator(boolean visible) {
    setBooleanProperty(errorVisibleProperty, visible);
    setBooleanProperty(defaultVisibleProperty, !visible);
  }

  private void setBooleanProperty(BooleanProperty booleanProperty, boolean value) {
    if (booleanProperty.get() != value) {
      booleanProperty.set(value);
    }
  }

  public void setBusiness(Business business) {
    this.business = business;
  }
}
