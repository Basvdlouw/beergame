package nl.ica.asd.frontend.gui.evaluate.businessgraphs;

import java.util.List;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import nl.ica.asd.frontend.gui.service.dto.PlayerBusinessUUID;
import nl.ica.asd.logic.postgameprocessor.dto.PlayedGame;

public class BusinessGraphsViewModel {

  private PlayedGame playedGame;

  private ListProperty<PlayerBusinessUUID> playerListProperty = new SimpleListProperty<>();

  public PlayedGame getPlayedGame() {
    return playedGame;
  }

  public BusinessGraphsViewModel(PlayedGame playedGame) {
    this.playedGame = playedGame;
  }

  public void setPlayerListProperty(List<PlayerBusinessUUID> tableItems) {
    playerListProperty.setValue(FXCollections.observableList(tableItems));
  }

  public ListProperty<PlayerBusinessUUID> getPlayerListProperty() {
    return playerListProperty;
  }
}
