package tree;

import graph.TreeGenerator;
import org.junit.jupiter.api.Test;

class TreeGeneratorTest {

    @Test
    void generateTreeTest() {
        int numberOfVertices = 1000; // Numarul de noduri
        String fileName = "tree.txt"; // Numele fișierului unde vom salva arborele

        TreeGenerator.generateTree(numberOfVertices, fileName);
        System.out.printf("Arborele a fost generat si salvat in %s%n", fileName);
    }
}
