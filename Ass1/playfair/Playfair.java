package playfair;

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class Playfair {
	
	private static final char UPPERCASE_ALPHA = 'A';
	private static final char UPPERCASE_ZETA = 'Z';
	private static final char LOWERCASE_ALPHA = 'a';
	private static final char LOWERCASE_ZETA = 'z';
	private static final String DECRYPTING_OPTION = "--decrypt";
	private static final String ENCRYPTING_OPTION = "--encrypt";
	private static final char EQUAL_LETTERS_SEPARATOR = 'X';
	private static final char EVEN_LETTERS_PADDING = 'Z';
	private static final int cipherTablesize = 5;
	private static final String alphabet = "ABCDEFGHIKLMNOPQRSTUVWXYZ";
	private static final char NULL_CHARACTER ='\u0000';

//	Parsing method that removes all non alphabet characters and transforms the J to I
//	In order to be possible to merge the key with the alphabet char 5*5 matrix.
	private String parseStringKey(Scanner keyReader){
		String parse = keyReader.nextLine();
		return parse.toUpperCase().replaceAll("[^A-Z]", "").replace("J", "I");
	}
	
	private String removeDup2(String s) {
		int stringLength = s.length();
		char ch;
		String result="";    
		for(int i = 0; i < stringLength; i++) {
			ch = s.charAt(i);
			if(ch!=' '){
				result += ch;
			}
			s = s.replace(ch,' '); //Replacing all occurrence of the current character by a space
		}
		return result;
	}
	
//	First the key is merged with the remaining letters of the alphabet resulting in a 25-character string. 
//	Then the 5*5 char array is filled by the obtained 25 character string.
	private char [][] buildCipherTable(String key) {
		String tableAlphabet = removeDup2(key+alphabet);
		char [][] cipherTable = new char [cipherTablesize][cipherTablesize];
		int alphabetIndex = 0;
		for (int i = 0; i < cipherTable.length; i++) {
			for (int j = 0; j<cipherTable[0].length ; j++) {
				cipherTable[i][j] = tableAlphabet.charAt(alphabetIndex);
				alphabetIndex++;
			}
		}
		return cipherTable;
	}
	
//	debugging method to print the cipher table char matrix.
	private void printTable (char[][] cipherTable) {
		System.out.println("This is the table being used");
		for (int i = 0; i<cipherTable.length; i++) {
			for (int j = 0; j<cipherTable[0].length ; j++) {
				System.out.print(cipherTable[i][j]+" ");
				if (j == 4)
					System.out.println();
			}
		}
	}

	private CharCipherTableCoordinate getCoordinates (char c, char[][] cipherTable) {
		for (int i = 0; i<cipherTable.length; i++) {
			for (int j = 0; j<cipherTable[0].length ; j++) {
				if (cipherTable[i][j] == c){
					return new CharCipherTableCoordinate(i, j);
				}
			}
		}
		return new CharCipherTableCoordinate(0, 0);
	}
	
//	encrypting characters according to playfair algorithm rules
	private CharTuple encryptCharacters(char [][] cipherTable, CharCipherTableCoordinate firstCharCoord, CharCipherTableCoordinate secondCharCoord) {
		if (firstCharCoord.getCoordinateX() == secondCharCoord.getCoordinateX()){
			firstCharCoord.setCoordinateY((firstCharCoord.getCoordinateY()+1)%cipherTablesize);
			secondCharCoord.setCoordinateY((secondCharCoord.getCoordinateY()+1)%cipherTablesize);
		}else if (firstCharCoord.getCoordinateY() == secondCharCoord.getCoordinateY()) {
			firstCharCoord.setCoordinateX((firstCharCoord.getCoordinateX()+1)%cipherTablesize);
			secondCharCoord.setCoordinateX((secondCharCoord.getCoordinateX()+1)%cipherTablesize);			
		}else {
			int tmp = firstCharCoord.getCoordinateY();
			firstCharCoord.setCoordinateY(secondCharCoord.getCoordinateY());
			secondCharCoord.setCoordinateY(tmp);
		}
		return new CharTuple(cipherTable[firstCharCoord.getCoordinateX()][firstCharCoord.getCoordinateY()], 
				cipherTable[secondCharCoord.getCoordinateX()][secondCharCoord.getCoordinateY()]);
	}
	
//	decrypting character according to plsyfair algorithm rules
	private CharTuple decryptCharacters(char [][] cipherTable, CharCipherTableCoordinate firstCharCoord, CharCipherTableCoordinate secondCharCoord) {
		if (firstCharCoord.getCoordinateX() == secondCharCoord.getCoordinateX()){
//			Added additional check with ternary operator in order not to have a negative index
			firstCharCoord.setCoordinateY(firstCharCoord.getCoordinateY() == 0?4:(firstCharCoord.getCoordinateY()-1)%cipherTablesize);
			secondCharCoord.setCoordinateY(secondCharCoord.getCoordinateY() == 0?4:(secondCharCoord.getCoordinateY()-1)%cipherTablesize);
		}else if (firstCharCoord.getCoordinateY() == secondCharCoord.getCoordinateY()) {
			firstCharCoord.setCoordinateX(firstCharCoord.getCoordinateX() == 0?4:(firstCharCoord.getCoordinateX()-1)%cipherTablesize);			
			secondCharCoord.setCoordinateX(secondCharCoord.getCoordinateX() == 0?4:(secondCharCoord.getCoordinateX()-1)%cipherTablesize);			
		}else {
			int tmp = firstCharCoord.getCoordinateY();
			firstCharCoord.setCoordinateY(secondCharCoord.getCoordinateY());
			secondCharCoord.setCoordinateY(tmp);
		}
		return new CharTuple(cipherTable[firstCharCoord.getCoordinateX()][firstCharCoord.getCoordinateY()], 
				cipherTable[secondCharCoord.getCoordinateX()][secondCharCoord.getCoordinateY()]);
	}
	
	private CharTuple encryptDecryptCharacters (char [][] cipherTable, CharTuple tuple, boolean encryption) {
		CharCipherTableCoordinate firstCharCoordinates = getCoordinates(tuple.getFirstChar(), cipherTable);
		CharCipherTableCoordinate secondCharCoordinates = getCoordinates(tuple.getSecondChar(), cipherTable);
		if (encryption) {
			return encryptCharacters(cipherTable, firstCharCoordinates, secondCharCoordinates);
		}
		else{
			return decryptCharacters(cipherTable, firstCharCoordinates, secondCharCoordinates);
		}
	}

	private boolean isLetter(char c){
		if( ((c >= UPPERCASE_ALPHA )&&(c <= UPPERCASE_ZETA )) || ((c >= LOWERCASE_ALPHA)&&(c <= LOWERCASE_ZETA)) )
			return true;
		return false;
	}
	
	private CharTuple addLetterToTuple(CharTuple tuple, char c) {
		if (tuple.getFirstChar() == NULL_CHARACTER) {
			tuple.setFirstChar(Character.toUpperCase(c));
		}else {
			tuple.setSecondChar(Character.toUpperCase(c));
		}
		return tuple;
	}
	
	private CharTuple writeTupleToOutput(char [][] cipherTable, CharTuple tuple, boolean encryptionFlag, PrintWriter outputStream, CharTuple processedTuple) {
//		in the case the two letters of the tuple are the same the separator is added.
		if (tuple.getFirstChar() == tuple.getSecondChar()) {
			tuple.setSecondChar(EQUAL_LETTERS_SEPARATOR);
			processedTuple = encryptDecryptCharacters(cipherTable, tuple, encryptionFlag);
			tuple.setSecondChar(NULL_CHARACTER);
		}else {
			processedTuple = encryptDecryptCharacters(cipherTable, tuple, encryptionFlag);
			tuple.init();
		}
//		writing processed tuple (decrypted or encrypted depending on the flag) to output file.
		outputStream.print(processedTuple.getFirstChar()+""+processedTuple.getSecondChar()+" ");
		return tuple;
	}
	
	private void paddingLastTuple(CharTuple tuple, CharTuple processedTuple, PrintWriter outputStream, char [][] cipherTable, boolean encryptionFlag) {
		if (tuple.getFirstChar()!=NULL_CHARACTER) {
			tuple.setSecondChar(EVEN_LETTERS_PADDING);
			processedTuple = encryptDecryptCharacters(cipherTable, tuple, encryptionFlag);
			outputStream.print(processedTuple.getFirstChar()+""+processedTuple.getSecondChar()+" ");
		}
	}

	void start(String[] argv) {
		boolean encryptionFlag;
		if(argv[0].equals(ENCRYPTING_OPTION)) {
			encryptionFlag = true;
		}else {
			encryptionFlag = false;
		}
		Scanner in = new Scanner (System.in);
		System.out.println("Please insert the key:");
		String key = parseStringKey(in);
		char [][] cipherTable = buildCipherTable(key);
		printTable(cipherTable);		// prints the table being used after inserting the key.
		CharTuple tuple = new CharTuple();
		CharTuple processedTuple = new CharTuple();
		int nextAsciiCode;
		char readCharacter;
		BufferedReader inputStream = null;
		PrintWriter outputStream = null;
		try{				
			inputStream =  new BufferedReader(new FileReader(argv[1]));
			outputStream = new PrintWriter(new FileOutputStream(argv[2]));
			while (((nextAsciiCode = inputStream.read()) != -1)){//it reads till the end of file, represented with a -1 by the bufferedReader
				readCharacter = (char) nextAsciiCode;//casting to char since bufferReader returns the ASCII  of what has been read.
				if (isLetter(readCharacter)) {
					tuple = addLetterToTuple(tuple, readCharacter);					
					if (tuple.isTupleFull()) {
						tuple = writeTupleToOutput(cipherTable, tuple, encryptionFlag, outputStream, processedTuple);					
					}
				}
			}
			inputStream.close();
			paddingLastTuple(tuple, processedTuple, outputStream, cipherTable, encryptionFlag);
			outputStream.close();				
		}catch(FileNotFoundException e){
			System.out.println(e.getMessage());
		}
		catch(IOException e){
			System.out.println(e.getMessage());
		}
	}

	private static void parseArguments(String[] argv){
		if(argv.length!=3){
			System.out.println("Usage: <--encrypt or --decrypt option> <input file> <output file>");
			System.exit(1);
		}
		if(!(argv[0].equals(ENCRYPTING_OPTION) || argv[0].equals(DECRYPTING_OPTION))) {
			System.out.println("Wrong command line option.");
			System.out.println("Usage: <--encrypt or --decrypt option> <input file> <output file>");
			System.exit(1);
		}
	}

	public static void main(String[] argv) {
		parseArguments(argv);
		new Playfair().start(argv); 
	} 
}
