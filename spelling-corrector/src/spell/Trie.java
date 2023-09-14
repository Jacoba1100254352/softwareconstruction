package spell;

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
        for (char c : word.toCharArray()) {
            current = current.getChildren()[c - 'a'];
            if (current == null) return null;
        }
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
    private void toStringHelper(INode node, StringBuilder currentWord, StringBuilder result) {
        if (node.getValue() > 0)
            result.append(currentWord).append('\n');

        for (int i = 0; i < node.getChildren().length; i++) {
            INode child = node.getChildren()[i];
            if (child != null) {
                char letter = (char) ('a' + i);
                currentWord.append(letter);
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
        return hashCodeHelper(root, 0);
    }

    // Recursive helper function for generating hash code.
    private int hashCodeHelper(INode node, int character) {
        if (node == null) return 0;

        int result = node.getValue() + character; // Incorporate the character information
        for (int i = 0; i < node.getChildren().length; i++) {
            INode child = node.getChildren()[i];
            result = 31 * result + hashCodeHelper(child, i);  // Incorporate index (which indirectly represents the character) into the hash
        }
        return result;
    }


    /**
     * Compares this Trie to another object for equality.
     *
     * @param o The object to compare.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trie that = (Trie) o;
        return this.wordCount == that.wordCount &&
                this.nodeCount == that.nodeCount &&
                nodesEqual(this.root, that.root);
    }

    // Recursive helper function to compare nodes for equality.
    private boolean nodesEqual(INode a, INode b) {
        if (a == null || b == null) return a == b;
        else if (a.getValue() != b.getValue()) return false;
        for (int i = 0; i < a.getChildren().length; i++)
            if (!nodesEqual(a.getChildren()[i], b.getChildren()[i])) return false;
        return true;
    }

    /*
        @Override
    public boolean equals(Object o) {
        return this == o || o != null && getClass() == o.getClass() && wordCount == ((Trie) o).wordCount && nodeCount == ((Trie) o).nodeCount && nodesEqual(root, ((Trie) o).root);
    }

    private boolean nodesEqual(INode a, INode b) {
        return (a == null || b == null) ? a == b :
                a.getValue() == b.getValue() &&
                        IntStream.range(0, a.getChildren().length)
                                .allMatch(i -> nodesEqual(a.getChildren()[i], b.getChildren()[i]));
    }
     */


}

