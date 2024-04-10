import graph.Graph;
import graph.GraphReader;
import graph.Node;
import parallel.BFSParallel;
import parallel.DFSParallel;
import sequential.BFSSequential;
import sequential.DFSSequential;
import tree.TreeNode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class GraphAlgorithmExecutor {
    public static void runSequentialBFS(String fileName, int startNodeID, List<Double> bfsTimes, List<Long> memoryUsage) throws IOException {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage beforeMem = memoryBean.getHeapMemoryUsage();
        long beforeUsedMem = beforeMem.getUsed();

        // BFS execution
        ArrayList<ArrayList<Integer>> adjacencyList = GraphReader.readGraph(fileName);
        long startTime = System.nanoTime();
        BFSSequential.BFS(adjacencyList, startNodeID); // Execute BFS
        long endTime = System.nanoTime();

        MemoryUsage afterMem = memoryBean.getHeapMemoryUsage();
        long afterUsedMem = afterMem.getUsed();
        memoryUsage.add(afterUsedMem - beforeUsedMem);
        System.out.printf("\nSequential BFS memory usage (bytes): " + String.valueOf(afterUsedMem - beforeUsedMem));// Calculate memory used by BFS

        // Time calculation
        double durationInSeconds = (endTime - startTime) / 1_000_000_000.0;
        System.out.printf("\nBFS execution time (iterative): %.9f seconds.\n", durationInSeconds);
        bfsTimes.add(durationInSeconds);
    }

    public static void runSequentialDFS(String fileName, int startNodeID, List<Double> dfsTimes, List<Long> memoryUsage) throws IOException {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage beforeMem = memoryBean.getHeapMemoryUsage();
        long beforeUsedMem = beforeMem.getUsed();

        // DFS execution
        ArrayList<ArrayList<Integer>> adjacencyList = GraphReader.readGraph(fileName);
        long startTime = System.nanoTime();
        DFSSequential.DFS(adjacencyList, startNodeID); // Execute DFS
        long endTime = System.nanoTime();

        MemoryUsage afterMem = memoryBean.getHeapMemoryUsage();
        long afterUsedMem = afterMem.getUsed();
        memoryUsage.add(afterUsedMem - beforeUsedMem); // Calculate memory used by DFS
        System.out.printf("\nSequential DFS memory usage (bytes): " + String.valueOf(afterUsedMem - beforeUsedMem));// Calculate memory used by BFS

        // Time calculation
        double durationInSeconds = (endTime - startTime) / 1_000_000_000.0;
        System.out.printf("\nDFS execution time (iterative): %.9f seconds.\n", durationInSeconds);
        dfsTimes.add(durationInSeconds);
    }

    public static void runParallelMethods(String fileName, int startNodeID) throws Exception {
        ArrayList<ArrayList<Integer>> adj = GraphReader.readGraph(fileName);
        Graph graph = convertListToGraph(adj);
        Node startNode = new Node(startNodeID);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        long startBfsTime = System.nanoTime();
        Future<Set<Node>> bfsResultFuture = runParallelBFS(executor, graph, startNode);

        long startDfsTime = System.nanoTime();
        Future<Set<Node>> dfsResultFuture = runParallelDFS(executor, graph, startNode);

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);

        // Extragere și afișare rezultate BFS
        Set<Node> bfsResult = bfsResultFuture.get();
        long endBfsTime = System.nanoTime();
        System.out.println("BFS Graph Structure:");
        bfsResult.forEach(node -> printNodeAndNeighbors(graph, node));
        printExecutionTime(startBfsTime, endBfsTime);

        // Extragere și afișare rezultate DFS
        Set<Node> dfsResult = dfsResultFuture.get();
        long endDfsTime = System.nanoTime();
        System.out.println("DFS Graph Structure:");
        dfsResult.forEach(node -> printNodeAndNeighbors(graph, node));
        printExecutionTime(startDfsTime, endDfsTime);
    }

    private static void printNodeAndNeighbors(Graph graph, Node node) {
        System.out.print("Node " + node.getId() + " connects to: ");
        graph.getNeighbors(node).forEach(neighbor -> System.out.print(neighbor.getId() + " "));
        System.out.println();
    }
    private static void printExecutionTime(long startTime, long endTime) {
        double durationInSeconds = (endTime - startTime) / 1_000_000_000.0; // Convert nanoseconds to seconds
        System.out.printf("Execution time: %.9f seconds.\n", durationInSeconds);
    }
    private static Graph convertListToGraph(ArrayList<ArrayList<Integer>> adjacencyList) {
        Graph graph = new Graph();
        for (int i = 0; i < adjacencyList.size(); i++) {
            Node sourceNode = new Node(i);
            for (int j : adjacencyList.get(i)) {
                Node destinationNode = new Node(j);
                graph.addEdge(sourceNode, destinationNode);
            }
        }
        return graph;
    }

    private static Future<Set<Node>> runParallelBFS(ExecutorService executor, Graph graph, Node startNode) {
        return executor.submit(new BFSParallel(graph, startNode));
    }

    private static Future<Set<Node>> runParallelDFS(ExecutorService executor, Graph graph, Node startNode) {
        return executor.submit(new DFSParallel(graph, startNode));
    }

    public static void runTreeBFS(String fileName, List<Double> bfsTimes, List<Long> memoryUsage) throws IOException {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage beforeMem = memoryBean.getHeapMemoryUsage();
        long beforeUsedMem = beforeMem.getUsed();

        TreeNode root = readTreeFromFile(fileName); // Ipoteză metoda de citire a arborelui
        long startTime = System.nanoTime();
        BFSSequential.treeBFS(root); // Execută BFS pe arbore
        long endTime = System.nanoTime();

        MemoryUsage afterMem = memoryBean.getHeapMemoryUsage();
        long afterUsedMem = afterMem.getUsed();
        memoryUsage.add(afterUsedMem - beforeUsedMem);
        System.out.printf("\nSequential BFS memory usage (bytes): " + String.valueOf(afterUsedMem - beforeUsedMem));// Calculate memory used by BFS


        double durationInSeconds = (endTime - startTime) / 1_000_000_000.0;
        System.out.printf("\nBFS execution time (iterative): %.9f seconds.\n", durationInSeconds);
        bfsTimes.add(durationInSeconds);
    }

    public static void runTreeDFS(String fileName, List<Double> dfsTimes, List<Long> memoryUsage) throws IOException {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage beforeMem = memoryBean.getHeapMemoryUsage();
        long beforeUsedMem = beforeMem.getUsed();

        TreeNode root = readTreeFromFile(fileName); // Ipoteză metoda de citire a arborelui
        long startTime = System.nanoTime();
        DFSSequential.treeDFS(root); // Execută DFS pe arbore
        long endTime = System.nanoTime();

        MemoryUsage afterMem = memoryBean.getHeapMemoryUsage();
        long afterUsedMem = afterMem.getUsed();
        memoryUsage.add(afterUsedMem - beforeUsedMem);
        System.out.printf("\nSequential DFS memory usage (bytes): " + String.valueOf(afterUsedMem - beforeUsedMem));// Calculate memory used by BFS

        double durationInSeconds = (endTime - startTime) / 1_000_000_000.0;
        System.out.printf("\nDFS execution time (iterative): %.9f seconds.\n", durationInSeconds);
        dfsTimes.add(durationInSeconds);
    }
    public static TreeNode readTreeFromFile(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line;
        Map<Integer, TreeNode> nodes = new HashMap<>();

        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(" ");
            int nodeId = Integer.parseInt(parts[0]);
            TreeNode node = nodes.computeIfAbsent(nodeId, TreeNode::new);

            for (int i = 1; i < parts.length; i++) {
                int childId = Integer.parseInt(parts[i]);
                TreeNode child = nodes.computeIfAbsent(childId, TreeNode::new);
                node.addChild(child);
            }
        }

        // Presupunând că primul nod este rădăcina arborelui
        return nodes.get(0); // sau orice altă logică specifică pentru a determina rădăcina
    }
}
