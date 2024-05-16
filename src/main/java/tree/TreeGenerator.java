package tree;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class TreeGenerator {
    public static void generateTree(int numberOfVertices, String fileName) {
        Random random = new Random();
        List<List<Integer>> adjacencyList = new ArrayList<>(numberOfVertices);
        for (int i = 0; i < numberOfVertices; i++) {
            adjacencyList.add(new ArrayList<>());
        }

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(String.format("%d %d\n", numberOfVertices, numberOfVertices - 1));

            List<Integer> connectedVertices = new ArrayList<>();
            List<Integer> remainingVertices = new ArrayList<>();
            for (int i = 0; i < numberOfVertices; i++) {
                remainingVertices.add(i);
            }
            // Alegem primul nod aleator și îl adăugăm la conectate
            int first = remainingVertices.remove(random.nextInt(remainingVertices.size()));
            connectedVertices.add(first);

            // Generăm muchii până când toate nodurile sunt conectate
            while (!remainingVertices.isEmpty()) {
                // Alegem un nod din cele deja conectate
                int from = connectedVertices.get(random.nextInt(connectedVertices.size()));
                // Alegem un nod din cele rămase
                int to = remainingVertices.remove(random.nextInt(remainingVertices.size()));

                // Adăugăm muchia la arbore și actualizăm lista de adiacență
                adjacencyList.get(from).add(to);
                adjacencyList.get(to).add(from);
                writer.write(String.format("%d %d\n", from, to));

                // Adăugăm noul nod la lista de conectate
                connectedVertices.add(to);
            }
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
