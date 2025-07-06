package Models;

public class Transaction implements Comparable<Transaction> {
    private String id;
    private String origem;
    private String destino;
    private String timestamp;
    private double valor;

    public Transaction(String id, String origem, String destino, String timestamp, double valor) {
        this.id = id;
        this.origem = origem;
        this.destino = destino;
        this.timestamp = timestamp;
        this.valor = valor;
    }

    public String getId() {
        return id;
    }

    public String getOrigem() {
        return origem;
    }

    public String getDestino() {
        return destino;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public double getValor() {
        return valor;
    }

    @Override
    public String toString() {
        return String.format("Transaction{id='%s', origem='%s', destino='%s', valor=%.2f, timestamp='%s'}",
                id, origem, destino, valor, timestamp);
    }

    @Override
    public int compareTo(Transaction other) {
        return this.timestamp.compareTo(other.timestamp);
    }
}
