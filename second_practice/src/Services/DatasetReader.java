package Services;

import Models.Transaction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatasetReader {

    public static List<Transaction> readFromCSV(String filePath) {
        List<Transaction> transactions = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean header = true;

            while ((line = br.readLine()) != null) {
                if (header) {
                    header = false; // Skip header
                    continue;
                }

                String[] parts = line.split(";");
                if (parts.length != 5) continue;

                String id = parts[0];
                String origem = parts[1];
                String destino = parts[2];
                double valor = Double.parseDouble(parts[3].replace(",", ".")); // Replace comma if needed
                String timestamp = parts[4];

                Transaction t = new Transaction(id, origem, destino, timestamp, valor);
                transactions.add(t);
            }

            System.out.println("Arquivo lido com sucesso. Total de transações: " + transactions.size());

        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        }

        return transactions;
    }

    public static void main(String[] args) {
        List<Transaction> lista = readFromCSV("transacoes.csv");
        lista.stream().limit(10).forEach(System.out::println);
    }
}
