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
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

public class TreeAlgorithmExecutor {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

    private static long getMemoryUsage() {
        return memoryBean.getHeapMemoryUsage().getUsed();
    }

    private static long monitorMemoryUsage(Runnable task) {
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
        task.run();
        memoryMonitor.interrupt();
        forceGarbageCollection();

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
//    public static TreeNode readTreeFromFile(String fileName) throws IOException {
//        BufferedReader reader = new BufferedReader(new FileReader(fileName));
//        String line;
//        Map<Integer, TreeNode> nodes = new HashMap<>();
//
//        while ((line = reader.readLine()) != null) {
//            String[] parts = line.split(" ");
//            int nodeId = Integer.parseInt(parts[0]);
//            TreeNode node = nodes.computeIfAbsent(nodeId, TreeNode::new);
//
//            for (int i = 1; i < parts.length; i++) {
//                int childId = Integer.parseInt(parts[i]);
//                TreeNode child = nodes.computeIfAbsent(childId, TreeNode::new);
//                node.addChild(child);
//            }
//        }
//
//        // Presupunând că primul nod este rădăcina arborelui
//        return nodes.get(0); // sau orice altă logică specifică pentru a determina rădăcina
//    }
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

    // Returnează nodul care nu are părinte presupunând că fiecare nod exceptând rădăcina are un părinte
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
}