package Services;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Statistics {

    private static final int[] RECORD_COUNTS = {10, 100, 1000, 10000, 100000};
    private static final int RUNS_PER_CASE = 10;

    public static void main(String[] args) {
        DatasetGenerator generator = new DatasetGenerator();

        List<List<Long>> times = new ArrayList<>();
        List<List<Integer>> comparisons = new ArrayList<>();
        List<List<Integer>> assignments = new ArrayList<>();

        for (int count : RECORD_COUNTS) {
            List<Long> t = new ArrayList<>();
            List<Integer> c = new ArrayList<>();
            List<Integer> a = new ArrayList<>();

            for (int i = 0; i < RUNS_PER_CASE; i++) {
                generator.gerarTransacoes(count);
                DatasetReader reader = new DatasetReader();
                generator.salvarCSV("temp.csv");
                reader.readFromCSV("temp.csv");

                Simulation.SimulationResult result = Simulation.runSimulation(reader.getTransactions());
                t.add(result.timeMillis);
                c.add(result.comparisons);
                a.add(result.assignments);
            }
            times.add(t);
            comparisons.add(c);
            assignments.add(a);
        }

        exportToCSV(times, "execution_times.csv");
        exportToCSV(comparisons, "comparisons.csv");
        exportToCSV(assignments, "assignments.csv");
    }

    private static void exportToCSV(List<? extends List<? extends Number>> data, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("Runs/Records;10;100;1000;10000\n");
            for (int i = 0; i < RUNS_PER_CASE; i++) {
                StringBuilder sb = new StringBuilder();
                sb.append(i + 1);
                for (List<? extends Number> col : data) {
                    sb.append(";").append(col.get(i));
                }
                writer.write(sb.toString() + "\n");
            }
            System.out.println("CSV gerado: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
