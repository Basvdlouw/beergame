package nl.ica.asd.logic.gamemanager.gamestatemanager.businessinitializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import nl.ica.asd.logic.domain.Action;
import nl.ica.asd.logic.domain.BeerGame;
import nl.ica.asd.logic.domain.Business;
import nl.ica.asd.logic.domain.BusinessType;
import nl.ica.asd.logic.domain.actions.DeliveryAction;
import nl.ica.asd.logic.domain.actions.OrderAction;
import nl.ica.asd.logic.domain.businesses.FactoryBusiness;
import nl.ica.asd.logic.domain.businesses.RegionalWarehouseBusiness;
import nl.ica.asd.logic.domain.businesses.RetailBusiness;
import nl.ica.asd.logic.domain.businesses.WholesaleBusiness;
import nl.ica.asd.logic.gamemanager.pyramideutil.BinaryTree;
import nl.ica.asd.logic.gamemanager.pyramideutil.BinaryTreeNode;
import nl.ica.asd.logic.gamemanager.pyramideutil.BusinessesPyramidFactory;
import nl.ica.asd.logic.statecalculator.BusinessState;

public class PyramidInitializer extends BusinessInitializer {


  public PyramidInitializer() {
    super();
  }

  public PyramidInitializer init(){
    return this;
  }

  @Override
  public List<Business> initialize(BeerGame beerGame, List<Business> businesses) {
    final Map<Class<? extends Business>, BusinessState> initialBusinessStates =
        beerGame.getGameConfiguration().getInitialBusinessStates();

    BinaryTree<Business> binaryTree = new BinaryTree<>();
    BinaryTreeNode root = new BinaryTreeNode<>(null);
    binaryTree.setRoot(treeBuilderAgentsForPyramid(root, businesses, initialBusinessStates));

    List<Business> testlist = treeLooper(root);
    return fixInitialOrdersAndDeliveries(initialBusinessStates, testlist);
  }

  private List<Business> treeLooper(BinaryTreeNode root) {
    final List<Business> tempList = new ArrayList<>();
    tempList.add((Business) root.getValue());

    if (root.getLeftChild() != null) {
      tempList.addAll(treeLooper(root.getLeftChild()));
    }
    if (root.getRightChild() != null) {
      tempList.addAll(treeLooper(root.getRightChild()));
    }
    if (root.getLeftChild() == null && root.getRightChild() == null) {
    }
    return tempList;
  }

  private BinaryTreeNode treeBuilderAgentsForPyramid(BinaryTreeNode<Business> root,
      List<Business> businesses,
      Map<Class<? extends Business>, BusinessState> initialBusinessStates) {

    List<Business> tempBusinesses = businesses;
    BusinessesPyramidFactory businessesPyramidFactory = new BusinessesPyramidFactory();

    if (root.getValue() == null) {
      root.setValue(businessesPyramidFactory
          .getBusiness(BusinessType.FACTORY, businesses, initialBusinessStates,
              root));
    }

    if (root.getValue() instanceof RetailBusiness) {
      return root;
    }

    if (root.getValue() instanceof FactoryBusiness) {
      setChildren(root, tempBusinesses, initialBusinessStates, businessesPyramidFactory,
          BusinessType.REGIONAL_WAREHOUSE);

      treeBuilderAgentsForPyramid(root.getLeftChild(), tempBusinesses, initialBusinessStates);
      treeBuilderAgentsForPyramid(root.getRightChild(), tempBusinesses, initialBusinessStates);

    } else if (root.getValue() instanceof RegionalWarehouseBusiness) {

      setChildren(root, tempBusinesses, initialBusinessStates, businessesPyramidFactory,
          BusinessType.WHOLESALE);

      treeBuilderAgentsForPyramid(root.getLeftChild(), tempBusinesses, initialBusinessStates);
      treeBuilderAgentsForPyramid(root.getRightChild(), tempBusinesses, initialBusinessStates);

    } else if (root.getValue() instanceof WholesaleBusiness) {

      setChildren(root, tempBusinesses, initialBusinessStates, businessesPyramidFactory,
          BusinessType.RETAIL);

    }
    return root;
  }

  private void setChildren(BinaryTreeNode root, List<Business> tempBusinesses,
      Map<Class<? extends Business>, BusinessState> initialBusinessStates,
      BusinessesPyramidFactory businessesPyramidFactory, BusinessType type) {
    root.setLeftChild(new BinaryTreeNode<>(businessesPyramidFactory
        .getBusiness(type, tempBusinesses, initialBusinessStates,
            root)));
    tempBusinesses.remove(root.getLeftChild().getValue());
    root.setRightChild(new BinaryTreeNode<>(businessesPyramidFactory
        .getBusiness(type, tempBusinesses, initialBusinessStates,
            root)));
    tempBusinesses.remove(root.getRightChild().getValue());
  }

