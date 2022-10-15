package nl.ica.asd.network.communication.twophasecommit;

import java.io.IOException;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;
import nl.ica.asd.agenthandler.AgentHandler;
import nl.ica.asd.logic.domain.BusinessRules;
import nl.ica.asd.logic.domain.BusinessType;
import nl.ica.asd.logic.domain.GameState;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.logic.domain.PlayerStatus;
import nl.ica.asd.logic.domain.actions.OrderAction;
import nl.ica.asd.logic.domain.builders.PlayerBuilder;
import nl.ica.asd.network.communication.clientserver.Server;
import nl.ica.asd.network.communication.datatransferobjects.DataTransferObject;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.AppendEntry;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.AppendEntryResult;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.InitialConnectionRequest;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.InitialConnectionResponse;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.SerializationExceptionResponse;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.Vote;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.VoteRequest;
import nl.ica.asd.network.communication.gameinitialisation.InitializeGame;
import nl.ica.asd.network.communication.interfaces.GameConnection;
import nl.ica.asd.network.communication.interfaces.PublicIPAddress;
import nl.ica.asd.network.communication.interfaces.SendBusinessRules;
import nl.ica.asd.network.communication.interfaces.SendGameStateUpdate;
import nl.ica.asd.network.communication.interfaces.SendHeartbeat;
import nl.ica.asd.network.communication.interfaces.SendOrder;
import nl.ica.asd.network.communication.interfaces.SendPlayerListUpdate;
import nl.ica.asd.network.communication.interfaces.SendVoteRequests;
import nl.ica.asd.network.failurehandler.LeaderElection;
import nl.ica.asd.network.failurehandler.exceptions.NoLeaderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("Messenger")
public class Messenger implements SendOrder, SendBusinessRules, SendPlayerListUpdate,
    SendHeartbeat, SendVoteRequests, SendGameStateUpdate, GameConnection {

  private static final Logger logger = LoggerFactory.getLogger(Messenger.class);

  @Named("OutgoingDataController")
  private final OutgoingDataController outgoingDataController;
  private PublicIPAddress publicIPAddress;

  @Inject
  private InitializeGame initializeGame;

  @Inject
  public Messenger(OutgoingDataController outgoingDataController, PublicIPAddress publicIPAddress) {
    this.outgoingDataController = outgoingDataController;
    this.publicIPAddress = publicIPAddress;
  }

  @Override
  public void sendBusinessRules(BusinessRules businessRules) {
    outgoingDataController.sendBusinessRules(businessRules);
  }

  @Override
  public void sendHeartbeat(Set<Player> targetsForHeartbeat, LeaderElection leaderElection) {
    try {
      outgoingDataController
          .sendHeartbeat(targetsForHeartbeat, new CallBackHandler(this, leaderElection));
    } catch (NoLeaderException | IOException e) {
      logger.error(e.getMessage());
    }
  }

  @Override
  public void sendOrder(OrderAction order) {
    outgoingDataController.sendOrder(order);
  }

  @Override
  public void sendPlayerListUpdate(Set<Player> players) {
    players.forEach(outgoingDataController::sendPlayerListUpdate);
  }

  @Override
  public void sendVoteRequests(Set<Player> targetsForVoteRequest, LeaderElection leaderElection,
      Player proposedLeader) {
    try {
      outgoingDataController.sendVoteRequest(
          targetsForVoteRequest,
          new CallBackHandler(this, leaderElection),
          proposedLeader
      );
    } catch (IOException e) {
      logger.error(e.getMessage());
    }
  }

  @Override
  public void sendGameStateUpdate(GameState gameState) {
    outgoingDataController.sendGameStateUpdate(gameState);
  }

  public void receiveResponse(DataTransferObject originalRequest, DataTransferObject response,
      Player responder, CallBackHandler callBackHandler) {
    if (response.getClass() == Vote.class && originalRequest.getClass() == VoteRequest.class) {
      outgoingDataController
          .receiveVote((Vote) response, responder,
              callBackHandler.getLeaderElection());
    } else if (response.getClass() == AppendEntryResult.class
        && originalRequest.getClass() == AppendEntry.class) {
      outgoingDataController
          .receiveAppendEntryResult((AppendEntry) originalRequest, (AppendEntryResult) response,
              responder, callBackHandler.getLeaderElection());
    } else if (response.getClass() == InitialConnectionResponse.class
        && originalRequest.getClass() == InitialConnectionRequest.class) {
      outgoingDataController
          .receiveConnectionResponse((InitialConnectionRequest) originalRequest,
              (InitialConnectionResponse) response);
    } else if (response.getClass() == SerializationExceptionResponse.class) {
      logger.error("The message couldn't be deserialized, no way to handle this.");
    }
  }

  public void receiveFailedRequest(DataTransferObject originalRequest) {
    if (originalRequest instanceof InitialConnectionRequest) {
      outgoingDataController.receiveConnectionFailed();
    }
  }

  @Override
  public void connectToGame(String ipAddress, String userName) throws IOException {
    // WARNING: In this case the player is expected to be uninitialized, since we know nothing of the players in the game yet
    outgoingDataController.connectToGame(
        new Player("", null, BusinessType.NOT_SET, PlayerStatus.CONNECTED_AND_ACTIVE, ipAddress,
            Server.DEFAULT_PORT),
        new Player(userName, null, BusinessType.NOT_SET, PlayerStatus.CONNECTED_AND_ACTIVE,
            publicIPAddress.getIPAddress(), Server.DEFAULT_PORT),
        new CallBackHandler(this, null));
  }

  @Override
  public void leaveGame(Player playerSelf) {
    outgoingDataController
        .sendPlayerListUpdate(PlayerBuilder.fromBasePlayer(playerSelf)
            .withPlayerStatus(PlayerStatus.LEFT_ON_PURPOSE).build());
    initializeGame.stopServer();
    initializeGame.reset();
  }

  public class CallBackHandler implements ResponseCallback {

    private final Messenger messenger;
    private final LeaderElection leaderElection;

    CallBackHandler(
        Messenger messenger,
        LeaderElection leaderElection) {
      this.messenger = messenger;
      this.leaderElection = leaderElection;
    }

    public LeaderElection getLeaderElection() {
      return leaderElection;
    }

    @Override
    public void receiveResponse(DataTransferObject originalMessage,
        DataTransferObject responseMessage, Player sender) {
      messenger.receiveResponse(originalMessage, responseMessage, sender, this);
    }

    @Override
    public void receiveFailedRequest(DataTransferObject originalMessage) {
      messenger.receiveFailedRequest(originalMessage);
    }
  }
}
