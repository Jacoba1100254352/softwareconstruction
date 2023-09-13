package spell;

public class Node implements INode {

    // Frequency count for the word represented by this node
    private int value = 0;

    // Array of child nodes (assuming you're using a 26-branch Trie for English alphabets)
    private INode[] children = new INode[26];

    /*
    public Node() {
        // Initialize frequency count to 0
        this.value = 0;

        // Initialize the children array to hold 26 child nodes (one for each letter a-z)
        this.children = new INode[26];
    }*/

    @Override
    public int getValue() {
        return this.value;
    }

    @Override
    public void incrementValue() {
        this.value++;
    }

    @Override
    public INode[] getChildren() {
        return children;
    }
}
