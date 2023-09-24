package spell;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class SpellCorrector implements ISpellCorrector {

    private Trie dictionary;

    // Initialize the Trie dictionary from a given file.
    public void useDictionary(String filename) throws IOException {
        dictionary = new Trie();

        // Open the file and read its contents to populate the Trie dictionary.
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            // Loop through each line in the file, adding each word to the Trie.
            while ((line = reader.readLine()) != null)
                for (String word : line.split("\\s+"))
                    dictionary.add(word.toLowerCase());
        }
    }

    // Suggest a similar word based on the input.
    @Override
    public String suggestSimilarWord(String inputWord) {
        // Return null if input is null or empty.
        if (inputWord == null || inputWord.isEmpty())
            return null;

        // Convert all inputs/strings to lowercase
        inputWord = inputWord.toLowerCase();

        // Check if the word is already in the dictionary.
        if (dictionary.find(inputWord) != null)
            return inputWord;

        // Create and initialize a set to hold generatedWords
        Set<String> generatedWords = new HashSet<>();
        generatedWords.add(inputWord);

        String bestSuggestion = null;

        // Loop to find suggestions at edit distances 1 and 2.
        for (int distance = 1; distance <= 2; distance++) {
            Set<String> newGeneratedWords = new HashSet<>();

            // Generate new words based on current generatedWords set.
            for (String word : generatedWords)
                newGeneratedWords.addAll(generateEditDistanceOne(word));

            // Find the best suggestion among the newly generated words.
            if ((bestSuggestion = findBestSuggestion(newGeneratedWords, bestSuggestion)) != null)
                return bestSuggestion;

            // Prepare for the next iteration by updating the generatedWords set.
            generatedWords = newGeneratedWords;
        }

        return bestSuggestion;
    }

    // In the findBestSuggestion method, before and inside the for loop:
    private String findBestSuggestion(Set<String> words, String currentBest) {
        int maxFrequency = -1;  // Initialize max frequency.

        // Iterate through each word in the set to identify possible matches.
        for (String word : words) {
            // Look up the word in the dictionary.
            INode node = dictionary.find(word);
            if (node == null)
                continue;

            // Update current best suggestion if the found word has higher frequency,
            // or if frequencies are equal but the new word is lexicographically smaller (comes first alphabetically).
            int freq = node.getValue();
            if (freq > maxFrequency || (freq == maxFrequency && word.compareTo(Optional.ofNullable(currentBest).orElse("")) < 0)) {
                currentBest = word;
                maxFrequency = freq;
            }
        }
        return currentBest;
    }



    // Generate a list of all the words one letter/distance/mistake away from the original
    private Set<String> generateEditDistanceOne(String word) {
        Set<String> result = new HashSet<>();

        // Deletions
        for (int i = 0; i < word.length(); i++)
            result.add(word.substring(0, i) + word.substring(i + 1));

        // Transpositions
        for (int i = 0; i < word.length() - 1; i++)
            result.add(word.substring(0, i) + word.charAt(i + 1) + word.charAt(i) + word.substring(i + 2));

        // Alterations
        for (int i = 0; i < word.length(); i++)
            for (char chr = 'a'; chr <= 'z'; chr++)
                result.add(word.substring(0, i) + chr + word.substring(i + 1));

        // Insertions
        for (int i = 0; i <= word.length(); i++)
            for (char chr = 'a'; chr <= 'z'; chr++)
                result.add(word.substring(0, i) + chr + word.substring(i));

        return result;
    }
}