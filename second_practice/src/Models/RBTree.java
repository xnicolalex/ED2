package Models;

class NodeRB<AnyType> {
    private AnyType element;
    private NodeRB<AnyType> parent, left, right;
    private Color color; // RED or BLACK
    int N; // subtree size counter

    enum Color {
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
        return this.element;
    }

    public void setElement(AnyType element) {
        this.element = element;
    }

    public NodeRB<AnyType> getLeft() {
        return this.left;
    }

    public void setLeft(NodeRB<AnyType> left) {
        this.left = left;
    }

    public NodeRB<AnyType> getRight() {
        return this.right;
    }

    public void setRight(NodeRB<AnyType> right) {
        this.right = right;
    }

    public NodeRB<AnyType> getParent() {
        return this.parent;
    }

    public void setParent(NodeRB<AnyType> parent) {
        this.parent = parent;
    }

    public boolean isRed() {
        return this.color == Color.RED;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}


public class RBTree<T extends Comparable<T>> implements BalancedTree<T> {
    private NodeRB<T> root;

    private boolean isRed(NodeRB<T> node) {
        return node != null && node.isRed();
    }

    private NodeRB<T> rotateLeft(NodeRB<T> parent) {
        System.out.println("Left rotation at " + parent.getElement());

        NodeRB<T> newRoot = parent.getRight();
        parent.setRight(newRoot.getLeft());
        newRoot.setLeft(parent);
        newRoot.setColor(parent.getColor());
        parent.setColor(NodeRB.Color.RED);
        return newRoot;
    }

    private NodeRB<T> rotateRight(NodeRB<T> parent) {
        System.out.println("Right rotation at " + parent.getElement());

        NodeRB<T> newRoot = parent.getLeft();
        parent.setLeft(newRoot.getRight());
        newRoot.setRight(parent);
        newRoot.setColor(parent.getColor());
        parent.setColor(NodeRB.Color.RED);
        return newRoot;
    }

    private void flipColors(NodeRB<T> node) {
        System.out.println("Color flip at " + node.getElement());
        node.setColor(NodeRB.Color.RED);
        if (node.getLeft() != null) {
            node.getLeft().setColor(NodeRB.Color.BLACK);
        }
        if (node.getRight() != null) {
            node.getRight().setColor(NodeRB.Color.BLACK);
        }
    }

    private NodeRB<T> moveRedLeft(NodeRB<T> node) {
        this.flipColors(node);
        if (node.getRight() != null && this.isRed(node.getRight().getLeft())) {
            node.setRight(this.rotateRight(node.getRight()));
            node = this.rotateLeft(node);
        }
        return node;
    }

    private NodeRB<T> moveRedRight(NodeRB<T> node) {
        this.flipColors(node);
        if (node.getLeft() != null && this.isRed(node.getLeft().getLeft())) {
            node = this.rotateRight(node);
        }
        return node;
    }

    private NodeRB<T> fixUp(NodeRB<T> node) {
        if (this.isRed(node.getRight()))
            node = this.rotateLeft(node);
        if (this.isRed(node.getLeft()) && this.isRed(node.getLeft().getLeft()))
            node = this.rotateRight(node);
        if (this.isRed(node.getLeft()) && this.isRed(node.getRight()))
            this.flipColors(node);
        return node;
    }

    @Override
    public void insert(T value) {
        this.root = this.insert(this.root, value);
        this.root.setColor(NodeRB.Color.BLACK);
    }

    private NodeRB<T> insert(NodeRB<T> current, T value) {
        if (current == null) return new NodeRB<>(value);

        int cmp = value.compareTo(current.getElement());
        if (cmp < 0) {
            current.setLeft(this.insert(current.getLeft(), value));
        } else if (cmp > 0) {
            current.setRight(this.insert(current.getRight(), value));
        }

        if (this.isRed(current.getRight()) && !this.isRed(current.getLeft()))
            current = this.rotateLeft(current);
        if (this.isRed(current.getLeft()) && this.isRed(current.getLeft().getLeft()))
            current = this.rotateRight(current);
        if (this.isRed(current.getLeft()) && this.isRed(current.getRight()))
            this.flipColors(current);

        return current;
    }

    @Override
    public boolean remove(T value) {
        if (!this.find(value)) return false;

        if (!this.isRed(this.root.getLeft()) && !this.isRed(this.root.getRight())) {
            this.root.setColor(NodeRB.Color.RED);
        }

        this.root = this.remove(this.root, value);

        if (this.root != null) {
            this.root.setColor(NodeRB.Color.BLACK);
        }

        return true;
    }

    private NodeRB<T> remove(NodeRB<T> current, T value) {
        if (value.compareTo(current.getElement()) < 0) {
            if (!this.isRed(current.getLeft()) && !this.isRed(current.getLeft().getLeft())) {
                current = this.moveRedLeft(current);
            }
            current.setLeft(this.remove(current.getLeft(), value));
        } else {
            if (this.isRed(current.getLeft())) {
                current = this.rotateRight(current);
            }

            if (value.compareTo(current.getElement()) == 0 && current.getRight() == null) {
                return null;
            }

            if (!this.isRed(current.getRight()) && !this.isRed(current.getRight().getLeft())) {
                current = this.moveRedRight(current);
            }

            if (value.compareTo(current.getElement()) == 0) {
                NodeRB<T> minNode = this.findMin(current.getRight());
                current.setElement(minNode.getElement());
                current.setRight(this.removeMin(current.getRight()));
            } else {
                current.setRight(this.remove(current.getRight(), value));
            }
        }

        return this.fixUp(current);
    }

    private NodeRB<T> findMin(NodeRB<T> node) {
        while (node.getLeft() != null) {
            node = node.getLeft();
        }
        return node;
    }

    private NodeRB<T> removeMin(NodeRB<T> node) {
        if (node.getLeft() == null) return null;

        if (!this.isRed(node.getLeft()) && !this.isRed(node.getLeft().getLeft())) {
            node = this.moveRedLeft(node);
        }

        node.setLeft(this.removeMin(node.getLeft()));
        return this.fixUp(node);
    }

    @Override
    public boolean find(T value) {
        NodeRB<T> current = this.root;
        while (current != null) {
            int cmp = value.compareTo(current.getElement());
            if (cmp < 0) {
                current = current.getLeft();
            } else if (cmp > 0) {
                current = current.getRight();
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getHeight() {
        return this.calculateHeight(this.root);
    }

    private int calculateHeight(NodeRB<T> node) {
        if (node == null) return -1;
        return 1 + Math.max(this.calculateHeight(node.getLeft()), this.calculateHeight(node.getRight()));
    }

    @Override
    public void printInOrder() {
        this.printInOrder(this.root);
    }

    private void printInOrder(NodeRB<T> node) {
        if (node != null) {
            this.printInOrder(node.getLeft());
            System.out.print(node.getElement() + " ");
            this.printInOrder(node.getRight());
        }
    }
}
