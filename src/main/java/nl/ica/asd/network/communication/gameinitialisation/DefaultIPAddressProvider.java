package nl.ica.asd.network.communication.gameinitialisation;

import nl.ica.asd.network.communication.interfaces.PublicIPAddress;
import nl.ica.asd.network.communication.upnp.WeUPnPRouter;

public class DefaultIPAddressProvider implements PublicIPAddress {
  @Override
  public String getIPAddress() {
    WeUPnPRouter weUPnPRouter = WeUPnPRouter.getInstance();
    return weUPnPRouter.getInternalIPAddress();
  }
}