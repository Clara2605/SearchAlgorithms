package parallel;

import graph.MyLogger;
import tree.TreeNode;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

public class BFSTreeParallel implements Callable<Set<TreeNode>> {
    private final TreeNode root;
    private final ConcurrentLinkedQueue<TreeNode> queue = new ConcurrentLinkedQueue<>();
    private final ConcurrentHashMap<TreeNode, Boolean> visited = new ConcurrentHashMap<>();

    public BFSTreeParallel(TreeNode root) {
        this.root = root;
        this.queue.add(root);
        this.visited.put(root, true);
    }

    @Override
    public Set<TreeNode> call() {
        try {
            while (!queue.isEmpty()) {
                TreeNode currentNode = queue.poll();
                if (currentNode == null) continue; // Safety check
                for (TreeNode child : currentNode.getChildren()) {
                    if (visited.putIfAbsent(child, true) == null) {
                        queue.add(child);
                    }
                }
                //System.out.println(); // Finish the line after all children are printed
            }
        } catch (Exception e) {
            MyLogger.log(Level.SEVERE, "An error occurred during tree BFS: " + e.getMessage());
            // Handle or rethrow the exception as needed
        }
        return visited.keySet(); // Return the set of visited nodes
    }
}
