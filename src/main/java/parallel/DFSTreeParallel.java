package parallel;

import graph.MyLogger;
import tree.TreeNode;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Level;

public class DFSTreeParallel implements Callable<Set<TreeNode>> {
    private final TreeNode root;
    private final ConcurrentLinkedDeque<TreeNode> stack = new ConcurrentLinkedDeque<>();
    private final ConcurrentHashMap<TreeNode, Boolean> visited = new ConcurrentHashMap<>();

    public DFSTreeParallel(TreeNode root) {
        this.root = root;
        this.stack.push(root);
        this.visited.put(root, true);
    }

    @Override
    public Set<TreeNode> call() {
        try {
            while (!stack.isEmpty()) {
                TreeNode currentNode = stack.pop();
                Collections.reverse(currentNode.getChildren()); // Reverse to maintain DFS order
                for (TreeNode child : currentNode.getChildren()) {
                    if (visited.putIfAbsent(child, true) == null) {
                        stack.push(child);
                    }
                }
                //System.out.println(); // Finish the line after all children are printed
            }
        } catch (Exception e) {
            MyLogger.log(Level.SEVERE, "An error occurred during tree DFS: " + e.getMessage());
            // Optionally rethrow or handle the exception
        }
        return visited.keySet();
    }
}
