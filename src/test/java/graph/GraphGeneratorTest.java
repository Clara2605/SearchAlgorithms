package graph;

import graph.GraphGenerator;
import org.junit.jupiter.api.Test;



class GraphGeneratorTest {

    @Test
    void generate(){

        int numberOfVertices = 50000; // Numarul de noduri
        int numberOfEdges = 75000; // Numarul de muchii
        String fileName = "graph50000.txt"; // Numele fisierului unde vom salva graf-ul

        GraphGenerator.generateGraph(numberOfVertices, numberOfEdges, fileName);
        System.out.printf("Graful a fost generat si salvat in %s%n", fileName);
    }
}