package Models;

class NodeRB<AnyType> {
    private AnyType element;
    private NodeRB<AnyType> parent, left, right;
    private Color color; // RED or BLACK
    private int N;

    enum Color{
        RED, BLACK
    }

    public NodeRB(AnyType element) {
        this(element, null, null);
    }

    public NodeRB(AnyType element, NodeRB<AnyType> left, NodeRB<AnyType> right) {
        this.element = element;
        this.left = left;
        this.right = right;
        this.color = Color.RED;
    }

    public AnyType getElement() {
        return element;
    }

    public void setElement(AnyType element) {
        this.element = element;
    }

    public NodeRB<AnyType> getLeft() {
        return left;
    }

    public void setLeft(NodeRB<AnyType> left) {
        this.left = left;
    }

    public NodeRB<AnyType> getRight() {
        return right;
    }

    public void setRight(NodeRB<AnyType> right) {
        this.right = right;
    }

    public boolean isRed() {
        return color == Color.RED;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}


public class RBTree<T extends Comparable<T>> implements BalancedTree<T> {
    private NodeRB<T> root;

    private boolean isRed(NodeRB<T> node) {
        return false;
    }

    private NodeRB<T> rotateLeft(NodeRB<T> h) {
        return null;
    }

    private NodeRB<T> rotateRight(NodeRB<T> h) {
        return null;
    }

    private void flipColors(NodeRB<T> h) {

    }

    @Override
    public void insert(T value) {
    }

    private NodeRB<T> insert(NodeRB<T> h, T value) {
        return null;
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