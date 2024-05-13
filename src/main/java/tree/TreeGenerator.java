package graph;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class TreeGenerator {

//    public static void generateTree(int numberOfVertices, String fileName) {
//        Random random = new Random(); // Pentru generarea aleatoare a muchiilor
//        List<List<Integer>> adjacencyList = new ArrayList<>(numberOfVertices);
//
//        for (int i = 0; i < numberOfVertices; i++) {
//            adjacencyList.add(new ArrayList<>());
//        }
//
//        try (FileWriter writer = new FileWriter(fileName)) {
//            // Scrie numărul de noduri și de muchii (mereu N-1 pentru un arbore) la începutul fișierului
//            writer.write(String.format("%d %d\n", numberOfVertices, numberOfVertices - 1));
//
//            // Creăm o listă de noduri pentru a alege aleator muchiile fără a genera cicluri
//            List<Integer> vertices = new ArrayList<>();
//            for (int i = 0; i < numberOfVertices; i++) {
//                vertices.add(i);
//            }
//            // Amestecăm lista pentru a avea o ordine aleatoare
//            Collections.shuffle(vertices, random);
//
//            // Generăm muchiile arborelui conectând nodurile în mod secvențial
//            for (int i = 1; i < numberOfVertices; i++) {
//                // Scriem muchia între nodul curent și un nod ales aleator din cele anterioare
//                int vertex1 = vertices.get(i);
//                int vertex2 = vertices.get(random.nextInt(i)); // Alege un nod dintre 0 și i-1 (inclusiv)
//                writer.write(String.format("%d %d\n", vertex1, vertex2));
//                // Adăugăm muchia în lista de adiacență
//                adjacencyList.get(vertex1).add(vertex2);
//                adjacencyList.get(vertex2).add(vertex1);
//            }
//
//            // Verifică dacă arborele este conex
//            if (!isConnected(adjacencyList, numberOfVertices)) {
//                System.out.println("Arborele nu este conex, reîncearcă generarea.");
//            }
//        } catch (IOException e) {
//            System.err.println("An error occurred while writing to the file: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    private static boolean isConnected(List<List<Integer>> adjacencyList, int numVertices) {
//        boolean[] visited = new boolean[numVertices];
//        Queue<Integer> queue = new LinkedList<>();
//        queue.add(0); // începem de la nodul 0
//        visited[0] = true;
//
//        while (!queue.isEmpty()) {
//            int node = queue.poll();
//            for (int neighbor : adjacencyList.get(node)) {
//                if (!visited[neighbor]) {
//                    visited[neighbor] = true;
//                    queue.add(neighbor);
//                }
//            }
//        }
//
//        for (boolean v : visited) {
//            if (!v) {
//                return false; // dacă un nod nu a fost vizitat, arborele nu este conex
//            }
//        }
//        return true;
//    }
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
