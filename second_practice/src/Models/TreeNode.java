package Models;

public class TreeNode<K extends Comparable<K>, V> {
    private K key;
    private V value;

    public TreeNode(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() { return key; }
    public V getValue() { return value; }
    public void setValue(V value) { this.value = value; }

    @Override
    public String toString() {
        return "(" + key + " -> " + value + ")";
    }
}
