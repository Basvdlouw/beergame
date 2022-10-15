package nl.ica.asd.logic.gamemanager.pyramideutil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import nl.ica.asd.logic.domain.Business;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

class BinaryTreeTest {

  private BinaryTree<Business> businessBinaryTree;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
    businessBinaryTree = new BinaryTree<>();
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  void getRoot_should_beEqual() {
    BinaryTreeNode<Business> businessBinaryTreeNode = mock(BinaryTreeNode.class);

    businessBinaryTree
        .setRoot(businessBinaryTreeNode);

    assertNotNull(businessBinaryTree.getRoot());
    assertEquals(businessBinaryTreeNode, businessBinaryTree.getRoot());
  }

  @Test
  void getRoot_should_notEqual() {
    BinaryTreeNode<Business> businessBinaryTreeNode = mock(BinaryTreeNode.class);
    BinaryTreeNode<Business> differentbusinessBinaryTreeNode = mock(BinaryTreeNode.class);
    businessBinaryTree
        .setRoot(businessBinaryTreeNode);
    assertNotEquals(differentbusinessBinaryTreeNode, businessBinaryTree.getRoot());
  }

  @Test
  void getRoot_should_returnRoot() {
    BinaryTree tree = new BinaryTree();
    tree.setRoot(new BinaryTreeNode("test"));
    assertEquals("test", tree.getRoot().getValue().toString());
  }
}