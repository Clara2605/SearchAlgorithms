package tree;

import java.util.ArrayList;

public class TreeNode {
    private int value;
    private ArrayList<TreeNode> children;
    private TreeNode parent;

    public TreeNode(int value) {
        this.value = value;
        this.children = new ArrayList<>();
        this.parent = null;
    }

    public void addChild(TreeNode child) {
        this.children.add(child);
        child.parent = this;
    }
    // Getter pentru variabila value
    public int getValue() {
        return this.value;
    }

    // Getter pentru a accesa copiii nodului
    public ArrayList<TreeNode> getChildren() {
        return this.children;
    }
    public TreeNode getParent() {  // Getter pentru a accesa părintele nodului
        return this.parent;
    }
}
