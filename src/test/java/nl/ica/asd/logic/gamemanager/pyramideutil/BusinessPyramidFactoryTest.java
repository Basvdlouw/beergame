package nl.ica.asd.logic.gamemanager.pyramideutil;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import nl.ica.asd.logic.domain.BeerGame;
import nl.ica.asd.logic.domain.Business;
import nl.ica.asd.logic.domain.BusinessType;
import nl.ica.asd.logic.domain.Player;
import nl.ica.asd.logic.domain.PlayerStatus;
import nl.ica.asd.logic.domain.businesses.FactoryBusiness;
import nl.ica.asd.logic.domain.businesses.RegionalWarehouseBusiness;
import nl.ica.asd.logic.domain.businesses.RetailBusiness;
import nl.ica.asd.logic.domain.businesses.WholesaleBusiness;
import nl.ica.asd.logic.statecalculator.BusinessState;
import nl.ica.asd.util.TestHelpers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class BusinessPyramidFactoryTest {

  private BeerGame beerGame;
  private List<Business> list;
  private Map<Class<? extends Business>, BusinessState> initialBusinessStates;
  private final BusinessesPyramidFactory businessesPyramidFactory = new BusinessesPyramidFactory();

  @Mock
  private FactoryBusiness factoryBusiness;
  @Mock
  private RegionalWarehouseBusiness regionalWarehouseBusiness;
  @Mock
  private WholesaleBusiness wholesaleBusiness;
  @Mock
  private RetailBusiness retailBusiness;

  @BeforeEach
  public void setup() {
    beerGame = TestHelpers.createTestingBeerGame();
    list = beerGame.getBusinesses();
    initialBusinessStates = beerGame.getGameConfiguration().getInitialBusinessStates();
  }


  @Test
  void businessesPyramidFactory_should_return_instanceOfFactory_when_getBusiness() {
    assertThat(businessesPyramidFactory
            .getBusiness(BusinessType.FACTORY, list, initialBusinessStates, null),
        instanceOf(FactoryBusiness.class));
  }

  @Test
  void businessesPyramidFactory_should_return_instanceOfRegionalWarehouse_when_getBusiness() {
    final BinaryTreeNode root = new BinaryTreeNode(businessesPyramidFactory
        .getBusiness(BusinessType.FACTORY, list, initialBusinessStates, null));

    assertThat(businessesPyramidFactory
        .getBusiness(BusinessType.REGIONAL_WAREHOUSE, list, initialBusinessStates,
            root), instanceOf(RegionalWarehouseBusiness.class));
  }

  @Test
  void businessesPyramidFactory_should_return_instanceOfWholeSale_when_getBusiness() {
    final BinaryTreeNode root = new BinaryTreeNode(businessesPyramidFactory
        .getBusiness(BusinessType.REGIONAL_WAREHOUSE, list, initialBusinessStates,
            new BinaryTreeNode(factoryBusiness)));

    assertThat(businessesPyramidFactory
            .getBusiness(BusinessType.WHOLESALE, list, initialBusinessStates, root),
        instanceOf(WholesaleBusiness.class));
  }

  @Test
  void businessesPyramidFactory_should_return_instanceOfRetail_when_getBusiness() {
    final BinaryTreeNode root = new BinaryTreeNode(businessesPyramidFactory
        .getBusiness(BusinessType.WHOLESALE, list, initialBusinessStates,
            new BinaryTreeNode(regionalWarehouseBusiness)));

    assertThat(businessesPyramidFactory
            .getBusiness(BusinessType.RETAIL, list, initialBusinessStates, root),
        instanceOf(RetailBusiness.class));
  }

  @Test
  void businessPyramidFactory_should_set_agent_when_getBusiness_for_factory() {
    assertNotNull(businessesPyramidFactory
        .getBusiness(BusinessType.FACTORY, new ArrayList<>(), initialBusinessStates, null)
        .getAgent());
  }

  @Test
  void businessPyramidFactory_shouldNot_set_agent_when_getBusiness_for_factory() {
    list = new ArrayList<>();
    final UUID uuid = UUID.randomUUID();
    list.add(new FactoryBusiness(20, 30, Collections.emptyMap(), null, uuid,
        new Player("player", uuid, BusinessType.FACTORY, PlayerStatus.CONNECTED_AND_ACTIVE,
            "1.1.1.1", 8208), UUID.randomUUID()));
    final Map<Class<? extends Business>, BusinessState> initialBusinessStates = beerGame
        .getGameConfiguration().getInitialBusinessStates();

    final BinaryTreeNode root = null;
    assertNull(businessesPyramidFactory
        .getBusiness(BusinessType.FACTORY, list, initialBusinessStates, root)
        .getAgent());
  }

  @Test
  void businessPyramidFactory_should_set_agent_when_getBusiness_for_regionalWarehouse() {
    list = new ArrayList<>();

    final BinaryTreeNode root = new BinaryTreeNode(businessesPyramidFactory
        .getBusiness(BusinessType.FACTORY, list, initialBusinessStates, null));

    assertNotNull(businessesPyramidFactory
        .getBusiness(BusinessType.REGIONAL_WAREHOUSE, list, initialBusinessStates,
            root)
        .getAgent());
  }

  @Test
  void businessPyramidFactory_shouldNot_set_agent_when_getBusiness_for_regionalWarehouse() {
    list = new ArrayList<>();
    final List<FactoryBusiness> listupperBusiness = new ArrayList<>();
    final UUID uuid = UUID.randomUUID();

    final BinaryTreeNode root = new BinaryTreeNode(businessesPyramidFactory
        .getBusiness(BusinessType.FACTORY, list, initialBusinessStates, null));

    listupperBusiness.add((FactoryBusiness) root.getValue());

    list.add(new RegionalWarehouseBusiness(20, 30, Collections.emptyMap(), null, uuid,
        new Player("player", uuid, BusinessType.REGIONAL_WAREHOUSE,
            PlayerStatus.CONNECTED_AND_ACTIVE,
            "1.1.1.1", 8208), listupperBusiness));

    assertNull(businessesPyramidFactory
        .getBusiness(BusinessType.REGIONAL_WAREHOUSE, list, initialBusinessStates,
            root)
        .getAgent());
  }

  @Test
  void businessPyramidFactory_should_set_agent_when_getBusiness_for_wholesale() {
    list = new ArrayList<>();

    final BinaryTreeNode root = new BinaryTreeNode(businessesPyramidFactory
        .getBusiness(BusinessType.REGIONAL_WAREHOUSE, list, initialBusinessStates,
            new BinaryTreeNode(factoryBusiness)));

    assertNotNull(businessesPyramidFactory
        .getBusiness(BusinessType.WHOLESALE, list, initialBusinessStates, root)
        .getAgent());
  }

  @Test
  void businessPyramidFactory_shouldNot_set_agent_when_getBusiness_for_wholesale() {
    list = new ArrayList<>();
    final List<RegionalWarehouseBusiness> listupperBusiness = new ArrayList<>();
    final UUID uuid = UUID.randomUUID();

    final BinaryTreeNode root = new BinaryTreeNode(businessesPyramidFactory
        .getBusiness(BusinessType.REGIONAL_WAREHOUSE, list, initialBusinessStates,
            new BinaryTreeNode(factoryBusiness)));

    listupperBusiness.add((RegionalWarehouseBusiness) root.getValue());
    list.add(new WholesaleBusiness(20, 30, Collections.emptyMap(), null, uuid,
        new Player("player", uuid, BusinessType.REGIONAL_WAREHOUSE,
            PlayerStatus.CONNECTED_AND_ACTIVE,
            "1.1.1.1", 8208), listupperBusiness));

    assertNull(businessesPyramidFactory
        .getBusiness(BusinessType.WHOLESALE, list, initialBusinessStates, root)
        .getAgent());
  }

  @Test
  void businessPyramidFactory_should_set_agent_when_getBusiness_for_retail() {
    list = new ArrayList<>();

    final BinaryTreeNode root = new BinaryTreeNode(businessesPyramidFactory
        .getBusiness(BusinessType.WHOLESALE, list, initialBusinessStates,
            new BinaryTreeNode(regionalWarehouseBusiness)));

    assertNotNull(businessesPyramidFactory
        .getBusiness(BusinessType.RETAIL, list, initialBusinessStates, root)
        .getAgent());
  }

  @Test
  void businessPyramidFactory_shouldNot_set_agent_when_getBusiness_for_retail() {
    list = new ArrayList<>();
    final List<WholesaleBusiness> listupperBusiness = new ArrayList<>();
    final UUID uuid = UUID.randomUUID();

    final BinaryTreeNode root = new BinaryTreeNode(businessesPyramidFactory
        .getBusiness(BusinessType.WHOLESALE, list, initialBusinessStates,
            new BinaryTreeNode(regionalWarehouseBusiness)));

    listupperBusiness.add((WholesaleBusiness) root.getValue());
    list.add(new RetailBusiness(20, 30, Collections.emptyMap(), null, uuid,
        new Player("player", uuid, BusinessType.REGIONAL_WAREHOUSE,
            PlayerStatus.CONNECTED_AND_ACTIVE,
            "1.1.1.1", 8208), listupperBusiness, UUID.randomUUID()));

    assertNull(businessesPyramidFactory
        .getBusiness(BusinessType.RETAIL, list, initialBusinessStates, root)
        .getAgent());
  }

  @Test
  void businessPyramidFactory_should_return_null_when_businessType_isNull() {
    final BusinessesPyramidFactory businessesPyramidFactory = new BusinessesPyramidFactory();
    assertNull(businessesPyramidFactory
        .getBusiness(null, Collections.EMPTY_LIST, Collections.EMPTY_MAP, new BinaryTreeNode(1)));
  }
}
