package nl.ica.asd.network.communication.upnp;


public class WeUPnPRouterKickStarterThread extends Thread {

  @Override
  public void run() {
    WeUPnPRouter.getInstance();
  }
}
