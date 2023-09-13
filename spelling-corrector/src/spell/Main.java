package spell;

import java.io.IOException;

public class Main {

	/**
	 * Give the dictionary file name as the first argument and the word to correct
	 * as the second argument.
	 */
	public static void main(String[] args) throws IOException {

		// Check for command line arguments
		if (args.length < 2) {
			System.out.println("Usage: java Main <dictionary-file> <word-to-correct>");
			return;
		}

		String dictionaryFileName = args[0];
		String inputWord = args[1];

		// Create an instance of your SpellCorrector implementation here
		ISpellCorrector corrector = new SpellCorrector();  // Assuming your SpellCorrector class implements ISpellCorrector

		try {
			// Load the dictionary
			corrector.useDictionary(dictionaryFileName);
		} catch (IOException e) {
			System.out.println("Error reading dictionary file: " + e.getMessage());
			return;
		}

		// Get suggestion
		String suggestion = corrector.suggestSimilarWord(inputWord);

		// Output suggestion
		if (suggestion == null) {
			suggestion = "No similar word found";
		}

		System.out.println("Suggestion is: " + suggestion);
	}
}
