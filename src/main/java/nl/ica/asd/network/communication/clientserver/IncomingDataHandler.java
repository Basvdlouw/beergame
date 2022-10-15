package nl.ica.asd.network.communication.clientserver;

public interface IncomingDataHandler {

  String handleReceivedData(String message);
}