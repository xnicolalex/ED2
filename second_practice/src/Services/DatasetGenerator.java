package Services;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DatasetGenerator {
    private final List<Map<String, String>> transacoes;
    private final List<String> origensFrequentes;
    private final List<String> origensRaras;
    private final Random random;
    private final SimpleDateFormat sdf;

    public DatasetGenerator() {
        this.transacoes = new ArrayList<>();
        this.origensFrequentes = new ArrayList<>();
        this.origensRaras = new ArrayList<>();
        this.random = new Random();
        this.sdf = new SimpleDateFormat("yyyy-MM-dd");

        // Inicializa origens frequentes (20%)
        for (int i = 0; i < 20; i++) {
            origensFrequentes.add("ORIG" + String.format("%03d", i));
        }

        // Inicializa origens raras (80%)
        for (int i = 20; i < 100; i++) {
            origensRaras.add("ORIG" + String.format("%03d", i));
        }
    }

    public void gerarTransacoes(int quantidade) {
        transacoes.clear();

        for (int i = 0; i < quantidade; i++) {
            Map<String, String> transacao = new HashMap<>();

            // ID com 10% de chance de repetir
            String id = (i > 0 && random.nextDouble() < 0.1)
                    ? transacoes.get(random.nextInt(i)).get("id")
                    : "ID" + String.format("%05d", i);

            // Origem com 80% de chance de ser frequente
            String origem = (random.nextDouble() < 0.8)
                    ? origensFrequentes.get(random.nextInt(origensFrequentes.size()))
                    : origensRaras.get(random.nextInt(origensRaras.size()));

            // Timestamp aleatório entre 2020 e 2024
            Calendar cal = Calendar.getInstance();
            cal.set(2020 + random.nextInt(5), random.nextInt(12), random.nextInt(28) + 1);
            String timestamp = sdf.format(cal.getTime());

            // Destino e valor
            String destino = "DEST" + String.format("%03d", random.nextInt(1000));
            String valor = String.format("%.2f", 10 + random.nextDouble() * 990);

            // Popula a transação
            transacao.put("id", id);
            transacao.put("origem", origem);
            transacao.put("destino", destino);
            transacao.put("timestamp", timestamp);
            transacao.put("valor", valor);

            transacoes.add(transacao);
        }
    }

    public void salvarCSV(String caminhoArquivo) {
        try (FileWriter writer = new FileWriter(caminhoArquivo)) {
            writer.write("id;origem;destino;valor;timestamp\n");
            for (Map<String, String> t : transacoes) {
                writer.write(String.join(";",
                        t.get("id"),
                        t.get("origem"),
                        t.get("destino"),
                        t.get("valor"),
                        t.get("timestamp")) + "\n");
            }
            System.out.println("Arquivo salvo em: " + caminhoArquivo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Map<String, String>> getTransacoes() {
        return transacoes;
    }
}
