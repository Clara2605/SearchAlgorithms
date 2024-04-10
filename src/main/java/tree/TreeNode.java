package tree;

import java.util.ArrayList;

public class TreeNode {
    private int value; // Variabila este acum privată
    private ArrayList<TreeNode> children;

    public TreeNode(int value) {
        this.value = value;
        this.children = new ArrayList<>();
    }

    public void addChild(TreeNode child) {
        this.children.add(child);
    }

    // Getter pentru variabila value
    public int getValue() {
        return this.value;
    }

    // Getter pentru a accesa copiii nodului
    public ArrayList<TreeNode> getChildren() {
        return this.children;
    }
}
