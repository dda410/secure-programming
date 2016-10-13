package vigenerCipherBruteForce;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class VigenerCipherBruteForce {

	private static final char[] ALPHABET = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 
											'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y','z', '!', ' '};
//	this double variable sets the percentage of English words in a string in order to be considered written in English.
//	It is useful in order to eliminate all the brute-force decrypted strings that do not make any sense in English. 
	private static final double ENGLISH_WORD_PERCENTAGE = 0.9;
	
	private static int getCharIndexInAlphabet(char c) {
		for (int i = 0; i < ALPHABET.length; i++) {
			if (c==ALPHABET[i])
				return i;
		}
		return 0;
	}

//	Shifts the letters of the input strings accordingly to the key letters indices.
	private static String decryptString(String toBeDecrypted, int[] keyIndices) {
		String result="";
		for (int i = 0; i < toBeDecrypted.length(); i++) {
			if (i%2 == 0) {
				result+=ALPHABET[(getCharIndexInAlphabet(toBeDecrypted.charAt(i)) + keyIndices[0]) % ALPHABET.length];
			}else {
				result+=ALPHABET[(getCharIndexInAlphabet(toBeDecrypted.charAt(i)) + keyIndices[1]) % ALPHABET.length];
			}
		}
		return result;
	}
	
//	Checks if the word is receiving as parameter is contained in the unix american-english dictionary
	public static boolean isEnglishWord(String word) {
		try {
			BufferedReader in = new BufferedReader(new FileReader("american-english"));
			String str;
			while ((str = in.readLine()) != null) {
				if (str.indexOf(word) != -1) {
					return true;
				}
			}
			in.close();
		}catch (IOException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}

		return false;
	}

//	Checks whether a decrypted string is written in English.
	private static boolean englishLanguageChecker(String s) {
		String[] arr = s.split(" ");
		int numberOfWords = 0;
		int numberOfEnglishWords = 0;
		for (String word : arr) {
			numberOfWords++;
			if (isEnglishWord(word)) {
				numberOfEnglishWords++;
			}
		}
		return (((double)numberOfEnglishWords/numberOfWords) > ENGLISH_WORD_PERCENTAGE) ? true : false;
	}
	
	public static void main(String[] args) {
		String inputString = args[0];
		String decryptedString;
//		The algorithm will produce ALPHABET.length^key.length possible decrypted strings. In this exact case
//		where the key.length is 2 it will produce 27^2 = 784 different possible results.
		for (int i = 0; i < ALPHABET.length; i++) {
			for (int j = 0; j < ALPHABET.length; j++) {
				decryptedString = decryptString(inputString, new int[] {i, j});
				if (englishLanguageChecker(decryptedString)) {
					System.out.println("\nDecrypted text with the following key: "+(char)ALPHABET[i]+""+(char)ALPHABET[j]);
					System.out.println(decryptedString);
				}
			}
		}
	}
}
