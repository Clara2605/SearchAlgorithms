package graph;

public class Node {
    private int id; // Unique identifier for each node

    public Node(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return id == node.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    // Optional, to ease displaying
    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                '}';
    }
}
