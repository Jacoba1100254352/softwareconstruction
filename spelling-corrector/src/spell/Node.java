package spell;

public class Node implements INode {

    // Frequency count for the word represented by this node
    private int count = 0;

    // Array of child nodes (assuming you're using a 26-branch Trie for English alphabets)
    private final INode[] children = new INode[26];

    @Override
    public int getValue() {
        return this.count;
    }

    @Override
    public void incrementValue() {
        this.count++;
    }

    @Override
    public INode[] getChildren() {
        return this.children;
    }
}
