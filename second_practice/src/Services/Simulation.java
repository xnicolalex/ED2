package Services;

import Models.HybridHash;
import Models.Transaction;

import java.util.List;

public class Simulation {

    public static SimulationResult runSimulation(List<Transaction> transactions) {
        System.out.printf("Iniciando simulação com %d transações...%n", transactions.size());

        HybridHash hash = new HybridHash(512);

        int insertions = 1;

        long startTime = System.nanoTime();
        try {
            for (Transaction t : transactions) {
                hash.insert(t);
                // System.out.printf("Inserindo transação de número %d.%n", insertions++);
            }
        } catch (Exception e) {
            System.err.println("[EXCEÇÃO DETECTADA] " + e.getMessage());
            e.printStackTrace();
        }
        long endTime = System.nanoTime();

        long durationMillis = (endTime - startTime) / 1_000_000;

        int comparisons = hash.getComparisons();
        int assignments = hash.getAssignments();

        System.out.printf("Finalizado em %d ms | Comparações: %d | Atribuições: %d%n",
                durationMillis, comparisons, assignments);

        return new SimulationResult(comparisons, assignments, durationMillis);
    }

    public static class SimulationResult {
        public final int comparisons;
        public final int assignments;
        public final long timeMillis;

        public SimulationResult(int comparisons, int assignments, long timeMillis) {
            this.comparisons = comparisons;
            this.assignments = assignments;
            this.timeMillis = timeMillis;
        }
    }
}
