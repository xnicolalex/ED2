package Models;

class NodeRB<AnyType> {
    private AnyType element;
    private NodeRB<AnyType> parent, left, right;
    private Color color; // RED or BLACK
    int N; // conta subarvores

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

    public NodeRB<AnyType> getParent() {
        return parent;
    }

    public void setParent(NodeRB<AnyType> parent) {
        this.parent = parent;
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
        if (node == null) return false;
        return node.isRed();
    }

    private NodeRB<T> rotateLeft(NodeRB<T> parent) {
        System.out.println("Rotação à esquerda em " + parent.getElement());

        NodeRB<T> newRoot = parent.getRight();
        parent.setRight(newRoot.getLeft());
        newRoot.setLeft(parent);
        newRoot.setColor(parent.getColor());
        parent.setColor(NodeRB.Color.RED);
        return newRoot;
    }

    private NodeRB<T> rotateRight(NodeRB<T> parent) {
        System.out.println("Rotação à direita em " + parent.getElement());

        NodeRB<T> newRoot = parent.getLeft();
        parent.setLeft(newRoot.getRight());
        newRoot.setRight(parent);
        newRoot.setColor(parent.getColor());
        parent.setColor(NodeRB.Color.RED);
        return newRoot;
    }

    private void flipColors(NodeRB<T> node) {
        System.out.println("Flip de cores em " + node.getElement());
        node.setColor(NodeRB.Color.RED);
        if (node.getLeft() != null) {
            node.getLeft().setColor(NodeRB.Color.BLACK);
        }
        if (node.getRight() != null) {
            node.getRight().setColor(NodeRB.Color.BLACK);
        }
    }

    private NodeRB<T> moveRedLeft(NodeRB<T> node) {
        flipColors(node);
        if (node.getRight() != null && isRed(node.getRight().getLeft())) {
            node.setRight(rotateRight(node.getRight()));
            node = rotateLeft(node);
        }
        return node;
    }

    private NodeRB<T> moveRedRight(NodeRB<T> node) {
        flipColors(node);
        if (node.getLeft() != null && isRed(node.getLeft().getLeft())) {
            node = rotateRight(node);
        }
        return node;
    }

    private NodeRB<T> fixUp(NodeRB<T> node) {
        if (isRed(node.getRight()))
            node = rotateLeft(node);
        if (isRed(node.getLeft()) && isRed(node.getLeft().getLeft()))
            node = rotateRight(node);
        if (isRed(node.getLeft()) && isRed(node.getRight()))
            flipColors(node);
        return node;
    }



    @Override
    public void insert(T value) {
        root = insert(root, value);
        root.setColor(NodeRB.Color.BLACK);
    }

    private NodeRB<T> insert(NodeRB<T> current, T value) {
        if (current == null) return new NodeRB<>(value);

        int cmp = value.compareTo(current.getElement());
        if (cmp < 0)
            current.setLeft(insert(current.getLeft(), value));
        else if (cmp > 0)
            current.setRight(insert(current.getRight(), value));
        else
            ; // duplicatas não são inseridas

        if (isRed(current.getRight()) && !isRed(current.getLeft()))
            current = rotateLeft(current);
        if (isRed(current.getLeft()) && isRed(current.getLeft().getLeft()))
            current = rotateRight(current);
        if (isRed(current.getLeft()) && isRed(current.getRight()))
            flipColors(current);

        return current;
    }


    @Override
    public boolean remove(T value) {
        if (!find(value)) return false;

        // se ambos filhos da raiz forem pretos, tornamos a raiz vermelha temporariamente
        if (!isRed(root.getLeft()) && !isRed(root.getRight())) {
            root.setColor(NodeRB.Color.RED);
        }

        root = remove(root, value);

        if (root != null) {
            root.setColor(NodeRB.Color.BLACK);
        }

        return true;
    }

    private NodeRB<T> remove(NodeRB<T> current, T value) {
        if (value.compareTo(current.getElement()) < 0) {
            if (!isRed(current.getLeft()) && !isRed(current.getLeft().getLeft())) {
                current = moveRedLeft(current);
            }
            current.setLeft(remove(current.getLeft(), value));
        } else {
            if (isRed(current.getLeft())) {
                current = rotateRight(current);
            }

            if (value.compareTo(current.getElement()) == 0 && current.getRight() == null) {
                return null;
            }

            if (!isRed(current.getRight()) && !isRed(current.getRight().getLeft())) {
                current = moveRedRight(current);
            }

            if (value.compareTo(current.getElement()) == 0) {
                NodeRB<T> minNode = findMin(current.getRight());
                current.setElement(minNode.getElement());
                current.setRight(removeMin(current.getRight()));
            } else {
                current.setRight(remove(current.getRight(), value));
            }
        }

        return fixUp(current);
    }

    private NodeRB<T> findMin(NodeRB<T> node) {
        while (node.getLeft() != null) {
            node = node.getLeft();
        }
        return node;
    }

    private NodeRB<T> removeMin(NodeRB<T> node) {
        if (node.getLeft() == null) return null;

        if (!isRed(node.getLeft()) && !isRed(node.getLeft().getLeft())) {
            node = moveRedLeft(node);
        }

        node.setLeft(removeMin(node.getLeft()));
        return fixUp(node);
    }

    @Override
    public boolean find(T value) {
        NodeRB<T> current = root;
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
        return calculateHeight(root);
    }

    private int calculateHeight(NodeRB<T> node) {
        if (node == null) return -1;
        return 1 + Math.max(calculateHeight(node.getLeft()), calculateHeight(node.getRight()));
    }

    @Override
    public void printInOrder() {
        printInOrder(root);
    }

    private void printInOrder(NodeRB<T> node) {
        if (node != null) {
            printInOrder(node.getLeft());
            System.out.print(node.getElement() + " ");
            printInOrder(node.getRight());
        }
    }
}
