package caesarCipherBruteForce;

public class CaesarCipherBruteForce {

	private static final char[] ALPHABET = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 
											'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y','z', '!', ' '};
	
	private static int getCharIndexInAlphabet(char c) {
		for (int i = 0; i < ALPHABET.length; i++) {
			if (c==ALPHABET[i])
				return i;
		}
		return 0;
	}
	
	public static void main(String[] args) {
		String inputString = args[0];
		for (int i = 0; i < ALPHABET.length; i++) {
			System.out.println("\nShifting letters by "+i+" index position:");
			for (int j = 0; j < inputString.length(); j++) {
				System.out.print(ALPHABET[(getCharIndexInAlphabet(inputString.charAt(j)) + i) % ALPHABET.length]);
			}
			System.out.println();
		}
	}
}
