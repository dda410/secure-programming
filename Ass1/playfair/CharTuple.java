package playfair;

public class CharTuple {
	private static final char NULL_CHARACTER ='\u0000';
	private char firstChar;
	private char secondChar;
	
	public CharTuple() {		
	}
	
	public CharTuple (char a, char b) {
		firstChar = a;
		secondChar = b;
	}
	
	public void init() {
		firstChar = NULL_CHARACTER;
		secondChar = NULL_CHARACTER;
	}
	
	public char getFirstChar() {
		return firstChar;
	}
	
	public char getSecondChar() {
		return secondChar;
	}
	
	public void setFirstChar(char a) {
		firstChar = a;
	}
	
	public void setSecondChar(char b) {
		secondChar = b;
	}
	
	public boolean isTupleFull() {
		return (firstChar != NULL_CHARACTER && secondChar != NULL_CHARACTER) ? true : false;
	}
}
