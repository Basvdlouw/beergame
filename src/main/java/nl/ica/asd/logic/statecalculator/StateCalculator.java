package nl.ica.asd.logic.statecalculator;

import java.util.Map;
import java.util.UUID;
import nl.ica.asd.storage.exception.UnknownBusinessException;
import nl.ica.asd.storage.exception.UnknownGameException;

public interface StateCalculator {

  BusinessState getBusinessStateForBusiness(UUID businessUuid, int round)
      throws UnknownBusinessException, UnknownGameException;

  Map<UUID, BusinessState> getBusinessStatesForRound(int round)
      throws UnknownBusinessException, UnknownGameException;

  Map<Integer, BusinessState> getBusinessStatesForPlayer(String username)
      throws UnknownBusinessException, UnknownGameException;

  Map<Integer, Map<UUID, BusinessState>> getAllBusinessStates()
      throws UnknownBusinessException, UnknownGameException;
}