  private List<Business> fixInitialOrdersAndDeliveries(
      Map<Class<? extends Business>, BusinessState> initialBusinessStates,
      List<Business> businesses) {
    List<Business> businessesWithInitialState = new ArrayList<>();

    for (Business business : businesses) {

      BusinessState businessState = initialBusinessStates.get(business.getClass());
      List<Business> lowerLevelBusinesses = getLowerLevelBusinesses(business, businesses);
      List<Action> secondRoundActions = new ArrayList<>();
      List<Action> firstRoundActions = new ArrayList<>();
      List<Action> initialRoundActions = new ArrayList<>();

      Business upperLevelBusiness = getUpperLevelBusinesses(business);

      UUID upperLevelBusinessUUID = upperLevelBusiness.equals(business)
          ? ((FactoryBusiness) upperLevelBusiness).getSupplierUUID()
          : upperLevelBusiness.getUUID();

      initialRoundActions.add(
          new OrderAction(businessState.getOutgoingOrders(), business.getUUID(),
              upperLevelBusinessUUID)); // outgoing order

      initialRoundActions.add(
          new DeliveryAction(businessState.getIncomingGoods(), upperLevelBusinessUUID,
              business.getUUID())); // incoming goods
      firstRoundActions.add(
          new DeliveryAction(businessState.getIncomingGoods(), upperLevelBusinessUUID,
              business.getUUID())); // incoming goods

      if (business instanceof FactoryBusiness) {
        secondRoundActions.add(
            new DeliveryAction(businessState.getIncomingGoods(), upperLevelBusinessUUID,
                business.getUUID())); // incoming goods
      }

      for (Business lowerLevelBusiness : lowerLevelBusinesses) {

        UUID lowerLevelBusinessUUID = lowerLevelBusiness.equals(business)
            ? ((RetailBusiness) lowerLevelBusiness).getCustomerUUID()
            : lowerLevelBusiness.getUUID();

        initialRoundActions.add(
            new OrderAction(businessState.getIncomingOrders(), lowerLevelBusinessUUID,
                business.getUUID())); // incoming order

        initialRoundActions.add(
            new DeliveryAction(businessState.getOutgoingGoods(), business.getUUID(),
                lowerLevelBusinessUUID)); // outgoing goods

        firstRoundActions.add(
            new OrderAction(businessState.getIncomingOrders(), lowerLevelBusinessUUID,
                business.getUUID())); // incoming order

        businessesWithInitialState.add(business.getBusinessType().getBuilderFromBusiness(business)
            .withBusinessState(businessState).build());
      }

      business.getActionsPerRound().put(0, initialRoundActions);
      business.getActionsPerRound().put(1, firstRoundActions);
      business.getActionsPerRound().put(2, secondRoundActions);
    }
    return businessesWithInitialState;
  }

  private Business getUpperLevelBusinesses(Business business) {
    switch (business.getBusinessType()) {
      case RETAIL:
        return ((RetailBusiness) business).getWholesaleBusinesses().get(0);
      case WHOLESALE:
        return ((WholesaleBusiness) business).getRegionalWarehouseBusinesses().get(0);
      case REGIONAL_WAREHOUSE:
        return ((RegionalWarehouseBusiness) business).getFactoryBusinesses().get(0);
      case FACTORY:
        return business;
    }
    return null;
  }

  private List<Business> getLowerLevelBusinesses(Business business, List<Business> businesses) {
    switch (business.getBusinessType()) {
      case RETAIL:
        return Arrays.asList(business);
      case WHOLESALE:
        return businesses.stream()
            .filter(x -> x instanceof RetailBusiness)
            .filter(x -> getUpperLevelBusinesses(x).equals(business))
            .collect(Collectors.toList());
      case REGIONAL_WAREHOUSE:
        return businesses.stream()
            .filter(x -> x instanceof WholesaleBusiness)
            .filter(x -> getUpperLevelBusinesses(x).equals(business))
            .collect(Collectors.toList());
      case FACTORY:
        return businesses.stream()
            .filter(x -> x instanceof RegionalWarehouseBusiness)
            .filter(x -> getUpperLevelBusinesses(x).equals(business))
            .collect(Collectors.toList());
    }
    return null;
  }
}
