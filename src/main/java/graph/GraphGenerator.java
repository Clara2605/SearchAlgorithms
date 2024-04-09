package graph;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class GraphGenerator {
    private Random random = new Random();

    public void generateGraph(String fileName, int numberOfVertices, int numberOfEdges, boolean isTree) throws IOException {
        try (FileWriter writer = new FileWriter(fileName)) {
            // Scrie numărul de vârfuri și muchii/arce (pentru arbori, numărul de muchii este întotdeauna n-1)
            writer.write(numberOfVertices + " " + (isTree ? numberOfVertices - 1 : numberOfEdges) + "\n");

            if (isTree) {
                // Generarea unui arbore - puteți alege o strategie specifică, aici este o abordare simplă liniară
                for (int i = 1; i < numberOfVertices; i++) {
                    // Conectează fiecare nod cu nodul anterior pentru a forma un arbore liniar
                    writer.write((i - 1) + " " + i + "\n");
                }
            } else {
                // Generarea unui graf - muchii alese aleator
                for (int i = 0; i < numberOfEdges; i++) {
                    int u = random.nextInt(numberOfVertices);
                    int v = random.nextInt(numberOfVertices);
                    while (u == v) { // Evită buclele
                        v = random.nextInt(numberOfVertices);
                    }
                    writer.write(u + " " + v + "\n");
                }
            }
        }
    }
}
