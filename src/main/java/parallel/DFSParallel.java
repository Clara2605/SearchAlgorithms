//package parallel;
//
//import graph.Graph;
//import graph.Node;
//
//import java.util.concurrent.*;
//
//public class DFSParallel implements Runnable {
//    private final Graph graph;
//    private final Node startNode;
//    private static final ConcurrentLinkedDeque<Node> stack = new ConcurrentLinkedDeque<>();
//    private static final ConcurrentHashMap<Node, Boolean> visited = new ConcurrentHashMap<>();
//
//    public DFSParallel(Graph graph, Node startNode) {
//        this.graph = graph;
//        this.startNode = startNode;
//    }
//
//    @Override
//    public void run() {
//        stack.push(startNode);
//        visited.put(startNode, true);
//
//        while (!stack.isEmpty()) {
//            Node currentNode = stack.pop();
//            // Processing the current node, eg display
//            graph.getNeighbors(currentNode).forEach(neighbor -> {
//                if (visited.putIfAbsent(neighbor, true) == null) {
//                    stack.push(neighbor);
//                }
//            });
//        }
//    }
//
//    // Method to start parallel graphDFS
//    public static void startParallelDFS(Graph graph, Node startNode) throws InterruptedException {
//        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
//        executor.submit(new DFSParallel(graph, startNode));
//        executor.shutdown();
//        executor.awaitTermination(1, TimeUnit.HOURS); // Wait until all tasks are finished
//    }
//}
package parallel;

import graph.Graph;
import graph.MyLogger;
import graph.Node;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Level;

public class DFSParallel implements Callable<Set<Node>> {
    private final Graph graph;
    private final Node startNode;
    private final ConcurrentLinkedDeque<Node> stack = new ConcurrentLinkedDeque<>();
    private final ConcurrentHashMap<Node, Boolean> visited = new ConcurrentHashMap<>();

    public DFSParallel(Graph graph, Node startNode) {
        this.graph = graph;
        this.startNode = startNode;
    }

    @Override
    public Set<Node> call() {
        try {
            stack.push(startNode);
            visited.put(startNode, true);

            while (!stack.isEmpty()) {
                Node currentNode = stack.pop();
                // Assuming graph.getNeighbors(node) returns a Set<Node>
                graph.getNeighbors(currentNode).forEach(neighbor -> {
                    if (visited.putIfAbsent(neighbor, true) == null) {
                        stack.push(neighbor);
                    }
                });
            }
        } catch (Exception e) {
            MyLogger.log(Level.SEVERE, "An error occurred during graphDFS: " + e.getMessage());
            // Optionally rethrow or handle the exception
        }
        return visited.keySet();
    }
}
