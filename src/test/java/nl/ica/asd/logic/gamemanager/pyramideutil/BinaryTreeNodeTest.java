package nl.ica.asd.logic.gamemanager.pyramideutil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import nl.ica.asd.logic.domain.builders.FactoryBusinessBuilder;
import nl.ica.asd.logic.domain.builders.RetailBusinessBuilder;
import nl.ica.asd.logic.domain.businesses.RetailBusiness;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BinaryTreeNodeTest {

  private BinaryTreeNode binaryTreeNode;

  @BeforeEach
  void setUp() {
    binaryTreeNode = new BinaryTreeNode<>(FactoryBusinessBuilder.aFactoryBusiness().build());
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  void getLeftChild_should_returnNull() {

    assertNull(binaryTreeNode.getLeftChild());
  }

  @Test
  void getLeftChild_should_returnLeftChild() {
    BinaryTreeNode mockBinaryTreeNode = mock(BinaryTreeNode.class);
    binaryTreeNode.setLeftChild(mockBinaryTreeNode);

    assertNotNull(binaryTreeNode.getLeftChild());
    assertEquals(mockBinaryTreeNode, binaryTreeNode.getLeftChild());
  }

  @Test
  void setLeftChild_should_returnNull() {
    assertNull(binaryTreeNode.getLeftChild());
  }

  @Test
  void setLeftChild() {
    BinaryTreeNode mockBinaryTreeNode = mock(BinaryTreeNode.class);

    binaryTreeNode.setLeftChild(mockBinaryTreeNode);

    assertNotNull(binaryTreeNode.getLeftChild());
  }

  @Test
  void getRightChild_should_returnNull() {

    assertNull(binaryTreeNode.getRightChild());
  }

  @Test
  void getRightChild_should_returnLeftChild() {
    BinaryTreeNode mockBinaryTreeNode = mock(BinaryTreeNode.class);
    binaryTreeNode.setRightChild(mockBinaryTreeNode);

    assertNotNull(binaryTreeNode.getRightChild());
    assertEquals(mockBinaryTreeNode, binaryTreeNode.getRightChild());
  }

  @Test
  void setRightChild_should_returnNull() {
    assertNull(binaryTreeNode.getRightChild());
  }

  @Test
  void setRightChild() {
    BinaryTreeNode mockBinaryTreeNode = mock(BinaryTreeNode.class);

    binaryTreeNode.setRightChild(mockBinaryTreeNode);

    assertNotNull(binaryTreeNode.getRightChild());
  }

  @Test
  void getValue_should_returnNull() {
    BinaryTreeNode node = new BinaryTreeNode(null);
    assertNull(node.getValue());
  }

  @Test
  void getValue_should_returnValue() {
    binaryTreeNode.setValue(FactoryBusinessBuilder.aFactoryBusiness().build());
    assertNotNull(binaryTreeNode.getValue());
  }

  @Test
  void setValue_should_setValue() {
    binaryTreeNode.setValue(RetailBusinessBuilder.aRetailBusiness().build());
    assertNotNull(binaryTreeNode.getValue());
    assertTrue(binaryTreeNode.getValue() instanceof RetailBusiness);
  }
}