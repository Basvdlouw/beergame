package nl.ica.asd.network.communication.interfaces;

import java.io.IOException;
import java.util.Optional;

public interface PublicIPAddress {

  String getIPAddress() throws IOException;
}
