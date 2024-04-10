import sequential.BFSSequential;
import sequential.DFSSequential;
import tree.TreeNode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeAlgorithmExecutor {
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
