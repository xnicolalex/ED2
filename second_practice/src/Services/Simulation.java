package Services;

import Models.HybridHash;
import Models.Transaction;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Simulation {

    public static SimulationResult runSimulation(List<Transaction> transactions) {
        HybridHash hash = new HybridHash(512);

        long startTime = System.nanoTime();
        for (Transaction t : transactions) {
            hash.insert(t);
        }
        long endTime = System.nanoTime();

        long durationMillis = (endTime - startTime) / 1_000_000;

        return new SimulationResult(
                hash.getComparisons(),
                hash.getAssignments(),
                durationMillis
        );
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