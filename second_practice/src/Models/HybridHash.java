package Models;

import java.util.*;

public class HybridHash {

    private static class Entry {
        Object originStructure = null;
        int idCollisions = 0;
        int idInsertions = 0;
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
            entry.idInsertions = 1;
            assignments += 3;
        } else if (entry.originStructure instanceof LinkedList<?>) {
            @SuppressWarnings("unchecked")
            LinkedList<Transaction> list = (LinkedList<Transaction>) entry.originStructure;
            list.add(t);
            entry.idInsertions++;
            assignments += 2;

            if (entry.idInsertions > 3) {
                System.out.printf("[INFO] Converting LinkedList at index %d to AVLTree (by ID)%n", idx);
                AVLTree<String, Transaction> avl = new AVLTree<>();
                entry.idInsertions = 0;
                for (Transaction tx : list) {
                    avl.insert(tx.getId(), tx);
                    comparisons++;
                    entry.idInsertions++;
                }
                entry.originStructure = avl;
                assignments += 2;
            }
        } else if (entry.originStructure instanceof AVLTree<?, ?> tree) {
            @SuppressWarnings("unchecked")
            AVLTree<String, Transaction> avl = (AVLTree<String, Transaction>) tree;
            avl.insert(t.getId(), t);
            comparisons++;
            entry.idInsertions++;
            assignments++;

            if (entry.idInsertions > 10) {
                System.out.printf("[INFO] Converting AVLTree at index %d to RBTree (by ID)%n", idx);
                RBTree<String, Transaction> rb = new RBTree<>();
                entry.idInsertions = 0;
                for (Transaction tx : avl.inOrder()) {
                    rb.insert(tx.getId(), tx);
                    comparisons++;
                    entry.idInsertions++;
                }
                entry.originStructure = rb;
                assignments += 2;
            }
        } else if (entry.originStructure instanceof RBTree<?, ?> tree) {
            @SuppressWarnings("unchecked")
            RBTree<String, Transaction> rb = (RBTree<String, Transaction>) tree;
            rb.insert(t.getId(), t);
            entry.idInsertions++;
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
                entry.originInsertions++;
                assignments += 3;
                break;
            }

            if (attempts < 4) {
                attempts++;
                continue;
            }

            entry = table[first_origin];

            if (entry.originStructure instanceof Transaction) {
                System.out.printf("[INFO] Converting Transaction at index %d to AVLTree (by Origin)%n", idxOrigin);
                comparisons++;
                AVLTree<String, Transaction> avlTree = new AVLTree<>();
                avlTree.insert( ((Transaction) entry.originStructure).getOrigin(), (Transaction) entry.originStructure );
                avlTree.insert(t.getOrigin(), t);
                entry.originStructure = avlTree;
                entry.originInsertions += 2;
                assignments += 3;
                break;
            } else if (entry.originStructure instanceof LinkedList<?> rawList &&
                    !rawList.isEmpty() &&
                    rawList.getFirst() instanceof Transaction) {

                @SuppressWarnings("unchecked")
                LinkedList<Transaction> castList = (LinkedList<Transaction>) rawList;
                castList.add(t);
                entry.originInsertions++;
                entry.originCollisions++;
                assignments += 2;
                comparisons++;

                if (entry.idInsertions + entry.originInsertions > 3) {
                    System.out.printf("[INFO] Converting LinkedList at index %d to AVLTree (by Origin)%n", idxOrigin);
                    AVLTree<String, Transaction> avlTree = new AVLTree<>();
                    for (Transaction txx : castList) {
                        avlTree.insert(txx.getOrigin(), txx);
                        comparisons++;
                    }
                    entry.originStructure = avlTree;
                    assignments += 2;
                }
                break;
            } else if (entry.originStructure instanceof AVLTree<?, ?> tree) {
                @SuppressWarnings("unchecked")
                AVLTree<String, Transaction> avl = (AVLTree<String, Transaction>) tree;
                avl.insert(t.getOrigin(), t);
                comparisons++;
                entry.originInsertions++;
                assignments++;

                if (entry.originInsertions + entry.idInsertions > 10) {
                    System.out.printf("[INFO] Converting AVLTree at index %d to RBTree (by Origin)%n", idxOrigin);
                    RBTree<String, Transaction> rb = new RBTree<>();
                    for (Transaction txx : avl.inOrder()) {
                        rb.insert(txx.getOrigin(), txx);
                        comparisons++;
                    }
                    entry.originStructure = rb;
                    assignments += 2;
                }
                break;

            } else if (entry.originStructure instanceof RBTree<?, ?> tree) {
                @SuppressWarnings("unchecked")
                RBTree<String, Transaction> rb = (RBTree<String, Transaction>) tree;
                rb.insert(t.getOrigin(), t);
                comparisons++;
                entry.originInsertions++;
                break;
            }
        }
    }

    public void printTable() {
        for (int i = 0; i < this.size; i++) {
            Entry e = table[i];
            if (e.originStructure != null) {
                System.out.print("[" + i + "] STRUCT: ");
                if (e.originStructure instanceof Transaction t) {
                    System.out.println("[Transaction]");
                    System.out.println(t);
                } else if (e.originStructure instanceof List<?> list) {
                    System.out.println("[Linked List]:");
                    for ( Object t : list ){
                        System.out.println( t );
                    }
                } else if (e.originStructure instanceof AVLTree<?, ?> tree) {
                    System.out.println("[AVLTree]");
                    System.out.println( tree );
                } else if (e.originStructure instanceof RBTree<?, ?> tree) {
                    System.out.println("[RBTree]");
                    System.out.println(tree);
                }
                System.out.println();
            }
        }
    }

    public ArrayList<Transaction> searchByTimestamp(String startTimestamp, String endTimestamp) {
        ArrayList<Transaction> result = new ArrayList<>();

        // Traverse the table and search in each structure
        for (int i = 0; i < this.size; i++) {
            Entry entry = table[i];

            if (entry.originStructure != null) {
                if (entry.originStructure instanceof LinkedList<?>) {
                    for (Object o : (LinkedList<?>) entry.originStructure) {
                        comparisons++;
                        if (o instanceof Transaction t) {
                            if (isWithinTimestampRange(t.getTimestamp(), startTimestamp, endTimestamp)) {
                                result.add(t);
                            }
                        }
                    }
                } else if (entry.originStructure instanceof AVLTree<?, ?> tree) {
                    @SuppressWarnings("unchecked")
                    AVLTree<String, Transaction> avl = (AVLTree<String, Transaction>) tree;
                    result.addAll(avl.searchByTimestamp(startTimestamp, endTimestamp)); // Use the AVL method to find transactions
                } else if (entry.originStructure instanceof RBTree<?, ?> tree) {
                    @SuppressWarnings("unchecked")
                    RBTree<String, Transaction> rb = (RBTree<String, Transaction>) tree;
                    result.addAll(rb.searchByTimestamp(startTimestamp, endTimestamp)); // Use the RBTree method to find transactions
                }
            }
        }

        return result;
    }

    // Helper method to check if the transaction's timestamp is within the given range
    private boolean isWithinTimestampRange(String transactionTimestamp, String startTimestamp, String endTimestamp) {
        comparisons += 3;
        return transactionTimestamp.compareTo(startTimestamp) >= 0 && transactionTimestamp.compareTo(endTimestamp) <= 0;
    }

    public int getComparisons() { return comparisons; }

    public int getAssignments() { return assignments; }

    public void resetCounters() {
        comparisons = 0;
        assignments = 0;
    }
}
