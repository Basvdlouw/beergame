package nl.ica.asd.network.playerlobby;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import nl.ica.asd.frontend.gui.service.dto.AvailableBusiness;
import nl.ica.asd.frontend.gui.service.dto.ConnectedPlayerWithBusiness;
import nl.ica.asd.storage.exception.UnknownGameException;

public interface PlayerBusiness {

  Set<UUID> getOccupiedBusinesses();

  List<ConnectedPlayerWithBusiness> getConnectedPlayersWithBusiness() throws UnknownGameException;

  List<AvailableBusiness> getAvailableBusinesses() throws UnknownGameException;
}
