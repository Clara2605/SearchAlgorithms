package graph;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;

public class GraphGenerator {

    // Method for generating the graph and saving it to a file
    public static void generateGraph(int numberOfVertices, int numberOfEdges, String fileName) {
        if (numberOfEdges > (long) numberOfVertices * (numberOfVertices - 1) / 2) {
            MyLogger.log(Level.SEVERE, "The number of edges is too large for the given number of nodes.");
            return;
        }
        Random random = new Random(); // For randomly generating the edges
        try (FileWriter writer = new FileWriter(fileName)) {
            // Write the number of nodes and edges at the beginning of the file
            writer.write(String.format("%d %d\n", numberOfVertices, numberOfEdges));

            for (int i = 0; i < numberOfEdges; i++) {
                int vertex1 = random.nextInt(numberOfVertices);
                int vertex2 = random.nextInt(numberOfVertices);
                // Ensure we do not generate a loop (an edge that connects a node to itself)
                while (vertex1 == vertex2) {
                    vertex2 = random.nextInt(numberOfVertices);
                }
                // Write the edge to the file
                writer.write(String.format("%d %d\n", vertex1, vertex2));
            }
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }
    }
}
