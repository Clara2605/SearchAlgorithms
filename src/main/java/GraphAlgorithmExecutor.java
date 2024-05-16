import graph.Graph;
import graph.GraphReader;
import graph.Node;
import parallel.BFSGraphParallel;
import parallel.DFSGraphParallel;
import sequential.BFSSequential;
import sequential.DFSSequential;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class GraphAlgorithmExecutor {
    private static final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

    private static long getMemoryUsage() {
        return memoryBean.getHeapMemoryUsage().getUsed();
    }
    private static double monitorMemoryUsage(Runnable task) {
        forceGarbageCollection(); // Ensure a clean state before measurement
        long startMem = getMemoryUsage();
        AtomicLong peakMem = new AtomicLong(startMem);
        Thread memoryMonitor = new Thread(() -> {
            while (!Thread.interrupted()) {
                long currentMem = getMemoryUsage();
                peakMem.set(Math.max(peakMem.get(), currentMem));
                try {
                    Thread.sleep(100); // Sleep for a short time to prevent high CPU usage
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        memoryMonitor.start(); // Start monitoring
        task.run();
        memoryMonitor.interrupt(); // End monitoring
        forceGarbageCollection();
        long endMem = getMemoryUsage();

        //return (peakMem.get() - startMem) / (1024.0 * 1024.0); // Return the peak memory usage in megabytes
        return peakMem.get() - startMem; // Return the peak memory usage in bytes
    }
    private static void forceGarbageCollection() {
        try {
            System.gc();
            System.runFinalization();
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Thread interrupted during garbage collection.");
        }
    }
    public static void runSequentialBFS(String fileName, int startNodeID, List<Double> bfsTimes, List<Double> memoryUsage) throws IOException {
        ArrayList<ArrayList<Integer>> adjacencyList = GraphReader.readGraph(fileName);
        BFSSequential.printOutput = false;
        double memUsed = monitorMemoryUsage(() -> BFSSequential.graphBFS(adjacencyList, startNodeID));
        BFSSequential.printOutput = true;
        long startTime = System.nanoTime();
        BFSSequential.graphBFS(adjacencyList, startNodeID); // Execute graphBFS
        long endTime = System.nanoTime();
        double durationInSeconds = (endTime - startTime) / 1_000_000_000.0;
        bfsTimes.add(durationInSeconds);
        memoryUsage.add(memUsed);
       // System.out.printf("\nSequential BFS memory usage (MB): %.2f\n", memUsed);
        System.out.printf("\nSequential BFS memory usage (bytes): %.0f\n", memUsed);
        System.out.printf("Sequential BFS execution time: %.9f seconds.\n", durationInSeconds);

    }

    public static void runSequentialDFS(String fileName, int startNodeID, List<Double> dfsTimes, List<Double> memoryUsage) throws IOException {
        ArrayList<ArrayList<Integer>> adjacencyList = GraphReader.readGraph(fileName);
        DFSSequential.printOutput = false;
        double memUsed = monitorMemoryUsage(() -> DFSSequential.graphDFS(adjacencyList, startNodeID));
        DFSSequential.printOutput = true;
        long startTime = System.nanoTime();
        DFSSequential.graphDFS(adjacencyList, startNodeID); // Execute graphDFS
        long endTime = System.nanoTime();
        double durationInSeconds = (endTime - startTime) / 1_000_000_000.0;
        dfsTimes.add(durationInSeconds);
        memoryUsage.add(memUsed);
       // System.out.printf("\nSequential DFS memory usage (MB): %.2f\n", memUsed);
        System.out.printf("\nSequential DFS memory usage (bytes): %.0f\n", memUsed);
        System.out.printf("\ngraphDFS execution time (iterative): %.9f seconds.\n", durationInSeconds);
    }

    public static void runSequentialMethods(String fileName, int startNodeID, int nodeCount) {
        List<Double> bfsSequentialTimes = new ArrayList<>();
        List<Double> dfsSequentialTimes = new ArrayList<>();
        List<Double> bfsMemoryUsage = new ArrayList<>();
        List<Double> dfsMemoryUsage = new ArrayList<>();
        try {
            runSequentialBFS(fileName, startNodeID, bfsSequentialTimes, bfsMemoryUsage);
            runSequentialDFS(fileName, startNodeID, dfsSequentialTimes, dfsMemoryUsage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ExcelDataRecorder.writeData("SequentialExecutionTimes.xlsx", bfsSequentialTimes, dfsSequentialTimes, nodeCount, true,false);
            System.out.println("Sequential execution times for " + nodeCount + " nodes saved to SequentialExecutionTimes.xlsx");

            // Added: Write memory usage data to Excel
            ExcelDataRecorder.writeData("SequentialMemoryUsage.xlsx", bfsMemoryUsage, dfsMemoryUsage, nodeCount, false,false);
            System.out.println("Sequential memory usage for " + nodeCount + " nodes saved to SequentialMemoryUsage.xlsx");
        } catch (IOException e) {
            System.out.println("Failed to write sequential execution times to Excel for " + nodeCount + " nodes.");
            e.printStackTrace();
        }
    }

    public static void runParallelMethods(String fileName, int startNodeID, int nodeCount) throws Exception {
        ArrayList<ArrayList<Integer>> adj = GraphReader.readGraph(fileName);
        Graph graph = convertListToGraph(adj);
        Node startNode = new Node(startNodeID);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        List<Double> bfsParallelTimes = new ArrayList<>();
        List<Double> dfsParallelTimes = new ArrayList<>();
        List<Double> bfsMemoryUsage = new ArrayList<>();
        List<Double> dfsMemoryUsage = new ArrayList<>();

        // Memory and execution time monitoring for BFS
        double bfsMemUsage = monitorParallelMemoryUsage(() -> new BFSGraphParallel(graph, startNode).call(), executor);
        long startBfsTime = System.nanoTime();
        Set<Node> bfsResult = runParallelBFS(executor, graph, startNode).get();
        long endBfsTime = System.nanoTime();
        double bfsDurationInSeconds = (endBfsTime - startBfsTime) / 1_000_000_000.0; // Convert nanoseconds to seconds
        bfsMemoryUsage.add(bfsMemUsage);
        bfsParallelTimes.add(bfsDurationInSeconds);

        // Memory and execution time monitoring for DFS
        double dfsMemUsage = monitorParallelMemoryUsage(() -> new DFSGraphParallel(graph, startNode).call(), executor);
        long startDfsTime = System.nanoTime();
        Set<Node> dfsResult = runParallelDFS(executor, graph, startNode).get();
        long endDfsTime = System.nanoTime();
        double dfsDurationInSeconds = (endDfsTime - startDfsTime) / 1_000_000_000.0; // Convert nanoseconds to seconds
        dfsMemoryUsage.add(dfsMemUsage);
        dfsParallelTimes.add(dfsDurationInSeconds);

        // Output results and cleanup
        printResults("BFS", bfsResult, bfsDurationInSeconds, bfsMemUsage, graph);
        printResults("DFS", dfsResult, dfsDurationInSeconds, dfsMemUsage, graph);

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);

        // Write results to Excel
        try {
            ExcelDataRecorder.writeData("ParallelExecutionTimes.xlsx", bfsParallelTimes, dfsParallelTimes, nodeCount, true, false);
            System.out.println("Parallel execution times for " + nodeCount + " nodes saved to ParallelExecutionTimes.xlsx");

            ExcelDataRecorder.writeData("ParallelMemoryUsage.xlsx", bfsMemoryUsage, dfsMemoryUsage, nodeCount, false, false);
            System.out.println("Parallel memory usage for " + nodeCount + " nodes saved to ParallelMemoryUsage.xlsx");
        } catch (IOException e) {
            System.out.println("Failed to write parallel execution data to Excel.");
            e.printStackTrace();
        }
    }

    private static void printResults(String algorithmName, Set<Node> result, double durationInSeconds, double memoryUsage, Graph graph) {
        System.out.println(algorithmName + " Graph Structure:");
        result.forEach(node -> printNodeAndNeighbors(graph, node));
        System.out.printf(algorithmName + " Execution time: %.9f seconds.\n", durationInSeconds);
        //System.out.printf(algorithmName + " Memory usage (MB): %.2f\n", memoryUsage);
        System.out.printf(algorithmName + " Memory usage (bytes): %.0f\n", memoryUsage);
    }

    private static void printNodeAndNeighbors(Graph graph, Node node) {
        System.out.print("Node " + node.getId() + " connects to: ");
        graph.getNeighbors(node).forEach(neighbor -> System.out.print(neighbor.getId() + " "));
        System.out.println();
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
        return executor.submit(new BFSGraphParallel(graph, startNode));
    }

    private static Future<Set<Node>> runParallelDFS(ExecutorService executor, Graph graph, Node startNode) {
        return executor.submit(new DFSGraphParallel(graph, startNode));
    }
    private static double monitorParallelMemoryUsage(Callable<Set<Node>> task, ExecutorService executor) throws Exception {
        forceGarbageCollection();  // Ensure a clean state before measurement
        long startMem = getMemoryUsage();
        AtomicLong peakMem = new AtomicLong(startMem);
        Thread memoryMonitor = new Thread(() -> {
            while (!Thread.interrupted()) {
                long currentMem = getMemoryUsage();
                peakMem.set(Math.max(peakMem.get(), currentMem));
                try {
                    Thread.sleep(10);  // Sleep for a short time to prevent high CPU usage
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        memoryMonitor.start();  // Start monitoring
        Future<Set<Node>> result = executor.submit(task);
        Set<Node> nodes = result.get();  // Ensure task is completed
        memoryMonitor.interrupt();  // End monitoring
        forceGarbageCollection();
        long endMem = getMemoryUsage();

        //return (peakMem.get() - startMem) / (1024.0 * 1024.0);  // Return the peak memory usage in megabytes
        return peakMem.get() - startMem;  // Return the peak memory usage in bytes
    }
}
