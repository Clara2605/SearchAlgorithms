package graph;

public class Node {
    private int id;
    private Node parent; // Adăugat pentru a gestiona referința la nodul părinte în cazul arborilor

    public Node(int id) {
        this.id = id;
        this.parent = null; // Inițial, nodul nu are un părinte asignat
    }

    // Getteri și Setteri
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }
    // Ensure you override the equals() and hashCode() methods to use the nodes in hash-based data structures like HashSet or HashMap.
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
