package Models;

public class Transaction implements Comparable<Transaction> {
    private String id;
    private String origin;
    private String destination;
    private String timestamp;
    private double amount;

    public Transaction(String id, String origin, String destination, String timestamp, double amount) {
        this.id = id;
        this.origin = origin;
        this.destination = destination;
        this.timestamp = timestamp;
        this.amount = amount;
    }

    public String getId() { return id; }

    public String getOrigin() { return origin; }

    public String getDestination() { return destination; }

    public String getTimestamp() { return timestamp; }

    public double getAmount() { return amount; }

    @Override
    public String toString() {
        return String.format(
                "Transaction{id='%s', origin='%s', destination='%s', amount=%.2f, timestamp='%s'}",
                id, origin, destination, amount, timestamp
        );
    }

    @Override
    public int compareTo(Transaction other) {
        return this.timestamp.compareTo(other.timestamp);
    }
}
