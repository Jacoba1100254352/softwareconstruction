package spell;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class SpellCorrector implements ISpellCorrector {

    private Trie dictionary;

    public void useDictionary(String filename) throws IOException {
        dictionary = new Trie();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                for (String word : line.split("\\s+")) {
                    dictionary.add(word.toLowerCase());
                }
            }
        }
    }

    public String suggestSimilarWord(String inputWord) {
        if (inputWord == null || inputWord.isEmpty()) {
            return null;
        }

        String lowerInput = inputWord.toLowerCase();

        // If the word is already in the dictionary, return it.
        if (dictionary.find(lowerInput) != null) {
            return lowerInput;
        }

        // Generate words with an edit distance of 1.
        Set<String> editDistanceOneWords = generateEditDistanceOne(lowerInput);

        String bestSuggestion = null;
        int maxFrequency = -1;

        // Check these first-edit-distance words against the dictionary
        for (String word : editDistanceOneWords) {
            INode node = dictionary.find(word);
            if (node != null) {
                int freq = node.getValue();
                if (freq > maxFrequency || (freq == maxFrequency && word.compareTo(bestSuggestion) < 0)) {
                    bestSuggestion = word;
                    maxFrequency = freq;
                }
            }
        }

        if (bestSuggestion != null) {
            return bestSuggestion;
        }

        // Generate words with an edit distance of 2 if no match found above.
        Set<String> editDistanceTwoWords = new HashSet<>();
        for (String word1 : editDistanceOneWords) {
            editDistanceTwoWords.addAll(generateEditDistanceOne(word1));
        }

        // Check these second-edit-distance words against the dictionary
        for (String word : editDistanceTwoWords) {
            INode node = dictionary.find(word);
            if (node != null) {
                int freq = node.getValue();
                if (freq > maxFrequency || (freq == maxFrequency && word.compareTo(bestSuggestion) < 0)) {
                    bestSuggestion = word;
                    maxFrequency = freq;
                }
            }
        }

        return bestSuggestion;  // Returns null if no similar word is found.
    }


    private Set<String> generateEditDistanceOne(String word) {
        Set<String> result = new HashSet<>();

        // Deletions
        for (int i = 0; i < word.length(); ++i) {
            result.add(word.substring(0, i) + word.substring(i + 1));
        }

        // Transpositions
        for (int i = 0; i < word.length() - 1; ++i) {
            result.add(word.substring(0, i) + word.charAt(i + 1) + word.charAt(i) + word.substring(i + 2));
        }

        // Alterations
        for (int i = 0; i < word.length(); ++i) {
            for (char c = 'a'; c <= 'z'; ++c) {
                result.add(word.substring(0, i) + c + word.substring(i + 1));
            }
        }

        // Insertions
        for (int i = 0; i <= word.length(); ++i) {
            for (char c = 'a'; c <= 'z'; ++c) {
                result.add(word.substring(0, i) + c + word.substring(i));
            }
        }

        return result;
    }
}
