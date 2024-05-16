package sequential;


import tree.TreeNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DFSSequential {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    public static boolean printOutput = true;

    // graphDFS function
    public static void graphDFS(ArrayList<ArrayList<Integer>> adj, int s) {
        if (adj == null || adj.isEmpty() || s < 0 || s >= adj.size()) {
            LOGGER.log(Level.SEVERE, "Invalid parameters for graphDFS");
            return;
        }
        boolean[] visited = new boolean[adj.size()];
        Stack<Integer> stack = new Stack<>();
        stack.push(s);

        while (!stack.isEmpty()) {
            int u = stack.pop();
            if (!visited[u]) {
                visited[u] = true;
                if (printOutput) {
                    System.out.print("Node " + u + " connects to: ");
                    adj.get(u).forEach(v -> System.out.print(v + " "));
                    System.out.println();
                }
                for (int v : adj.get(u)) {
                    if (!visited[v]) {
                        stack.push(v);
                    }
                }
            }
        }
    }
    public static void treeDFS(TreeNode root) {
        if (root == null) {
            LOGGER.log(Level.SEVERE, "Root is null");
            return;
        }

        Stack<TreeNode> stack = new Stack<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            TreeNode currentNode = stack.pop();
            if (printOutput) {
                System.out.print("Node " + currentNode.getValue() + " connects to: ");
                List<TreeNode> children = currentNode.getChildren();
                if (children.isEmpty()) {
                    System.out.print("no direct children\n");
                } else {
                    children.forEach(child -> System.out.print(child.getValue() + " "));
                    System.out.println();
                }
                children.forEach(stack::push);
            }
        }
    }
}
