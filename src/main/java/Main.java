import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final String filePathGraph10000 = "src/main/resources/data_input/graph10000.txt";
    private static final String filePathGraph1000 = "src/main/resources/data_input/graph1000.txt";
    private static final String filePathTree10000 = "src/main/resources/data_input/tree10000.txt";
    private static final String filePathTree1000 = "src/main/resources/data_input/tree1000.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int choice;
        int nodeCount = 0; // Initialize with a value that is neither 1000 nor 10000
        int startNodeID = 0; // The starting node for the algorithms

        do {
            System.out.println("\nChoose the number of nodes for the graph (1000 or 10000):");
            while (!scanner.hasNextInt() || !((nodeCount = scanner.nextInt()) == 1000 || nodeCount == 10000)) {
                System.out.println("Invalid input. Please choose 1000 or 10000.");
                scanner.nextLine(); // Clear the incorrect input
            }
            String fileName = (nodeCount == 1000) ? filePathGraph1000 : filePathGraph10000;
            //String fileName = (nodeCount == 1000) ? filePathTree1000 : filePathTree10000;
            displayChoices();

            while (!scanner.hasNextInt()) {
                System.out.println("Invalid choice. Please enter 1, 2, or 3.");
                scanner.nextLine(); // Clear the incorrect input
            }
            choice = scanner.nextInt();

            choiceRun(choice, fileName, startNodeID, nodeCount, scanner);

        } while (choice != 3); // Continue running until the user chooses to exit (3)

        scanner.close();
    }

    private static void displayChoices() {
        System.out.println("\nSelect the execution method:");
        System.out.println("1. Sequential");
        System.out.println("2. Parallel");
        System.out.println("3. Exit");
        System.out.print("Your choice (1/2/3): ");
    }

    private static void choiceRun(int choice, String fileName, int startNodeID, int nodeCount, Scanner scanner) {
        switch (choice) {
            case 1: // Execute the sequential algorithms
                runSequentialMethods(fileName, startNodeID, nodeCount);
                break;
            case 2: // Execute the parallel algorithms
                try {
                    GraphAlgorithmExecutor.runParallelMethods(fileName, startNodeID);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 3: // Exit option
                System.out.println("Exiting the program.");
                break;
            default:
                System.out.println("Invalid option. Please choose again.");
                scanner.nextLine(); // Clear the incorrect input
                break;
        }
    }

    private static void runSequentialMethods(String fileName, int startNodeID, int nodeCount) {
        List<Double> bfsSequentialTimes = new ArrayList<>();
        List<Double> dfsSequentialTimes = new ArrayList<>();
        List<Long> bfsMemoryUsage = new ArrayList<>();
        List<Long> dfsMemoryUsage = new ArrayList<>();
        try {
            GraphAlgorithmExecutor.runSequentialBFS(fileName, startNodeID, bfsSequentialTimes, bfsMemoryUsage);
            GraphAlgorithmExecutor.runSequentialDFS(fileName, startNodeID, dfsSequentialTimes, dfsMemoryUsage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ExcelExecutionTimeRecorder.writeExecutionTimes("SequentialExecutionTimes.xlsx", bfsSequentialTimes, dfsSequentialTimes, nodeCount);
            System.out.println("Sequential execution times for " + nodeCount + " nodes saved to SequentialExecutionTimes.xlsx");

            // Added: Write memory usage data to Excel
            ExcelExecutionTimeRecorder.writeMemoryUsage("SequentialMemoryUsage.xlsx", bfsMemoryUsage, dfsMemoryUsage, nodeCount);
            System.out.println("Sequential memory usage for " + nodeCount + " nodes saved to SequentialMemoryUsage.xlsx");
        } catch (IOException e) {
            System.out.println("Failed to write sequential execution times to Excel for " + nodeCount + " nodes.");
            e.printStackTrace();
        }
    }
}
