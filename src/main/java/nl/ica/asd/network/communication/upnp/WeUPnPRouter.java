package nl.ica.asd.network.communication.upnp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import nl.ica.asd.network.communication.clientserver.Server;
import org.bitlet.weupnp.GatewayDevice;
import org.bitlet.weupnp.GatewayDiscover;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class WeUPnPRouter {

  private static WeUPnPRouter weUPnPRouter;

  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private GatewayDevice device;

  public static WeUPnPRouter getInstance() {
    if (weUPnPRouter == null) {
      weUPnPRouter = new WeUPnPRouter();
    }
    return weUPnPRouter;
  }

  private WeUPnPRouter() {
    GatewayDiscover gatewayDiscover = new GatewayDiscover();
    try {
      final Map<InetAddress, GatewayDevice> devices = gatewayDiscover.discover();

      for (final GatewayDevice device : devices.values()) {
        this.device = device;
        return;
      }
    } catch (IOException | SAXException | ParserConfigurationException e) {
      logger.error("Could not get default UPnP gateway device");
    }
  }

  /*public String getExternalIPAddress() throws IOException {
    try {
      return device.getExternalIPAddress();
    } catch (final SAXException e) {
      return device.getLocalAddress().getHostAddress();
    }
  }*/

  public String getInternalIPAddress() {
    if (device != null) {
      return device.getLocalAddress().getHostAddress();
    } else {
      try {
        return InetAddress.getLocalHost().getHostAddress();
      } catch (UnknownHostException e) {
        logger.warn("Failed getting local IP, now using \"localhost\"");
        return "localhost";
      }
    }
  }

  public void addPortMapping() {
    try {
      device.addPortMapping(Server.DEFAULT_PORT, Server.DEFAULT_PORT,
          device.getLocalAddress().getHostAddress(),
          "TCP", "ASD - Beergame");
    } catch (final Exception e) {
      logger.error("Could not add port mapping");
    }
  }

  public void removePortMapping() {
    try {
      device.deletePortMapping(Server.DEFAULT_PORT, "TCP");
    } catch (final Exception e) {
      logger.error("Could not delete port mapping");
    }
  }
}