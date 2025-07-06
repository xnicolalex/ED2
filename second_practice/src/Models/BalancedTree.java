package Models;

public interface BalancedTree<K extends Comparable<K>, V> {
    void insert(K key, V value);
    boolean remove(K key);
    V find(K key);
    int getHeight();
    void printInOrder();
}
