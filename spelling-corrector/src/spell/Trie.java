package spell;

import java.util.stream.IntStream;


public class Trie implements ITrie {

    private final INode root;
    private int wordCount, nodeCount;

    /**
     * Constructor initializes Trie with a new root node, 1 nodeCount and no (0) wordCount.
     */
    public Trie() {
        this.root = new Node();
        this.wordCount = 0;
        this.nodeCount = 1;
    }

    /**
     * Adds a word to the Trie object.
     *
     * @param word The word to be added.
     */
    @Override
    public void add(String word) {
        INode current = root;
        for (char c : word.toCharArray()) {
            int index = c - 'a';
            if (current.getChildren()[index] == null) {
                current.getChildren()[index] = new Node();
                nodeCount++;
            }
            current = current.getChildren()[index];
        }
        if (current.getValue() == 0)
            wordCount++;
        current.incrementValue();
    }

    /**
     * Finds a word in the Trie.
     *
     * @param word The word to find.
     * @return The node where the word ends, null if not found.
     */
    @Override
    public INode find(String word) {
        INode current = root;
        for (char chr : word.toCharArray())
            if ((current = current.getChildren()[chr - 'a']) == null) return null;
        return current.getValue() == 0 ? null : current;
    }


    /**
     * @return The total number of unique words in the Trie.
     */
    @Override
    public int getWordCount() {
        return this.wordCount;
    }

    /**
     * @return The total number of nodes in the Trie.
     */
    @Override
    public int getNodeCount() {
        return this.nodeCount;
    }

    /**
     * Converts the Trie to a string representation.
     *
     * @return String representation of the Trie.
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        toStringHelper(root, new StringBuilder(), result);
        return result.toString();
    }

    // Helper function to recursively build the string representation.
    private static void toStringHelper(INode node, StringBuilder currentWord, StringBuilder result) {
        // If value > 0, the node represents a word, add the word (and continue with new line)
        if (node.getValue() > 0)
            result.append(currentWord).append('\n');

        // Go through each child if it is not null append the letter to the StringBuilder
        // Then recurse over that child/letter to see build and add more words
        // After recursing undo to continue through all/other options
        for (int i = 0; i < NUM_LETTERS; i++) {
            INode child = node.getChildren()[i];
            if (child != null) {
                currentWord.append((char) ('a' + i));
                toStringHelper(child, currentWord, result);
                currentWord.deleteCharAt(currentWord.length() - 1); // backtrack
            }
        }
    }

    /**
     * Generates a hash code for the Trie.
     *
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        int childrenBitmap = 0;  // Initialize a 26-bit bitmap for children

        for (int i = 0; i < NUM_LETTERS; i++)
            if (root.getChildren()[i] != null)
                childrenBitmap |= (1 << i);  // Set the i-th bit if the child exists

        // Combine the bitmap into the hash code
        return 31 * (31 * wordCount + nodeCount) + childrenBitmap;
    }



    /**
     * Compares this Trie to another object for equality.
     *
     * @param object The object to compare.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object object) {
        return this == object ||
                (object != null && this.getClass() == object.getClass() &&
                        (this.wordCount == ((Trie) object).wordCount) &&
                (this.nodeCount == ((Trie) object).nodeCount) && nodesEqual(this.root, ((Trie) object).root));
    }

    // Recursive helper function to compare nodes for equality.
    private boolean nodesEqual(INode node1, INode node2) {
        return (node1 == null || node2 == null) ? node1 == node2 :
                node1.getValue() == node2.getValue() &&
                IntStream.range(0, NUM_LETTERS).allMatch(i -> nodesEqual(node1.getChildren()[i], node2.getChildren()[i]));
    }

}

