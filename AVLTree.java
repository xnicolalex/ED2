package Models;

//import org.w3c.dom.Node;

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
    private NodeAVL<T> root;

    public AVLTree(){
        root = null;
    }

    // Métodos de altura

    private int calculateHeight(NodeAVL<T> node) {
        if (node == null) {
            return -1;
        }

        int leftHeight = calculateHeight(node.getLeft());
        int rightHeight = calculateHeight(node.getRight());

        int maxHeight = Math.max(leftHeight, rightHeight);

        return 1 + maxHeight;
    }

    private int getStoredHeight(NodeAVL<T> node){
        if (node == null){
            return -1;
        } else {
            return node.getHeight();
        }
    }

    private int getMax(int a, int b){
        return Math.max(a, b);
    }


    // Métodos de rotação
    // Rotação simples com filho esquerdo
    private NodeAVL<T> rotateWithLeftChild(NodeAVL<T> parent) {
        System.out.println("Rotação simples com filho esquerdo (LL) no nó: " + parent.getElement());

        NodeAVL<T> leftChild = parent.getLeft();
        parent.setLeft(leftChild.getRight());
        leftChild.setRight(parent);

        parent.setHeight(getMax(getStoredHeight(parent.getLeft()), getStoredHeight(parent.getRight())) + 1);
        leftChild.setHeight(getMax(getStoredHeight(leftChild.getLeft()), getStoredHeight(leftChild.getRight())) + 1);

        return leftChild;
    }

    // Rotação simples com filho direito
    private NodeAVL<T> rotateWithRightChild(NodeAVL<T> parent) {
        System.out.println("Rotação simples com filho direito (RR) no nó: " + parent.getElement());

        NodeAVL<T> rightChild = parent.getRight();
        parent.setRight(rightChild.getLeft());
        rightChild.setLeft(parent);

        parent.setHeight(getMax(getStoredHeight(parent.getLeft()), getStoredHeight(parent.getRight())) + 1);
        rightChild.setHeight(getMax(getStoredHeight(rightChild.getLeft()), getStoredHeight(rightChild.getRight())) + 1);

        return rightChild;
    }

    // Rotação dupla
    private NodeAVL<T> doubleWithLeftChild(NodeAVL<T> parent) {
        System.out.println("Rotação dupla à esquerda (LR) no nó: " + parent.getElement());

        parent.setLeft(rotateWithRightChild(parent.getLeft()));
        return rotateWithLeftChild(parent);
    }

    // Rotação dupla
    private NodeAVL<T> doubleWithRightChild(NodeAVL<T> parent) {
        System.out.println("Rotação dupla à direita (RL) no nó: " + parent.getElement());

        parent.setRight(rotateWithLeftChild(parent.getRight()));
        return rotateWithRightChild(parent);
    }


    @Override
    public void insert(T value) {
        root = insert(value, root);
    }

    private NodeAVL<T> insert(T key, NodeAVL<T> node) {
        if (node == null) {
            return new NodeAVL<>(key);
        }

        int compareResult = key.compareTo(node.getElement());

        if (compareResult < 0) {
            node.setLeft(insert(key, node.getLeft()));

            if (getStoredHeight(node.getLeft()) - getStoredHeight(node.getRight()) == 2) {
                if (key.compareTo(node.getLeft().getElement()) < 0) {
                    node = rotateWithLeftChild(node); // LL
                } else {
                    node = doubleWithLeftChild(node); // LR
                }
            }
        } else if (compareResult > 0) {
            node.setRight(insert(key, node.getRight()));

            if (getStoredHeight(node.getRight()) - getStoredHeight(node.getLeft()) == 2) {
                if (key.compareTo(node.getRight().getElement()) > 0) {
                    node = rotateWithRightChild(node); // RR
                } else {
                    node = doubleWithRightChild(node); // RL
                }
            }
        }

        node.setHeight(getMax(getStoredHeight(node.getLeft()), getStoredHeight(node.getRight())) + 1);
        return node;
    }

    @Override
    public boolean remove(T value) {
        boolean[] removed = {false};
        root = remove(value, root, removed);
        return removed[0];
    }

    private NodeAVL<T> remove(T value, NodeAVL<T> node, boolean[] removed) {
        if (node == null) {
            return null;
        }

        int compareResult = value.compareTo(node.getElement());

        if (compareResult < 0) {
            node.setLeft(remove(value, node.getLeft(), removed));
        } else if (compareResult > 0) {
            node.setRight(remove(value, node.getRight(), removed));
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
                node.setElement(successor.getElement());
                node.setRight(remove(successor.getElement(), node.getRight(), new boolean[1]));
            }
        }

        if (node == null) return null;

        node.setHeight(getMax(getStoredHeight(node.getLeft()), getStoredHeight(node.getRight())) + 1);

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
        }
        return node;
    }

    @Override
    public boolean find(T value) {
        NodeAVL<T> current = root;

        while (current != null) {
            int compareResult = value.compareTo(current.getElement());

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
        return calculateHeight(root);
    }

    @Override
    public void printInOrder() {
        printInOrder(root);
    }

    private void printInOrder(NodeAVL<T> node) {
        if (node != null){
            printInOrder(node.getLeft());
            System.out.print(node.getElement() + " ");
            printInOrder(node.getRight());
        }
    }
}

