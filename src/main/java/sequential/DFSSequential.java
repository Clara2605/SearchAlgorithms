package sequential;


import java.util.ArrayList;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DFSSequential {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    // DFS function
    public static void DFS(ArrayList<ArrayList<Integer>> adj, int s) {
        if (adj == null || adj.isEmpty() || s < 0 || s >= adj.size()) {
            LOGGER.log(Level.SEVERE, "Invalid parameters for DFS");
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
}
