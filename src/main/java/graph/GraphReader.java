package graph;

import java.io.*;
import java.util.ArrayList;

public class GraphReader {

    // Reading the graph from a file
    public static ArrayList<ArrayList<Integer>> readGraph(String fileName) throws IOException {
        File file = new File(fileName);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = br.readLine();
        String[] parts = line.split(" ");

        int numberOfVertices = Integer.parseInt(parts[0]); // Number of nodes
        ArrayList<ArrayList<Integer>> adjacencyList = new ArrayList<>(numberOfVertices);
        for (int i = 0; i < numberOfVertices; i++) {
            adjacencyList.add(new ArrayList<>());
        }

        while ((line = br.readLine()) != null) {
            parts = line.split(" ");
            int u = Integer.parseInt(parts[0]);
            int v = Integer.parseInt(parts[1]);
            adjacencyList.get(u).add(v);
            adjacencyList.get(v).add(u); // Add the edge v-u for an undirected graph
        }

        br.close();
        return adjacencyList;
    }
}
