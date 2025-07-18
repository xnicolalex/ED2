package Models;
import java.util.*;

class NodeAVL<K extends Comparable<K>, V> {
    private TreeNode<K, V> pair;
    private NodeAVL<K, V> left, right;
    private int height;

    public NodeAVL(TreeNode<K, V> pair) {
        this.pair = pair;
        this.height = 0;
    }

    public K getKey() { return pair.getKey(); }
    public V getValue() { return pair.getValue(); }
    public void setValue(V value) { this.pair.setValue(value); }

    public NodeAVL<K, V> getLeft() { return left; }
    public NodeAVL<K, V> getRight() { return right; }
    public void setLeft(NodeAVL<K, V> left) { this.left = left; }
    public void setRight(NodeAVL<K, V> right) { this.right = right; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }

    public TreeNode<K, V> getPair() { return pair; }

    public void setPair(TreeNode<K, V> pair) { this.pair = pair; }

    @Override
    public String toString() {
        return String.format(
                "NodeAVL{key=%s, value=%s, height=%d, left=%s, right=%s}",
                getKey(),
                getValue(),
                height,
                (left != null ? left.getKey() : "null"),
                (right != null ? right.getKey() : "null")
        );
    }
}

public class AVLTree<K extends Comparable<K>, V> implements BalancedTree<K, V> {
    private NodeAVL<K, V> root;

    private int comparisons = 0;
    private int assignments = 0;

    @Override
    public void insert(K key, V value) {
        root = insert(key, value, root);
    }

    private NodeAVL<K, V> insert(K key, V value, NodeAVL<K, V> node) {
        if (node == null) {
            assignments++;
            return new NodeAVL<>(new TreeNode<>(key, value));
        }

        comparisons++;
        int cmp = key.compareTo(node.getKey());

        if (cmp < 0) {
            node.setLeft(insert(key, value, node.getLeft()));
            assignments++;
        } else if (cmp > 0) {
            node.setRight(insert(key, value, node.getRight()));
            assignments++;
        } else {
            node.setValue(value);
            assignments++;
        }

        node.setHeight(1 + Math.max(height(node.getLeft()), height(node.getRight())));
        assignments++;

        return balance(node);
    }

    private NodeAVL<K, V> balance(NodeAVL<K, V> node) {
        int balanceFactor = height(node.getLeft()) - height(node.getRight());
        comparisons += 2;

        if (balanceFactor > 1) {
            comparisons++;
            if (height(node.getLeft().getLeft()) >= height(node.getLeft().getRight())) {
                comparisons++;
                return rotateRight(node);
            } else {
                node.setLeft(rotateLeft(node.getLeft()));
                assignments++;
                return rotateRight(node);
            }
        } else if (balanceFactor < -1) {
            comparisons++;
            if (height(node.getRight().getRight()) >= height(node.getRight().getLeft())) {
                comparisons++;
                return rotateLeft(node);
            } else {
                node.setRight(rotateRight(node.getRight()));
                assignments++;
                return rotateLeft(node);
            }
        }

        return node;
    }

    private NodeAVL<K, V> rotateRight(NodeAVL<K, V> y) {
        NodeAVL<K, V> x = y.getLeft();
        assignments++;

        y.setLeft(x.getRight());
        assignments++;
        x.setRight(y);
        assignments++;

        y.setHeight(1 + Math.max(height(y.getLeft()), height(y.getRight())));
        assignments++;
        x.setHeight(1 + Math.max(height(x.getLeft()), height(x.getRight())));
        assignments++;

        return x;
    }

    private NodeAVL<K, V> rotateLeft(NodeAVL<K, V> x) {
        NodeAVL<K, V> y = x.getRight();
        assignments++;

        x.setRight(y.getLeft());
        assignments++;
        y.setLeft(x);
        assignments++;

        x.setHeight(1 + Math.max(height(x.getLeft()), height(x.getRight())));
        assignments++;
        y.setHeight(1 + Math.max(height(y.getLeft()), height(y.getRight())));
        assignments++;

        return y;
    }

    private int height(NodeAVL<K, V> node) {
        comparisons++;
        return node == null ? -1 : node.getHeight();
    }

