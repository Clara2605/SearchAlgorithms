//package parallel;
//
//import graph.Graph;
//import graph.Node;
//
//import java.util.concurrent.ConcurrentLinkedQueue;
//import java.util.concurrent.ConcurrentHashMap;
//
//public class BFSParallel implements Runnable {
//    private final Graph graph;
//    private final Node startNode;
//    private final ConcurrentLinkedQueue<Node> queue = new ConcurrentLinkedQueue<>();
//    private final ConcurrentHashMap<Node, Boolean> visited = new ConcurrentHashMap<>();
//
//    public BFSParallel(Graph graph, Node startNode) {
//        this.graph = graph;
//        this.startNode = startNode;
//        this.queue.add(startNode);
//        this.visited.put(startNode, true);
//    }
//
//    @Override
//    public void run() {
//        while (!queue.isEmpty()) {
//            Node currentNode = queue.poll();
//            // Simulate node processing
//            graph.getNeighbors(currentNode).forEach(neighbor -> {
//                if (visited.putIfAbsent(neighbor, true) == null) {
//                    queue.add(neighbor);
//                }
//            });
//        }
//    }
//}
package parallel;

import graph.Graph;
import graph.MyLogger;
import graph.Node;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

public class BFSParallel implements Callable<Set<Node>> {
    private final Graph graph;
    private final Node startNode;
    private final ConcurrentLinkedQueue<Node> queue = new ConcurrentLinkedQueue<>();
    private final ConcurrentHashMap<Node, Boolean> visited = new ConcurrentHashMap<>();

    public BFSParallel(Graph graph, Node startNode) {
        this.graph = graph;
        this.startNode = startNode;
        this.queue.add(startNode);
        this.visited.put(startNode, true);
    }

    @Override
    public Set<Node> call() {
        try {
            while (!queue.isEmpty()) {
                Node currentNode = queue.poll();
                // Safe check in case of null, although unlikely with ConcurrentLinkedQueue
                if (currentNode == null) continue;

                try {
                    Set<Node> neighbors = graph.getNeighbors(currentNode);
                    for (Node neighbor : neighbors) {
                        if (visited.putIfAbsent(neighbor, true) == null) {
                            queue.add(neighbor);
                        }
                    }
                } catch (Exception e) {
                    MyLogger.log(Level.SEVERE, "Failed to process neighbors for node " + currentNode.getId() + ": " + e.getMessage());
                    // Depending on the failure's nature, you might choose to continue, break, or throw a custom exception
                }
            }
        } catch (Exception e) {
            MyLogger.log(Level.SEVERE, "An error occurred during graphBFS: " + e.getMessage());
            // Rethrow if you want the caller to handle it or handle/recover internally
        }
        return visited.keySet();
    }
}