import Models.*;

public class Main {
    public static void main(String[] args) {
        // Create a new HybridHash with a size of 10
        HybridHash hybridHash = new HybridHash(10);

        // Create some sample Transaction objects
        Transaction t1 = new Transaction("1", "A", "B", "2025-07-01T10:00:00", 100.0);
        Transaction t2 = new Transaction("2", "A", "C", "2025-07-01T12:00:00", 200.0);
        Transaction t3 = new Transaction("3", "B", "C", "2025-07-01T14:00:00", 300.0);
        Transaction t4 = new Transaction("4", "A", "D", "2025-07-02T10:00:00", 150.0);

        // Insert transactions into the HybridHash
        hybridHash.insert(t1);
        hybridHash.insert(t2);
        hybridHash.insert(t3);
        hybridHash.insert(t4);

        hybridHash.printTable();

        System.out.println();

        // Search transactions by ID
        System.out.println("Search by ID (1): " + hybridHash.searchById("1"));
        System.out.println("Search by ID (2): " + hybridHash.searchById("2"));

        // Search for transactions within a timestamp range
        String startTimestamp = "2025-07-01T09:00:00";
        String endTimestamp = "2025-07-01T13:00:00";
        System.out.println("Transactions within timestamp range (" + startTimestamp + " to " + endTimestamp + "):");
        for (Transaction transaction : hybridHash.searchByTimestamp(startTimestamp, endTimestamp)) {
            System.out.println(transaction);
        }
    }
}
