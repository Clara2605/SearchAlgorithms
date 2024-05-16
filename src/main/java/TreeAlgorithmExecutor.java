import parallel.BFSTreeParallel;
import parallel.DFSTreeParallel;
import sequential.BFSSequential;
import sequential.DFSSequential;
import tree.TreeNode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

public class TreeAlgorithmExecutor {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

    private static long getMemoryUsage() {
        return memoryBean.getHeapMemoryUsage().getUsed();
    }

    private static long monitorMemoryUsage(Runnable task) {
        forceGarbageCollection(); // Ensure a clean state before measurement
        long before = getMemoryUsage();
        AtomicLong peakMemory = new AtomicLong(before);

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        Runnable memoryTask = () -> {
            long currentMemory = getMemoryUsage();
            peakMemory.set(Math.max(peakMemory.get(), currentMemory));
        };

        ScheduledFuture<?> memoryMonitor = scheduler.scheduleAtFixedRate(memoryTask, 0, 10, TimeUnit.MILLISECONDS);

        try {
            task.run();
        } finally {
            memoryMonitor.cancel(true);
            scheduler.shutdown();
            try {
                scheduler.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        forceGarbageCollection();
        long after = getMemoryUsage();
        long memoryUsed = peakMemory.get() - before;
        LOGGER.info("Before memory: " + before);
        LOGGER.info("After memory: " + after);
        LOGGER.info("Peak memory: " + peakMemory.get());
        LOGGER.info("Memory used: " + memoryUsed);
        return Math.max(memoryUsed, 0); // Return positive memory usage, or zero if negative
    }

    private static void forceGarbageCollection() {
        try {
            System.gc();
            System.runFinalization();
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Thread interrupted during garbage collection.");
        }
    }


    public static void runTreeBFS(String fileName, List<Double> bfsTimes, List<Long> memoryUsage) throws IOException {
        TreeNode root = readTreeFromFile(fileName);
        BFSSequential.printOutput = false;
        long memUsed = monitorMemoryUsage(() -> BFSSequential.treeBFS(root));
        BFSSequential.printOutput = true;
        long startTime = System.nanoTime();
        BFSSequential.treeBFS(root);
        long endTime = System.nanoTime();

        double durationInSeconds = (endTime - startTime) / 1_000_000_000.0;
        bfsTimes.add(durationInSeconds);
        memoryUsage.add(memUsed);
        System.out.printf("\nTree BFS memory usage (bytes): %d\n", memUsed);
        System.out.printf("Tree BFS execution time: %.9f seconds.\n", durationInSeconds);
    }

    public static void runTreeDFS(String fileName, List<Double> dfsTimes, List<Long> memoryUsage) throws IOException {
        TreeNode root = readTreeFromFile(fileName);
        DFSSequential.printOutput = false;
        long memUsed = monitorMemoryUsage(() -> DFSSequential.treeDFS(root));
        DFSSequential.printOutput = true;
        long startTime = System.nanoTime();
        DFSSequential.treeDFS(root);
        long endTime = System.nanoTime();

        double durationInSeconds = (endTime - startTime) / 1_000_000_000.0;
        dfsTimes.add(durationInSeconds);
        memoryUsage.add(memUsed);
        System.out.printf("\nTree DFS memory usage (bytes): %d\n", memUsed);
        System.out.printf("Tree DFS execution time: %.9f seconds.\n", durationInSeconds);
    }

    public static TreeNode readTreeFromFile(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line;
        Map<Integer, TreeNode> nodes = new HashMap<>();

        reader.readLine(); // Citeste si ignora prima linie cu numarul de noduri si muchii
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(" ");
            int parentId = Integer.parseInt(parts[0]);
            TreeNode parentNode = nodes.computeIfAbsent(parentId, TreeNode::new);

            for (int i = 1; i < parts.length; i++) {
                int childId = Integer.parseInt(parts[i]);
                TreeNode childNode = nodes.computeIfAbsent(childId, TreeNode::new);
                parentNode.addChild(childNode);
            }
        }
        reader.close();

        return nodes.values().stream().filter(n -> n.getParent() == null).findFirst().orElse(null);
    }

    public static void runTreeMethods(String fileName, Scanner scanner, int nodeCount) {
        List<Double> bfsTreeTimes = new ArrayList<>();
        List<Double> dfsTreeTimes = new ArrayList<>();
        List<Long> bfsTreeMemoryUsage = new ArrayList<>();
        List<Long> dfsTreeMemoryUsage = new ArrayList<>();

        try {
            runTreeBFS(fileName, bfsTreeTimes, bfsTreeMemoryUsage);
            runTreeDFS(fileName, dfsTreeTimes, dfsTreeMemoryUsage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ExcelDataRecorder.writeData("SequentialExecutionTimes.xlsx", bfsTreeTimes, dfsTreeTimes, nodeCount, true, true);
            ExcelDataRecorder.writeData("SequentialMemoryUsage.xlsx", convertToDoubleList(bfsTreeMemoryUsage), convertToDoubleList(dfsTreeMemoryUsage), nodeCount, false, true);
        } catch (IOException e) {
            LOGGER.severe("Failed to write tree execution times to Excel for " + nodeCount + " nodes.");
        }
    }

    public static List<Double> convertToDoubleList(List<Long> longList) {
        List<Double> doubleList = new ArrayList<>();
        for (Long value : longList) {
            doubleList.add(value.doubleValue());
        }
        return doubleList;
    }

    public static void runParallelMethods(String fileName, int nodeCount) throws Exception {
        TreeNode root = readTreeFromFile(fileName);

        List<Double> bfsParallelTimes = new ArrayList<>();
        List<Double> dfsParallelTimes = new ArrayList<>();
        List<Double> bfsMemoryUsage = new ArrayList<>();
        List<Double> dfsMemoryUsage = new ArrayList<>();

        // BFS
        double bfsMemUsage = monitorParallelMemoryUsage(() -> new BFSTreeParallel(root).call());
        long startBfsTime = System.nanoTime();
        ExecutorService executorBFS = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        Future<Set<TreeNode>> bfsResult = executorBFS.submit(new BFSTreeParallel(root));
        long endBfsTime = System.nanoTime();
        double bfsDurationInSeconds = (endBfsTime - startBfsTime) / 1_000_000_000.0;
        bfsMemoryUsage.add(bfsMemUsage);
        bfsParallelTimes.add(bfsDurationInSeconds);

        // DFS
        double dfsMemUsage = monitorParallelMemoryUsage(() -> new DFSTreeParallel(root).call());
        long startDfsTime = System.nanoTime();
        ExecutorService executorDFS = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        Future<Set<TreeNode>> dfsResult = executorDFS.submit(new DFSTreeParallel(root));
        long endDfsTime = System.nanoTime();
        double dfsDurationInSeconds = (endDfsTime - startDfsTime) / 1_000_000_000.0;
        dfsMemoryUsage.add(dfsMemUsage);
        dfsParallelTimes.add(dfsDurationInSeconds);

        // Output and cleanup
        printResults("BFS", bfsResult.get(), bfsDurationInSeconds, bfsMemUsage);
        printResults("DFS", dfsResult.get(), dfsDurationInSeconds, dfsMemUsage);

        executorBFS.shutdown();
        executorBFS.awaitTermination(1, TimeUnit.HOURS);
        executorDFS.shutdown();
        executorDFS.awaitTermination(1, TimeUnit.HOURS);

        try {
            ExcelDataRecorder.writeData("ParallelExecutionTimes.xlsx", bfsParallelTimes, dfsParallelTimes, nodeCount, true, true);
            System.out.println("Parallel execution times for " + nodeCount + " nodes saved to ParallelExecutionTimes.xlsx");

            ExcelDataRecorder.writeData("ParallelMemoryUsage.xlsx", bfsMemoryUsage, dfsMemoryUsage, nodeCount, false, true);
            System.out.println("Parallel memory usage for " + nodeCount + " nodes saved to ParallelMemoryUsage.xlsx");
        } catch (IOException e) {
            System.out.println("Failed to write parallel execution data to Excel.");
            e.printStackTrace();
        }
    }

    private static double monitorParallelMemoryUsage(Callable<Set<TreeNode>> task) throws Exception {
        forceGarbageCollection();
        long startMem = getMemoryUsage();
        AtomicLong peakMem = new AtomicLong(startMem);
        Thread memoryMonitor = new Thread(() -> {
            while (!Thread.interrupted()) {
                long currentMem = getMemoryUsage();
                peakMem.set(Math.max(peakMem.get(), currentMem));
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        memoryMonitor.start();
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        Future<Set<TreeNode>> result = executor.submit(task);
        result.get();
        memoryMonitor.interrupt();
        forceGarbageCollection();
        long endMem = getMemoryUsage();
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);
        return peakMem.get() - startMem;
    }

    private static void printResults(String algorithmName, Set<TreeNode> result, double durationInSeconds, double memoryUsage) {
        System.out.println(algorithmName + " Tree Structure:");
        result.forEach(node -> System.out.println("Visited node: " + node.getValue()));
        System.out.printf(algorithmName + " Execution time: %.9f seconds.\n", durationInSeconds);
        System.out.printf(algorithmName + " Memory usage (bytes): %.0f\n", memoryUsage);
    }
}
