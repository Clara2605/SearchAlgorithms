package sequential;

import tree.TreeNode;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BFSSequential {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    public static boolean printOutput = true; // Control flag for printing

    // graphBFS applied to the graph
    public static void graphBFS(ArrayList<ArrayList<Integer>> adj, int s) {
        if (adj == null || adj.isEmpty() || s < 0 || s >= adj.size()) {
            LOGGER.log(Level.SEVERE, "Invalid parameters for graphBFS");
            return;
        }

        int V = adj.size();
        boolean[] visited = new boolean[V];
        Queue<Integer> queue = new LinkedList<>();

        visited[s] = true;
        queue.add(s);

        while (!queue.isEmpty()) {
            int u = queue.poll();
            if (printOutput) {
                System.out.print("Node " + u + " connects to: ");
                for (int v : adj.get(u)) {
                    System.out.print(v + " ");
                }
                System.out.println();
            }
            for (int v : adj.get(u)) {
                if (!visited[v]) {
                    visited[v] = true;
                    queue.add(v);
                }
            }
        }
    }

    public static void treeBFS(TreeNode root) {
        if (root == null) {
            LOGGER.log(Level.SEVERE, "Root is null");
            return;
        }

        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            TreeNode currentNode = queue.poll();
            if (printOutput) {
                // Start printing the current node and its direct children
                System.out.print("Node " + currentNode.getValue() + " connects to: ");
                List<TreeNode> children = currentNode.getChildren();
                if (children.isEmpty()) {
                    System.out.print("no direct children\n");
                } else {
                    children.forEach(child -> System.out.print(child.getValue() + " "));
                    System.out.println();
                }
                children.forEach(queue::add);
            }
        }
    }
}
