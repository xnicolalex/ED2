package Models;

import java.util.*;

public class HybridHash {

    private static class Entry {
        Object originStructure = null;
        int originCollisions = 0;
        int originInsertions = 0;
    }

    private int size;
    private Entry[] table;

    private int comparisons = 0;
    private int assignments = 0;

    public HybridHash(int size) {
        this.size = size;
        table = new Entry[this.size];
        for (int i = 0; i < this.size; i++) {
            table[i] = new Entry();
            assignments++;
        }
    }

    private int hashId(String id) {
        comparisons++;
        return (id.hashCode() & 0x7fffffff) % this.size;
    }

    private int hashOrigin(String origin, int attempt) {
        comparisons++;
        int h = (origin.hashCode() & 0x7fffffff) % this.size;
        return (h + attempt * attempt) % this.size;
    }

    public List<Transaction> searchById(String id) {
        int idx = hashId(id);
        Entry entry = table[idx];
        List<Transaction> result = new ArrayList<>();

        if (entry.originStructure instanceof LinkedList<?>) {
            for (Object o : (LinkedList<?>) entry.originStructure) {
                comparisons++;
                if (o instanceof Transaction t && t.getId().equals(id)) {
                    result.add(t);
                }
            }
        } else if (entry.originStructure instanceof AVLTree<?, ?> tree) {
            @SuppressWarnings("unchecked")
            AVLTree<String, Transaction> avl = (AVLTree<String, Transaction>) tree;
            Transaction found = avl.find(id);
            comparisons++;
            if (found != null) result.add(found);
        } else if (entry.originStructure instanceof RBTree<?, ?> tree) {
            @SuppressWarnings("unchecked")
            RBTree<String, Transaction> rb = (RBTree<String, Transaction>) tree;
            Transaction found = rb.find(id);
            comparisons++;
            if (found != null) result.add(found);
        }

        return result;
    }

    public void insert(Transaction t) {
        insertById(t);
        insertByOrigin(t);
    }

    private void insertById(Transaction t) {
        int idx = hashId(t.getId());
        Entry entry = table[idx];

        if (entry.originStructure == null) {
            LinkedList<Transaction> list = new LinkedList<>();
            list.add(t);
            entry.originStructure = list;
            entry.originInsertions = 1;
            assignments += 3;
        } else if (entry.originStructure instanceof LinkedList<?>) {
            @SuppressWarnings("unchecked")
            LinkedList<Transaction> list = (LinkedList<Transaction>) entry.originStructure;
            list.add(t);
            entry.originInsertions++;
            assignments += 2;

            if (entry.originInsertions > 3) {
                System.out.printf("[INFO] Converting LinkedList at index %d to AVLTree (by ID)%n", idx);
                AVLTree<String, Transaction> avl = new AVLTree<>();
                for (Transaction tx : list) {
                    avl.insert(tx.getId(), tx);
                    comparisons++;
                }
                entry.originStructure = avl;
                entry.originInsertions = 0;
                assignments += 2;
            }
        } else if (entry.originStructure instanceof AVLTree<?, ?> tree) {
            @SuppressWarnings("unchecked")
            AVLTree<String, Transaction> avl = (AVLTree<String, Transaction>) tree;
            avl.insert(t.getId(), t);
            comparisons++;
            entry.originInsertions++;
            assignments++;

            if (entry.originInsertions > 10) {
                System.out.printf("[INFO] Converting AVLTree at index %d to RBTree (by ID)%n", idx);
                RBTree<String, Transaction> rb = new RBTree<>();
                for (Transaction tx : avl.inOrder()) {
                    rb.insert(tx.getId(), tx);
                    comparisons++;
                }
                entry.originStructure = rb;
                entry.originInsertions = 0;
                assignments += 2;
            }
        } else if (entry.originStructure instanceof RBTree<?, ?> tree) {
            @SuppressWarnings("unchecked")
            RBTree<String, Transaction> rb = (RBTree<String, Transaction>) tree;
            rb.insert(t.getId(), t);
            comparisons++;
        }
    }

