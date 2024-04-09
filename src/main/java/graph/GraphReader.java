package graph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class GraphReader {
    // Metoda pentru citirea unui graf sau arbore dintr-un fișier
    // Adaugă un parametru isTree pentru a specifica dacă structura este un arbore
    public static ArrayList<ArrayList<Integer>> readGraph(String fileName, boolean isTree) throws IOException {
        ArrayList<ArrayList<Integer>> adjacencyList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line = br.readLine();
            String[] parts = line.split(" ");
            int numberOfVertices = Integer.parseInt(parts[0]);

            // Inițializează lista de adiacență
            for (int i = 0; i < numberOfVertices; i++) {
                adjacencyList.add(new ArrayList<>());
            }

            while ((line = br.readLine()) != null) {
                parts = line.split(" ");
                int u = Integer.parseInt(parts[0]);
                int v = Integer.parseInt(parts[1]);
                adjacencyList.get(u).add(v);
                if (!isTree) {
                    // Pentru grafuri (neorientate), adaugă muchia în ambele direcții
                    adjacencyList.get(v).add(u);
                }
                // Notă: Pentru arbori, presupunem că fișierul oferă muchiile în direcția corectă
                // și nu este nevoie să adaugăm muchia inversă
            }
        }
        return adjacencyList;
    }
}
