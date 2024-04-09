package graph;

import java.util.*;
import java.util.logging.Level;

public class Graph {
    private List<List<Integer>> adjacencyList;
    private boolean isDirected;
    private boolean isTree;

    public Graph(int numberOfVertices, boolean isDirected, boolean isTree) {
        this.adjacencyList = new ArrayList<>(numberOfVertices);
        for (int i = 0; i < numberOfVertices; i++) {
            this.adjacencyList.add(new ArrayList<>());
        }
        this.isDirected = isDirected;
        this.isTree = isTree;
    }

    public void addEdge(int from, int to) {
        this.adjacencyList.get(from).add(to);
        if (!this.isDirected && !isTree) { // Pentru arbori, muchiile sunt adăugate doar într-o singură direcție
            this.adjacencyList.get(to).add(from);
        }
    }

    // Returns the neighbors of a node
    public Set<Node> getNeighbors(Node node) {
        try {
            List<Integer> neighbors = this.adjacencyList.get(node.getId());
            if (neighbors == null) {
                return Collections.emptySet();
            }
            return new HashSet<>(neighbors); // Return a copy to avoid accidental modifications
        } catch (Exception e) {
            MyLogger.log(Level.SEVERE, "Failed to get neighbors for " + node + ": " + e.getMessage());
            return Collections.emptySet();
        }
    }

}
