package nl.ica.asd.logic.gamemanager.pyramideutil;

public class BinaryTreeNode<T> {

  private BinaryTreeNode<T> leftChild;
  private BinaryTreeNode<T> rightChild;

  private T value;

  public BinaryTreeNode(T value) {
    this.value = value;
  }

  public BinaryTreeNode<T> getLeftChild() {
    return leftChild;
  }

  public void setLeftChild(BinaryTreeNode<T> leftChild) {
    this.leftChild = leftChild;
  }

  public BinaryTreeNode<T> getRightChild() {
    return rightChild;
  }

  public void setRightChild(BinaryTreeNode<T> rightChild) {
    this.rightChild = rightChild;
  }

  public T getValue() {
    return value;
  }

  public void setValue(T value) {
    this.value = value;
  }

}
