package Services;

import Models.Transaction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatasetReader {

    private final List<Transaction> transactions;

    public DatasetReader() {
        this.transactions = new ArrayList<>();
    }

    public void readFromCSV(String filePath) {
        transactions.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean header = true;

            while ((line = br.readLine()) != null) {
                if (header) {
                    header = false;
                    continue;
                }

                String[] parts = line.split(";");
                if (parts.length != 5) continue;

                String id = parts[0];
                String origem = parts[1];
                String destino = parts[2];
                double valor = Double.parseDouble(parts[3].replace(",", ".")); // se vier com vírgula
                String timestamp = parts[4];

                transactions.add(new Transaction(id, origem, destino, timestamp, valor));
            }

            System.out.println("Arquivo lido com sucesso. Total de transações: " + transactions.size());

        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        }
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }
}
