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
import java.util.logging.Logger;

public class TreeAlgorithmExecutor {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    public static void runTreeBFS(String fileName, List<Double> bfsTimes, List<Long> memoryUsage) throws IOException {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage beforeMem = memoryBean.getHeapMemoryUsage();
        long beforeUsedMem = beforeMem.getUsed();

        TreeNode root = readTreeFromFile(fileName); // Ipoteză metoda de citire a arborelui
        long startTime = System.nanoTime();
        BFSSequential.treeBFS(root); // Execută graphBFS pe arbore
        long endTime = System.nanoTime();

        MemoryUsage afterMem = memoryBean.getHeapMemoryUsage();
        long afterUsedMem = afterMem.getUsed();
        memoryUsage.add(afterUsedMem - beforeUsedMem);
        System.out.printf("\nSequential treeBFS memory usage (bytes): " + String.valueOf(afterUsedMem - beforeUsedMem));// Calculate memory used by graphBFS


        double durationInSeconds = (endTime - startTime) / 1_000_000_000.0;
        System.out.printf("\ntreeBFS execution time (iterative): %.9f seconds.\n", durationInSeconds);
        bfsTimes.add(durationInSeconds);
    }

    public static void runTreeDFS(String fileName, List<Double> dfsTimes, List<Long> memoryUsage) throws IOException {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage beforeMem = memoryBean.getHeapMemoryUsage();
        long beforeUsedMem = beforeMem.getUsed();

        TreeNode root = readTreeFromFile(fileName); // Ipoteză metoda de citire a arborelui
        long startTime = System.nanoTime();
        DFSSequential.treeDFS(root); // Execută graphDFS pe arbore
        long endTime = System.nanoTime();

        MemoryUsage afterMem = memoryBean.getHeapMemoryUsage();
        long afterUsedMem = afterMem.getUsed();
        memoryUsage.add(afterUsedMem - beforeUsedMem);
        System.out.printf("\nSequential treeDFS memory usage (bytes): " + String.valueOf(afterUsedMem - beforeUsedMem));// Calculate memory used by graphBFS

        double durationInSeconds = (endTime - startTime) / 1_000_000_000.0;
        System.out.printf("\ntreeDFS execution time (iterative): %.9f seconds.\n", durationInSeconds);
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