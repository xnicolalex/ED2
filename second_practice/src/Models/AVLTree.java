package Models;

class NodeAVL<AnyType> {
    private AnyType element;
    private NodeAVL<AnyType> left;
    private NodeAVL<AnyType> right;
    private int height;

    public NodeAVL(AnyType element) {
        this(element, null, null);
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
    private NodeAVL<T> root;

    private int comparisons = 0;
    private int assignments = 0;

    public AVLTree() {
        this.root = null;
    }

    public int getComparisons() {
        return this.comparisons;
    }

    public int getAssignments() {
        return this.assignments;
    }

    public void resetCounters() {
        this.comparisons = 0;
        this.assignments = 0;
    }

    private int calculateHeight(NodeAVL<T> node) {
        if (node == null) return -1;
        int leftHeight = calculateHeight(node.getLeft());
        int rightHeight = calculateHeight(node.getRight());
        return 1 + Math.max(leftHeight, rightHeight);
    }

    private int getStoredHeight(NodeAVL<T> node) {
        return node == null ? -1 : node.getHeight();
    }

    private int getMax(int a, int b) {
        return Math.max(a, b);
    }

    private NodeAVL<T> rotateWithLeftChild(NodeAVL<T> parent) {
        NodeAVL<T> leftChild = parent.getLeft();
        parent.setLeft(leftChild.getRight()); this.assignments++;
        leftChild.setRight(parent); this.assignments++;

        parent.setHeight(getMax(getStoredHeight(parent.getLeft()), getStoredHeight(parent.getRight())) + 1); this.assignments++;
        leftChild.setHeight(getMax(getStoredHeight(leftChild.getLeft()), getStoredHeight(leftChild.getRight())) + 1); this.assignments++;

        return leftChild;
    }

    private NodeAVL<T> rotateWithRightChild(NodeAVL<T> parent) {
        NodeAVL<T> rightChild = parent.getRight();
        parent.setRight(rightChild.getLeft()); this.assignments++;
        rightChild.setLeft(parent); this.assignments++;

        parent.setHeight(getMax(getStoredHeight(parent.getLeft()), getStoredHeight(parent.getRight())) + 1); this.assignments++;
        rightChild.setHeight(getMax(getStoredHeight(rightChild.getLeft()), getStoredHeight(rightChild.getRight())) + 1); this.assignments++;

        return rightChild;
    }

    private NodeAVL<T> doubleWithLeftChild(NodeAVL<T> parent) {
        parent.setLeft(rotateWithRightChild(parent.getLeft())); this.assignments++;
        return rotateWithLeftChild(parent);
    }

    private NodeAVL<T> doubleWithRightChild(NodeAVL<T> parent) {
        parent.setRight(rotateWithLeftChild(parent.getRight())); this.assignments++;
        return rotateWithRightChild(parent);
    }

    @Override
    public void insert(T value) {
        this.root = insert(value, this.root);
    }

    private NodeAVL<T> insert(T key, NodeAVL<T> node) {
        if (node == null) {
            this.assignments++;
            return new NodeAVL<>(key);
        }

        int compareResult = key.compareTo(node.getElement()); this.comparisons++;

        if (compareResult < 0) {
            node.setLeft(insert(key, node.getLeft())); this.assignments++;
            if (getStoredHeight(node.getLeft()) - getStoredHeight(node.getRight()) == 2) {
                this.comparisons++;
                if (key.compareTo(node.getLeft().getElement()) < 0) {
                    this.comparisons++;
                    node = rotateWithLeftChild(node);
                } else {
                    this.comparisons++;
                    node = doubleWithLeftChild(node);
                }
            }
        } else if (compareResult > 0) {
            node.setRight(insert(key, node.getRight())); this.assignments++;
            if (getStoredHeight(node.getRight()) - getStoredHeight(node.getLeft()) == 2) {
                this.comparisons++;
                if (key.compareTo(node.getRight().getElement()) > 0) {
                    this.comparisons++;
                    node = rotateWithRightChild(node);
                } else {
                    this.comparisons++;
                    node = doubleWithRightChild(node);
                }
            }
        }

        node.setHeight(getMax(getStoredHeight(node.getLeft()), getStoredHeight(node.getRight())) + 1); this.assignments++;
        return node;
    }

    @Override
    public boolean remove(T value) {
        boolean[] removed = {false};
        this.root = remove(value, this.root, removed);
        return removed[0];
    }

    private NodeAVL<T> remove(T value, NodeAVL<T> node, boolean[] removed) {
        if (node == null) return null;

        int compareResult = value.compareTo(node.getElement()); this.comparisons++;

        if (compareResult < 0) {
            node.setLeft(remove(value, node.getLeft(), removed)); this.assignments++;
        } else if (compareResult > 0) {
            node.setRight(remove(value, node.getRight(), removed)); this.assignments++;
        } else {
            removed[0] = true;

            if (node.getLeft() == null && node.getRight() == null) {
                return null;
            } else if (node.getLeft() == null) {
                return node.getRight();
            } else if (node.getRight() == null) {
                return node.getLeft();
            } else {
                NodeAVL<T> successor = findMin(node.getRight());
                node.setElement(successor.getElement()); this.assignments++;
                node.setRight(remove(successor.getElement(), node.getRight(), new boolean[1])); this.assignments++;
            }
        }

        if (node == null) return null;

        node.setHeight(getMax(getStoredHeight(node.getLeft()), getStoredHeight(node.getRight())) + 1); this.assignments++;

        int balanceFactor = getStoredHeight(node.getLeft()) - getStoredHeight(node.getRight());

        if (balanceFactor == 2) {
            if (getStoredHeight(node.getLeft().getLeft()) >= getStoredHeight(node.getLeft().getRight())) {
                return rotateWithLeftChild(node);
            } else {
                return doubleWithLeftChild(node);
            }
        }

        if (balanceFactor == -2) {
            if (getStoredHeight(node.getRight().getRight()) >= getStoredHeight(node.getRight().getLeft())) {
                return rotateWithRightChild(node);
            } else {
                return doubleWithRightChild(node);
            }
        }

        return node;
    }

    private NodeAVL<T> findMin(NodeAVL<T> node) {
        if (node == null) return null;
        while (node.getLeft() != null) {
            node = node.getLeft();
            this.comparisons++;
        }
        return node;
    }

    @Override
    public boolean find(T value) {
        NodeAVL<T> current = this.root;

        while (current != null) {
            int compareResult = value.compareTo(current.getElement()); this.comparisons++;
            if (compareResult < 0) {
                current = current.getLeft();
            } else if (compareResult > 0) {
                current = current.getRight();
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getHeight() {
        return calculateHeight(this.root);
    }

    @Override
    public void printInOrder() {
        printInOrder(this.root);
    }

    private void printInOrder(NodeAVL<T> node) {
        if (node != null) {
            printInOrder(node.getLeft());
            System.out.print(node.getElement() + " ");
            printInOrder(node.getRight());
        }
    }
}