    @Override
    public V find(K key) {
        NodeAVL<K, V> current = root;

        while (current != null) {
            comparisons++;
            int cmp = key.compareTo(current.getKey());

            if (cmp < 0) current = current.getLeft();
            else if (cmp > 0) current = current.getRight();
            else return current.getValue();
        }

        return null;
    }

    public ArrayList<Transaction> searchByTimestamp(String startTimestamp, String endTimestamp) {
        ArrayList<Transaction> result = new ArrayList<>();
        searchByTimestamp(root, startTimestamp, endTimestamp, result);
        return result;
    }

    private void searchByTimestamp(NodeAVL<K, V> node, String startTimestamp, String endTimestamp, ArrayList<Transaction> result) {
        if (node == null) return;

        // Check the timestamp of the current node
        if (node.getValue() instanceof Transaction t){
            if (isWithinTimestampRange(t.getTimestamp(), startTimestamp, endTimestamp)) {
                result.add(t);
            }

            // Search in left subtree
            searchByTimestamp(node.getLeft(), startTimestamp, endTimestamp, result);

            // Search in right subtree
            searchByTimestamp(node.getRight(), startTimestamp, endTimestamp, result);
        }
    }

    // Helper method to check if the transaction's timestamp is within the given range
    private boolean isWithinTimestampRange(String transactionTimestamp, String startTimestamp, String endTimestamp) {
        comparisons += 3;
        return transactionTimestamp.compareTo(startTimestamp) >= 0 && transactionTimestamp.compareTo(endTimestamp) <= 0;
    }

    @Override
    public boolean remove(K key) {
        boolean[] removed = {false};
        root = remove(key, root, removed);
        return removed[0];
    }

    private NodeAVL<K, V> remove(K key, NodeAVL<K, V> node, boolean[] removed) {
        if (node == null) {
            comparisons++;
            return null;
        }

        comparisons++;
        int cmp = key.compareTo(node.getKey());

        if (cmp < 0) {
            node.setLeft(remove(key, node.getLeft(), removed));
            assignments++;
        } else if (cmp > 0) {
            node.setRight(remove(key, node.getRight(), removed));
            assignments++;
        } else {
            removed[0] = true;
            if (node.getLeft() == null) return node.getRight();
            if (node.getRight() == null) return node.getLeft();

            NodeAVL<K, V> min = findMin(node.getRight());
            assignments++;
            node.setPair(min.getPair());
            node.setRight(remove(min.getKey(), node.getRight(), new boolean[1]));
        }

        node.setHeight(1 + Math.max(height(node.getLeft()), height(node.getRight())));
        assignments++;
        return balance(node);
    }

    private NodeAVL<K, V> findMin(NodeAVL<K, V> node) {
        while (node.getLeft() != null) {
            comparisons++;
            node = node.getLeft();
        }
        return node;
    }

    @Override
    public int getHeight() {
        return height(root);
    }

    @Override
    public void printInOrder() {
        printInOrder(root);
    }

    private void printInOrder(NodeAVL<K, V> node) {
        if (node != null) {
            printInOrder(node.getLeft());
            System.out.println(node.getPair());
            printInOrder(node.getRight());
        }
    }

    public List<V> inOrder() {
        List<V> result = new ArrayList<>();
        inOrder(root, result);
        return result;
    }

    private void inOrder(NodeAVL<K, V> node, List<V> result) {
        if (node != null) {
            inOrder(node.getLeft(), result);
            result.add(node.getValue());
            inOrder(node.getRight(), result);
        }
    }

    public int getComparisons() { return comparisons; }
    public int getAssignments() { return assignments; }

    public void resetCounters() {
        comparisons = 0;
        assignments = 0;
    }

    @Override
    public String toString() {
        // Simply initiate the preorder traversal and print each node
        preorderPrint(root);
        return ""; // You can return an empty string as the method signature requires a return
    }

    private void preorderPrint(NodeAVL<K, V> node) {
        if (node == null) return;

        // Print the current node (in preorder)
        System.out.println(node);

        // Print left child (if exists)
        if (node.getLeft() != null) {
            preorderPrint(node.getLeft());
        }

        // Print right child (if exists)
        if (node.getRight() != null) {
            preorderPrint(node.getRight());
        }
    }
}

