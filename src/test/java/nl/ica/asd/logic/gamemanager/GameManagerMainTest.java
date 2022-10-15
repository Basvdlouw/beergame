package nl.ica.asd.logic.gamemanager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import nl.ica.asd.agenthandler.AgentHandler;
import nl.ica.asd.logic.domain.Agent;
import nl.ica.asd.logic.domain.BeerGame;
import nl.ica.asd.logic.domain.Business;
import nl.ica.asd.logic.domain.BusinessType;
import nl.ica.asd.logic.domain.GameState;
import nl.ica.asd.logic.domain.GameVisibilityType;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.logic.domain.PlayerStatus;
import nl.ica.asd.logic.domain.SupplyChainType;
import nl.ica.asd.logic.domain.businesses.FactoryBusiness;
import nl.ica.asd.logic.postgameprocessor.PostGameProcessor;
import nl.ica.asd.logic.statecalculator.BusinessState;
import nl.ica.asd.logic.statecalculator.StateCalculator;
import nl.ica.asd.network.communication.interfaces.SendOrder;
import nl.ica.asd.network.playerlobby.PlayerLobby;
import nl.ica.asd.storage.domain.GameConfiguration;
import nl.ica.asd.storage.gameconfig.GameConfig;
import nl.ica.asd.storage.gamedatastorage.GameDataAccess;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class GameManagerMainTest {

  @Mock
  StateCalculator stateCalculator;
  @Mock
  GameDataAccess gameDataAccess;
  @Mock
  SendOrder sendOrder;
  @Mock
  AgentHandler agentHandler;
  @Mock
  PlayerLobby playerLobby;
  @Mock
  PostGameProcessor postGameProcessor;
  @Mock
  GameConfig gameConfig;

  @Mock
  BusinessState businessState;

  GameManagerHandler gameManagerHandler;
  Set<Player> players;
  List<Business> businesses;
  BeerGame beerGame;

  UUID businessUUID;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
    this.players = new HashSet<>();
    this.businesses = new ArrayList<>();

    this.businessUUID = UUID.randomUUID();

    Player player = new Player("FEM Student", businessUUID, BusinessType.FACTORY,
        PlayerStatus.CONNECTED_AND_ACTIVE, "1",
        1);
    this.players.add(player);
    Business factory = new FactoryBusiness(10, 10, null, new Agent("nah"), businessUUID, player, UUID.randomUUID());
    this.businesses.add(factory);

    this.gameManagerHandler = new GameManagerHandler();
    this.beerGame = new BeerGame(1, "FEM", new Date(), businesses,
        players,
        new Date(), GameState.STARTED,
        new GameConfiguration(10, 10, SupplyChainType.LINEAR, 10,
            GameVisibilityType.VISIBLE,
            null));
  }

  @AfterEach
  void tearDown() {
    Mockito
        .reset(stateCalculator, gameDataAccess, sendOrder, agentHandler, playerLobby,
            postGameProcessor,
            gameConfig);
    players.clear();
    businesses.clear();
  }
}
