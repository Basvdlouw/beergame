package nl.ica.asd.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import nl.ica.asd.logic.domain.Agent;
import nl.ica.asd.logic.domain.Business;
import nl.ica.asd.logic.domain.BusinessRules;
import nl.ica.asd.logic.domain.BusinessType;
import nl.ica.asd.logic.domain.GameState;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.logic.domain.PlayerStatus;
import nl.ica.asd.logic.domain.actions.DeliveryAction;
import nl.ica.asd.logic.domain.actions.OrderAction;
import nl.ica.asd.logic.domain.businesses.FactoryBusiness;
import nl.ica.asd.logic.domain.businesses.RegionalWarehouseBusiness;
import nl.ica.asd.logic.domain.businesses.RetailBusiness;
import nl.ica.asd.logic.domain.businesses.WholesaleBusiness;

public class DomainObjectGenerator {

  public static Business getBusiness(Integer seed) {
    switch (Math.abs(seed % 4)) {
      case 0:
        return getFactoryBusiness(seed);
      case 1:
        return getRegionalWarehouseBusiness(seed);
      case 2:
        return getWholesaleBusiness(seed);
      case 3:
        return getRetailBusiness(seed);
    }
    throw new IllegalStateException();
  }

  public static FactoryBusiness getFactoryBusiness(Integer seed) {
    return new FactoryBusiness(seed, seed, new HashMap<>(), getAgent(seed),
        UUID.nameUUIDFromBytes(seed.toString().getBytes()), getPlayer(seed),
        UUID.randomUUID());
  }

  public static RegionalWarehouseBusiness getRegionalWarehouseBusiness(Integer seed) {
    List<FactoryBusiness> factoryBusinesses = new ArrayList<>();
    factoryBusinesses.add((FactoryBusiness) getBusiness(0));
    return new RegionalWarehouseBusiness(seed, seed, new HashMap<>(), getAgent(seed),
        UUID.nameUUIDFromBytes(seed.toString().getBytes()), getPlayer(seed),
        factoryBusinesses);
  }

  public static WholesaleBusiness getWholesaleBusiness(Integer seed) {
    List<RegionalWarehouseBusiness> regionalWarehouseBusinesses = new ArrayList<>();
    regionalWarehouseBusinesses.add((RegionalWarehouseBusiness) getBusiness(1));
    return new WholesaleBusiness(seed, seed, new HashMap<>(), getAgent(seed),
        UUID.nameUUIDFromBytes(seed.toString().getBytes()), getPlayer(seed),
        regionalWarehouseBusinesses);
  }

  public static RetailBusiness getRetailBusiness(Integer seed) {
    List<WholesaleBusiness> wholesaleBusinesses = new ArrayList<>();
    wholesaleBusinesses.add((WholesaleBusiness) getBusiness(2));
    return new RetailBusiness(seed, seed, new HashMap<>(), getAgent(seed),
        UUID.nameUUIDFromBytes(seed.toString().getBytes()), getPlayer(seed),
        wholesaleBusinesses, UUID.randomUUID());
  }

  public static Agent getAgent(Integer seed) {
    return new Agent(seed.toString());
  }

  public static BusinessRules getBusinessRules(Integer seed) {
    return new BusinessRules(getBusiness(seed), seed.toString());
  }

  public static Player getPlayer(Integer seed) {
    // TODO check businessType
    return new Player("user" + seed, UUID.nameUUIDFromBytes(seed.toString().getBytes()),
        BusinessType.FACTORY, PlayerStatus.CONNECTED_AND_ACTIVE, "123.123.123." + seed, seed * 111);
  }

  public static OrderAction getOderAction(Integer seed) {
    return new OrderAction(seed, UUID.nameUUIDFromBytes(seed.toString().getBytes()),
        UUID.nameUUIDFromBytes((seed++).toString().getBytes()));
  }

  public static DeliveryAction getDeliveryAction(Integer seed) {
    return new DeliveryAction(seed, UUID.nameUUIDFromBytes(seed.toString().getBytes()),
        UUID.nameUUIDFromBytes((seed++).toString().getBytes()));
  }

  public static GameState getGameState(Integer seed) {
    switch (Math.abs(seed % 3)) {
      case 0:
        return GameState.SETUP;
      case 1:
        return GameState.STARTED;
      case 2:
        return GameState.FINISHED;
    }
    throw new IllegalStateException();
  }
}