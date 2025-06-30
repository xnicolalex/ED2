package Services;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DatasetGenerator{
    public static void main(String[] args) {
        List<Map<String, String>> transacoes = new ArrayList<>();
        Random random = new Random();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // 1. Gerar 20% de origens que aparecerão em 80% das transações (Padrão 80/20)
        List<String> origensFrequentes = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            origensFrequentes.add("ORIG" + String.format("%03d", i));
        }

        // 2. Gerar 80% de origens menos frequentes
        List<String> origensRaras = new ArrayList<>();
        for (int i = 20; i < 100; i++) {
            origensRaras.add("ORIG" + String.format("%03d", i));
        }

        // 3. Gerar transações (10.000 registros)
        for (int i = 0; i < 10000; i++) {
            Map<String, String> transacao = new HashMap<>();

            // ID (10% de chance de repetir IDs anteriores)
            String id;
            if (i > 0 && random.nextDouble() < 0.1) {
                id = transacoes.get(random.nextInt(i)).get("id"); // Reusa ID existente
            } else {
                id = "ID" + String.format("%05d", i);
            }

            // Origem (80% chance de usar uma origem frequente)
            String origem;
            if (random.nextDouble() < 0.8) {
                origem = origensFrequentes.get(random.nextInt(origensFrequentes.size()));
            } else {
                origem = origensRaras.get(random.nextInt(origensRaras.size()));
            }

            // Timestamp (aleatório entre 2020-2024, não ordenado)
            Calendar cal = Calendar.getInstance();
            cal.set(2020 + random.nextInt(5), random.nextInt(12), random.nextInt(28) + 1);
            String timestamp = sdf.format(cal.getTime());

            // Destino (aleatório)
            String destino = "DEST" + String.format("%03d", random.nextInt(1000));

            // Valor (aleatório entre 10.00 e 1000.00)
            String valor = String.format("%.2f", 10 + random.nextDouble() * 990);

            // Monta a transação
            transacao.put("id", id);
            transacao.put("origem", origem);
            transacao.put("destino", destino);
            transacao.put("timestamp", timestamp);
            transacao.put("valor", valor);

            System.out.println(valor);
            transacoes.add(transacao);
        }

        // 4. Salva em CSV (opcional: JSON ou outro formato)
        try (FileWriter writer = new FileWriter("transacoes.csv")) {
            writer.write("id;origem;destino;valor;timestamp\n");
            for (Map<String, String> t : transacoes) {
                writer.write(String.join(";",
                        t.get("id"),
                        t.get("origem"),
                        t.get("destino"),
                        t.get("valor"),
                        t.get("timestamp")) + "\n");
            }
            System.out.println("Dataset gerado em transacoes.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
