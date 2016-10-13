package filevault;

import java.io.File;
import java.util.Scanner;
import java.io.Console;
import javax.crypto.Cipher;

public class Filevault {
	
	private static final String encryptionExtension = ".encrypted";
	private static final String decryptionExtension = ".decrypted";
	private static final String exitCondition = "exit";
	private static final String helpCondition = "help";
	
//	in the case the file was saved with the .encypted extension
	private String removeEncryptedExtension(String s) {
		if ( (s.substring(s.length()-encryptionExtension.length(), s.length())).equals(encryptionExtension)) {
			s = s.substring(0, s.length()-encryptionExtension.length());
		}
		return s;
	}
	
	private void encryptFile(File inputFile, File vaultPath, String key) throws ParsingFileVaultException {
		File outputFile = new File (vaultPath.getAbsolutePath()+File.separator+inputFile.getName()+encryptionExtension);
		CryptoUtils.doCrypto(Cipher.ENCRYPT_MODE, key, inputFile, outputFile);
		System.out.println("File is now encrypted");
	}
	
	private void decryptFile(File inputFile, File vaultPath, String key) throws ParsingFileVaultException {
		inputFile = new File (vaultPath.getAbsolutePath()+File.separator+inputFile.getName());
		File outputFile = new File (vaultPath.getAbsolutePath()+File.separator+removeEncryptedExtension(inputFile.getName())+decryptionExtension);
		CryptoUtils.doCrypto(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
		System.out.println("File is now decrypted");
	}
	
	private void decryptFileWithDestination(File inputFile, File vaultPath, File outputFile, String key) throws ParsingFileVaultException {
		inputFile = new File (vaultPath.getAbsolutePath()+File.separator+inputFile.getName());
		outputFile = new File (outputFile.getAbsolutePath()+File.separator+removeEncryptedExtension(inputFile.getName())+decryptionExtension);
		CryptoUtils.doCrypto(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
		System.out.println("File is now decrypted");
	}
	
	private File checkFileVaultPathExist(String path) throws ParsingFileVaultException {
		if (! (new File(path).exists()) ) {
			throw new ParsingFileVaultException("The selected file vault does not exist.");
		}
		return new File(path);
	}
	
	private File createFileVault(String path) throws ParsingFileVaultException {
		File newDirectory = new File(path);
		try {
		newDirectory.mkdirs();
		return newDirectory;
		} catch(SecurityException e) {
			System.out.println(e.getMessage());
			throw new ParsingFileVaultException("Some error occurred and the directory was not created");
		}
	}
	
//	this method parses the user input regarding the selection/creation of the vault
	private File parseInput(String s) throws ParsingFileVaultException {
		String[] wordArray = s.split("\\s+");
		int wordCount = wordArray.length;
		File vault = null;
		if (s.equals(exitCondition)) {
			System.exit(0);
		}else if(s.equals(helpCondition)) {
			vaultSelectionHelp();
			throw new ParsingFileVaultException("");
		}else if (wordCount!=2) {
			throw new ParsingFileVaultException("The input string is not correct");
		}else if (wordArray[0].equals("cd")) {
			vault = checkFileVaultPathExist(wordArray[1]);
		}else if (wordArray[0].equals("mkdir")) {
			vault = createFileVault(wordArray[1]);
		}else {
			throw new ParsingFileVaultException("The input string is not correct");
		}
		return vault;
	}
	
	private void vaultSelectionHelp() {
		System.out.println("\nType 'exit' to exit the program or 'help' to display this menu");
		System.out.println("select or create a file vault: ");
		System.out.println("Usage: <option> <directoryPath>");
		System.out.println("Where <option> is 'cd' or 'mkdir'");
	}
	
	private File vaultSelection(Scanner readVaultInstructions) {
		String s;
		File vault;
		while (true) {
			try {
				s = readVaultInstructions.nextLine();
				vault = parseInput(s);
				break;
			} catch (ParsingFileVaultException e) {
				System.out.println(e.getMessage());
			}
		}
		return vault;
	}
	
	private void listFilesInVault(File vault) {
		File[] filesList = vault.listFiles();
		for (File file : filesList) {
			if (file.isFile()) {
				System.out.println(file.getName());
			}
		}
	}
	
	private String readPassword() throws ParsingFileVaultException{
		System.out.println("Insert the key with which you want to encrypt/decrypt the file using symmetric-key AES encryption");
		String password = "";
		String password2 = "";
		Console cons;
		char[] passwd;
		if ((cons = System.console()) != null && (passwd = cons.readPassword("[%s]", "Password:")) != null) {
			password = new String (passwd);		
		}
		System.out.println("Please insert password again to double check");
		if ((cons = System.console()) != null && (passwd = cons.readPassword("[%s]", "Password:")) != null) {
			password2 = new String (passwd);		
		}
		if (! password.equals(password2)) {
			throw new ParsingFileVaultException("the two passwords do no match, going back to the file selection");
		}
		return password;
	}

	private void parseFileInput(String s, File vault) throws ParsingFileVaultException {
		String[] wordArray = s.split("\\s+");
		int wordCount = wordArray.length;
		if (s.equals(exitCondition)) {
			System.exit(0);
		}else if(s.equals(helpCondition)) {
			fileSelectionHelp();
		}else if (s.equals("ls")) {
			listFilesInVault(vault);
		}else if (wordArray[0].equals("encrypt") && wordCount==2) {
			String password = readPassword();
			encryptFile(new File (wordArray[1]), vault, password);
		}else if (wordArray[0].equals("decrypt") && wordCount==2) {
			String password = readPassword();
			decryptFile(new File (wordArray[1]), vault, password);
		}else if (wordArray[0].equals("decrypt") && wordCount==3) {
			String password = readPassword();
			decryptFileWithDestination(new File (wordArray[1]), vault, new File (wordArray[2]),password);
		}else {
			throw new ParsingFileVaultException("The input string is not correct");
		}
	}
	
	private void fileSelectionHelp() {
		System.out.println("\nType 'exit' to exit the program and 'help' to display this menu");
		System.out.println("List the files in the vault, decrypt or encrypt a file");
		System.out.println("Usage: <option> <filePath>");
		System.out.println("Where <option> is 'encrypt' or 'decrypt' or 'ls'. 'encrypt' and 'decrypt' options have to be followed by a filePath");
		System.out.println("The path must be absolute for the files to be encrypted and relative to the vault for the ones to be decrypted");
		System.out.println("The decrypt option can also have a destination path of the decrypted files, if it doesn't the decrypted file destination will be the vault");
	}
	
	private void fileSelection(Scanner readFileInstructions, File vault) {
		String s;
		while (true) {
			try{	
				s = readFileInstructions.nextLine();
				parseFileInput(s, vault);
			}catch (ParsingFileVaultException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	public void start() {
		Scanner in = new Scanner(System.in);
//		First the user is asked to select or create a file vault
		vaultSelectionHelp();
		File vault = vaultSelection(in);
//		Then the user can add a file to the vault by encrypting it, decrypt a file of the vault and listing the contents of the vault.
		fileSelectionHelp();
		fileSelection(in, vault);
	}

	public static void main(String[] argv) {
		new Filevault().start();
	}
}
