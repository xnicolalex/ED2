package Models;
import java.util.*;

class NodeRB<K extends Comparable<K>, V> {
    private TreeNode<K, V> pair;
    private NodeRB<K, V> left, right;
    private Color color;

    enum Color { RED, BLACK }

    public NodeRB(TreeNode<K, V> pair) {
        this.pair = pair;
        this.color = Color.RED;
    }

    public K getKey() { return pair.getKey(); }
    public V getValue() { return pair.getValue(); }
    public void setValue(V value) { this.pair.setValue(value); }

    public NodeRB<K, V> getLeft() { return left; }
    public void setLeft(NodeRB<K, V> left) { this.left = left; }

    public NodeRB<K, V> getRight() { return right; }
    public void setRight(NodeRB<K, V> right) { this.right = right; }

    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }

    public boolean isRed() { return color == Color.RED; }
}

public class RBTree<K extends Comparable<K>, V> implements BalancedTree<K, V> {
    private NodeRB<K, V> root;
    private int comparisons = 0;
    private int assignments = 0;

    private boolean isRed(NodeRB<K, V> node) {
        comparisons++;
        return node != null && node.isRed();
    }

    private NodeRB<K, V> rotateLeft(NodeRB<K, V> h) {
        NodeRB<K, V> x = h.getRight(); assignments++;
        h.setRight(x.getLeft()); assignments++;
        x.setLeft(h); assignments++;
        x.setColor(h.getColor()); assignments++;
        h.setColor(NodeRB.Color.RED); assignments++;
        return x;
    }

    private NodeRB<K, V> rotateRight(NodeRB<K, V> h) {
        NodeRB<K, V> x = h.getLeft(); assignments++;
        h.setLeft(x.getRight()); assignments++;
        x.setRight(h); assignments++;
        x.setColor(h.getColor()); assignments++;
        h.setColor(NodeRB.Color.RED); assignments++;
        return x;
    }

    private void flipColors(NodeRB<K, V> h) {
        h.setColor(NodeRB.Color.RED); assignments++;
        if (h.getLeft() != null) {
            comparisons++;
            h.getLeft().setColor(NodeRB.Color.BLACK); assignments++;
        }
        if (h.getRight() != null) {
            comparisons++;
            h.getRight().setColor(NodeRB.Color.BLACK); assignments++;
        }
    }

    @Override
    public void insert(K key, V value) {
        root = insert(root, key, value);
        root.setColor(NodeRB.Color.BLACK); assignments++;
    }

    private NodeRB<K, V> insert(NodeRB<K, V> h, K key, V value) {
        if (h == null) {
            assignments++;
            return new NodeRB<>(new TreeNode<>(key, value));
        }

        comparisons++;
        int cmp = key.compareTo(h.getKey());

        if (cmp < 0) {
            h.setLeft(insert(h.getLeft(), key, value)); assignments++;
        } else if (cmp > 0) {
            h.setRight(insert(h.getRight(), key, value)); assignments++;
        } else {
            h.setValue(value); assignments++;
        }

        if (isRed(h.getRight()) && !isRed(h.getLeft())) {
            comparisons++;
            h = rotateLeft(h); assignments++;
        }
        if (isRed(h.getLeft()) && isRed(h.getLeft().getLeft())) {
            comparisons++;
            h = rotateRight(h); assignments++;
        }
        if (isRed(h.getLeft()) && isRed(h.getRight())) {
            comparisons++;
            flipColors(h);
        }

        return h;
    }

    @Override
    public V find(K key) {
        NodeRB<K, V> x = root;
        while (x != null) {
            comparisons++;
            int cmp = key.compareTo(x.getKey());

            if (cmp < 0) {
                x = x.getLeft(); assignments++;
            } else if (cmp > 0) {
                x = x.getRight(); assignments++;
            } else {
                return x.getValue();
            }
        }
        return null;
    }

    @Override
    public boolean remove(K key) {
        throw new UnsupportedOperationException("Remove not implemented");
    }

    @Override
    public int getHeight() {
        return height(root);
    }

    private int height(NodeRB<K, V> node) {
        comparisons++;
        if (node == null) return -1;
        return 1 + Math.max(height(node.getLeft()), height(node.getRight()));
    }

    @Override
    public void printInOrder() {
        printInOrder(root);
    }

    private void printInOrder(NodeRB<K, V> node) {
        if (node != null) {
            printInOrder(node.getLeft());
            System.out.println(node.getKey() + " -> " + node.getValue());
            printInOrder(node.getRight());
        }
    }

    public List<V> inOrder() {
        List<V> result = new ArrayList<>();
        inOrder(root, result);
        return result;
    }

    private void inOrder(NodeRB<K, V> node, List<V> result) {
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
}
