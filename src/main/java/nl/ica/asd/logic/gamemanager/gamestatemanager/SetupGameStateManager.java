package nl.ica.asd.logic.gamemanager.gamestatemanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.inject.Named;
import nl.ica.asd.agenthandler.exceptions.AgentException;
import nl.ica.asd.logic.domain.Agent;
import nl.ica.asd.logic.domain.BeerGame;
import nl.ica.asd.logic.domain.Business;
import nl.ica.asd.logic.domain.GameState;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.logic.domain.builders.BeerGameBuilder;
import nl.ica.asd.logic.domain.builders.BusinessBuilder;
import nl.ica.asd.logic.domain.builders.PlayerBuilder;
import nl.ica.asd.logic.gamemanager.gamestatemanager.businessinitializer.BusinessInitializerContext;
import nl.ica.asd.logic.statecalculator.BusinessState;
import nl.ica.asd.logic.watchers.NextRoundStartedWatcher;
import nl.ica.asd.logic.watchers.Watcher;
import nl.ica.asd.logic.watchers.events.NextRoundStartedEvent;
import nl.ica.asd.storage.exception.UnknownGameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("SetupManager")
public class SetupGameStateManager extends DefaultGameStateManager {

  private static final Logger logger = LoggerFactory.getLogger(SetupGameStateManager.class);

  @Override
  public void nextGameState() throws UnknownGameException, AgentException {
    if (gameDataAccess.getCurrentRound() != 0) {
      return;
    }

    final BeerGame beerGame = gameDataAccess.getBeerGame();
    Set<Player> players = this.playerLobby.getAllPlayers();
    List<Business> businesses = beerGame.getBusinesses();

    // Add Player to Game and Add Business to game
    final List<Business> tmpBusinesses = new ArrayList<>();
    final Set<Player> tmpPlayers = new HashSet<>();

    players.forEach(player -> {

      try {
        final BusinessBuilder builder = player.getBusinessType().getBuilder();
        final UUID uuid = UUID.nameUUIDFromBytes(player.getUsername().getBytes());
        final BusinessState businessState = beerGame
            .getGameConfiguration()
            .getInitialBusinessStates()
            .get(builder.build().getClass());

        final Player newPlayer = PlayerBuilder.fromBasePlayer(player).withBusinessUUID(uuid)
            .build();
        tmpPlayers.add(newPlayer);

        final Business newBusiness = builder
            .withUuid(uuid)
            .withPlayer(newPlayer)
            .withActionsPerRound(new HashMap<>())
            .withBusinessState(businessState)
            .withAgent(new Agent(agentHandler.getDefaultBusinessRules()))
            .build();

        tmpBusinesses.add(newBusiness);
      } catch (AgentException e) {
        logger.error(String.format("Exception: %s", e.getMessage()));
      }
    });

    businesses.addAll(tmpBusinesses);
    tmpBusinesses.clear();
    players = new HashSet<>(tmpPlayers);
    tmpPlayers.clear();

    businesses = connectBusinessSlots(beerGame, businesses);

    final BeerGame newGame = BeerGameBuilder.fromBaseBeerGame(beerGame)
        .withBusinesses(businesses)
        .withPlayers(players)
        .withGameState(GameState.STARTED)
        .withStartTimeCurrentRound(beerGame.getStartTimeCurrentRound())
        .withCurrentRound(1).build();

    gameDataAccess.saveBeerGameSettings(newGame);
    gameDataAccess.setCurrentGame(newGame);

    playerLobby.resetPlayers(newGame.getPlayers());

    final NextRoundStartedEvent event = new NextRoundStartedEvent(businesses,
        getPlayTurnStateForCurrentRound());
    onEvent(event);
  }

  private List<Business> connectBusinessSlots(BeerGame beerGame, List<Business> businesses) {
    final BusinessInitializerContext businessInitializerCtx = new BusinessInitializerContext();
    businessInitializerCtx
        .setBusinessInitializer(beerGame.getGameConfiguration().getSupplyChainType());
    return businessInitializerCtx.initialize(beerGame, businesses);
  }

  @Override
  protected boolean isOwnWatcher(Watcher watcher) {
    return watcher instanceof NextRoundStartedWatcher;
  }
}
