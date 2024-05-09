package parallel;

import graph.MyLogger;
import tree.TreeNode;

import java.util.ArrayList;
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
                // Process the current node here, for example, you might want to log it or keep it in a result list
                System.out.print(currentNode.getValue() + " ");

                // Reverse the children before pushing to stack to maintain the correct DFS order
                ArrayList<TreeNode> children = new ArrayList<>(currentNode.getChildren());
                for (int i = children.size() - 1; i >= 0; i--) {
                    TreeNode child = children.get(i);
                    if (visited.putIfAbsent(child, true) == null) {
                        stack.push(child);
                    }
                }
            }
        } catch (Exception e) {
            MyLogger.log(Level.SEVERE, "An error occurred during treeDFS: " + e.getMessage());
            // Optionally rethrow or handle the exception
        }
        return visited.keySet();
    }
}
