package Models;

class NodeAVL<AnyType>{
    private AnyType element;
    private NodeAVL<AnyType> left;
    private NodeAVL<AnyType> right;
    private int height;

    public NodeAVL( AnyType e ) {
        this( e, null, null );
    }

    public NodeAVL(AnyType element, NodeAVL<AnyType> left, NodeAVL<AnyType> right) {
        this.element = element;
        this.left = left;
        this.right = right;
        this.height = 0;
    }

    public AnyType getElement() {
        return element;
    }

    public NodeAVL<AnyType> getLeft() {
        return left;
    }

    public NodeAVL<AnyType> getRight() {
        return right;
    }

    public int getHeight() {
        return height;
    }

    public void setElement(AnyType element) {
        this.element = element;
    }

    public void setLeft(NodeAVL<AnyType> left) {
        this.left = left;
    }

    public void setRight(NodeAVL<AnyType> right) {
        this.right = right;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}

public class AVLTree<T extends Comparable<T>> implements BalancedTree<T> {
    @Override
    public void insert(T value) {

    }

    @Override
    public boolean remove(T value) {
        return false;
    }

    @Override
    public boolean find(T value) {
        return false;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public void printInOrder() {

    }
}
