package sequential;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BFSSequential {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    // BFS applied to the graph
    public static void BFS(ArrayList<ArrayList<Integer>> adj, int s) {
        if (adj == null || adj.isEmpty() || s < 0 || s >= adj.size()) {
            LOGGER.log(Level.SEVERE, "Invalid parameters for BFS");
            return;
        }

        int V = adj.size();
        boolean[] visited = new boolean[V];
        Queue<Integer> queue = new LinkedList<>();

        visited[s] = true;
        queue.add(s);

        while (!queue.isEmpty()) {
            int u = queue.poll();
            System.out.print(u + " ");

            for (int v : adj.get(u)) {
                if (!visited[v]) {
                    visited[v] = true;
                    queue.add(v);
                }
            }
        }
    }
}