    private void insertByOrigin(Transaction t) {
        int attempts = 0;
        int first_origin = 0;
        int idxOrigin;

        while (true) {
            idxOrigin = this.hashOrigin(t.getOrigin(), attempts);
            if (attempts == 0) {
                first_origin = idxOrigin;
                assignments++;
            }

            Entry entry = table[idxOrigin];

            if (entry.originStructure == null) {
                entry.originStructure = t;
                entry.originCollisions = 0;
                entry.originInsertions = 1;
                assignments += 3;
                break;
            }

            if (attempts < 4) {
                attempts++;
                continue;
            }

            entry = table[first_origin];

            if (entry.originStructure instanceof Transaction otherT &&
                    otherT.getOrigin().equals(t.getOrigin())) {
                comparisons++;
                AVLTree<String, Transaction> avlTree = new AVLTree<>();
                avlTree.insert(otherT.getOrigin(), otherT);
                avlTree.insert(t.getOrigin(), t);
                entry.originStructure = avlTree;
                assignments++;
                break;
            }

            if (entry.originStructure instanceof List<?> list &&
                    !list.isEmpty() &&
                    list.get(0) instanceof Transaction tx &&
                    tx.getOrigin().equals(t.getOrigin())) {

                @SuppressWarnings("unchecked")
                List<Transaction> castList = (List<Transaction>) list;
                castList.add(t);
                entry.originInsertions++;
                entry.originCollisions++;
                assignments += 2;
                comparisons++;

                if (entry.originInsertions > 3) {
                    System.out.printf("[INFO] Converting List at index %d to AVLTree (by Origin)%n", idxOrigin);
                    AVLTree<String, Transaction> avlTree = new AVLTree<>();
                    for (Transaction txx : castList) {
                        avlTree.insert(txx.getOrigin(), txx);
                        comparisons++;
                    }
                    entry.originStructure = avlTree;
                    entry.originInsertions = 0;
                    assignments += 2;
                }
                break;
            }

            if (entry.originStructure instanceof AVLTree<?, ?> tree) {
                @SuppressWarnings("unchecked")
                AVLTree<String, Transaction> avl = (AVLTree<String, Transaction>) tree;
                avl.insert(t.getOrigin(), t);
                comparisons++;
                entry.originInsertions++;
                assignments++;

                if (entry.originInsertions > 10) {
                    System.out.printf("[INFO] Converting AVLTree at index %d to RBTree (by Origin)%n", idxOrigin);
                    RBTree<String, Transaction> rb = new RBTree<>();
                    for (Transaction txx : avl.inOrder()) {
                        rb.insert(txx.getOrigin(), txx);
                        comparisons++;
                    }
                    entry.originStructure = rb;
                    entry.originInsertions = 0;
                    assignments += 2;
                }
                break;
            }

            if (entry.originStructure instanceof RBTree<?, ?> tree) {
                @SuppressWarnings("unchecked")
                RBTree<String, Transaction> rb = (RBTree<String, Transaction>) tree;
                rb.insert(t.getOrigin(), t);
                comparisons++;
                break;
            }
        }
    }

    public void printTable() {
        for (int i = 0; i < this.size; i++) {
            Entry e = table[i];
            if (e.originStructure != null) {
                System.out.print("[" + i + "] STRUCT: ");
                if (e.originStructure instanceof Transaction t)
                    System.out.print(t);
                else if (e.originStructure instanceof List<?> list)
                    System.out.print(list);
                else if (e.originStructure instanceof AVLTree<?, ?>)
                    System.out.print("[AVLTree]");
                else if (e.originStructure instanceof RBTree<?, ?>)
                    System.out.print("[RBTree]");
                System.out.println();
            }
        }
    }

    public int getComparisons() { return comparisons; }

    public int getAssignments() { return assignments; }

    public void resetCounters() {
        comparisons = 0;
        assignments = 0;
    }
}
