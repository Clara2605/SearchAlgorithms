package sequential;


import tree.TreeNode;

import java.util.ArrayList;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DFSSequential {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

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
                System.out.print(u + " ");

                // For each neighbor of u
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
            System.out.print(currentNode.getValue()+ " "); // Procesăm nodul curent

            // Adăugăm toți copiii nodului curent în stivă, invers pentru a menține ordinea corectă de vizitare
            for (int i = currentNode.getChildren().size() - 1; i >= 0; i--) {
                stack.push(currentNode.getChildren().get(i));
            }
        }
    }
}
