package graph;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

public class TreeGenerator {

    // Metodă pentru generarea arborelui și salvarea acestuia într-un fișier
    public static void generateTree(int numberOfVertices, String fileName) {
        Random random = new Random(); // Pentru generarea aleatoare a muchiilor

        try (FileWriter writer = new FileWriter(fileName)) {
            // Scrie numărul de noduri și de muchii (mereu N-1 pentru un arbore) la începutul fișierului
            writer.write(String.format("%d %d\n", numberOfVertices, numberOfVertices - 1));

            // Creăm o listă de noduri pentru a alege aleator muchiile fără a genera cicluri
            List<Integer> vertices = new ArrayList<>();
            for (int i = 0; i < numberOfVertices; i++) {
                vertices.add(i);
            }
            // Amestecăm lista pentru a avea o ordine aleatoare
            Collections.shuffle(vertices, random);

            // Generăm muchiile arborelui conectând nodurile în mod secvențial
            for (int i = 1; i < numberOfVertices; i++) {
                // Scriem muchia între nodul curent și un nod ales aleator din cele anterioare
                int vertex1 = vertices.get(i);
                int vertex2 = vertices.get(random.nextInt(i)); // Alege un nod dintre 0 și i-1 (inclusiv)
                writer.write(String.format("%d %d\n", vertex1, vertex2));
            }
        } catch (IOException e) {
            MyLogger.log(Level.SEVERE, "An error occurred while writing to the file.");
            e.printStackTrace();
        }
    }
}
