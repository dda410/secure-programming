package filevault;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
 
// A class used in order to encrypt or decrypt a file using the AES algorithm.

public class CryptoUtils {
    private static final String ENCRYPTION_ALGORITHM = "AES";
    
//    the key is always padded in order to be a 128 bit key for both encryption and decryption
    private static byte[] keyPadding(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
    	byte[] key = (password).getBytes("UTF-8");
    	MessageDigest sha = MessageDigest.getInstance("SHA-1");
    	key = sha.digest(key);
    	key = Arrays.copyOf(key, 16);
    	return key;
    }
 
    public static void doCrypto(int cipherMode, String key, File inputFile, File outputFile) throws ParsingFileVaultException {
        try {
//        	Setting the key and initializing the cipher
        	Key secretKey = new SecretKeySpec(keyPadding(key), ENCRYPTION_ALGORITHM);
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(cipherMode, secretKey);
// 			Reading the input file, the one it will be encrypted or decrypted        
            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);
//     		Setting the output file and writing to it the output       
            byte[] outputBytes = cipher.doFinal(inputBytes);
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);          
            inputStream.close();
            outputStream.close();            
        } catch (NoSuchPaddingException | NoSuchAlgorithmException
                | InvalidKeyException | BadPaddingException
                | IllegalBlockSizeException | IOException e) {
        	System.out.println(e.getMessage());
        	throw new ParsingFileVaultException("Some error occurred while encrypting/decrypting the file.");
        }
    }
}