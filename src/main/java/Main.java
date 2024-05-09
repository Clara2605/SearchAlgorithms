import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final String filePathGraph50000 = "src/main/resources/data_input/graph50000.txt";
    private static final String filePathGraph10000 = "src/main/resources/data_input/graph10000.txt";
    private static final String filePathGraph1000 = "src/main/resources/data_input/graph1000.txt";
    private static final String filePathTree50000 = "src/main/resources/data_input/tree50000.txt";
    private static final String filePathTree10000 = "src/main/resources/data_input/tree10000.txt";
    private static final String filePathTree1000 = "src/main/resources/data_input/tree1000.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int dataStructureChoice;
        int nodeCount = 0; // Initialize with a value that is neither 1000 nor 10000
        int startNodeID = 0; // The starting node for the algorithms

        do {
            dataStructureChoice = displayDataStructureChoice(scanner);

            if (dataStructureChoice == 1 || dataStructureChoice == 2) { // Graf sau Arbore
                System.out.println("\nChoose the number of nodes (1000, 10000, or 50000):");
                while (!scanner.hasNextInt() || !((nodeCount = scanner.nextInt()) == 1000 || nodeCount == 10000 || nodeCount == 50000)) {
                    System.out.println("Invalid input. Please choose 1000, 10000, or 50000.");
                    scanner.nextLine(); // Clear the incorrect input
                }

                String fileName;
                if (dataStructureChoice == 1) { // Graph
                    fileName = getNodeFilePath(nodeCount, true); // true for graph
                    displayChoices();
                    int choice = scanner.nextInt();
                    choiceRun(choice, fileName, startNodeID, nodeCount, scanner, false);
                } else { // Tree
                    fileName = getNodeFilePath(nodeCount, false); // false for tree
                    displayChoices();
                    int choice = scanner.nextInt();
                    choiceRun(choice, fileName, startNodeID, nodeCount, scanner, true); // Indicate we're working with trees
                }
            } else if (dataStructureChoice == 3) {
                System.out.println("Exiting the program.");
                break;
            }
        } while (true);

        scanner.close();
    }

    private static String getNodeFilePath(int nodeCount, boolean isGraph) {
        if (isGraph) {
            switch (nodeCount) {
                case 1000:
                    return filePathGraph1000;
                case 10000:
                    return filePathGraph10000;
                case 50000:
                    return filePathGraph50000;
                default:
                    return ""; // Just in case, although this should never happen
            }
        } else {
            switch (nodeCount) {
                case 1000:
                    return filePathTree1000;
                case 10000:
                    return filePathTree10000;
                case 50000:
                    return filePathTree50000;
                default:
                    return ""; // Just in case, although this should never happen
            }
        }
    }

    private static int displayDataStructureChoice(Scanner scanner) {
        System.out.println("\nChoose the data structure:");
        System.out.println("1. Graph");
        System.out.println("2. Tree");
        System.out.println("3. Exit");
        System.out.print("Your choice (1/2/3): ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Clear the newline character
        return choice;
    }

    private static void displayChoices() {
        System.out.println("\nSelect the execution method:");
        System.out.println("1. Sequential");
        System.out.println("2. Parallel (available only for Graph)");
        System.out.println("3. Exit");
        System.out.print("Your choice (1/2/3): ");
    }

    private static void choiceRun(int choice, String fileName, int startNodeID, int nodeCount, Scanner scanner, boolean isTree) {
        if (isTree) {
            switch (choice) {
                case 1: // Execute the sequential algorithms for tree
                    // Assuming that `runTreeMethods` is a method designed to run tree-specific algorithms
                    TreeAlgorithmExecutor.runTreeMethods(fileName, scanner,nodeCount);
                    break;
                case 2:
                    System.out.println("Parallel execution is not implemented for trees.");
                    break;
                case 3: // Exit option
                    System.out.println("Exiting the program.");
                    break;
                default:
                    System.out.println("Invalid option. Please choose again.");
                    scanner.nextLine(); // Clear the incorrect input
                    break;
            }
        } else {
            switch (choice) {
                case 1: // Execute the sequential algorithms for graph
                    GraphAlgorithmExecutor.runSequentialMethods(fileName, startNodeID, nodeCount);
                    break;
                case 2: // Execute the parallel algorithms for graph
                    try {
                        GraphAlgorithmExecutor.runParallelMethods(fileName, startNodeID, nodeCount);
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
    }
}
