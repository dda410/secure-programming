package verificateSignature;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class VerificateSignature {
	
	private static final int SIGNATURE_LENGTH = 128;
	private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";

	private static void parseArguments(String[] args) {
		if (args.length != 2) {
			System.out.println("Usage: VerificateSignature <Input File_Signed> <publicKeyFile>");
			System.exit(1);
		}
	}

	private static PublicKey decodePublicKey(String publicKeyFile) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
		FileInputStream keyfis = new FileInputStream(publicKeyFile);
		byte[] encKey = new byte[keyfis.available()];  
		keyfis.read(encKey);
		keyfis.close();
		X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(encKey);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
		return pubKey;
	}
	
	private static byte[] readSignature(String signatureFile) throws IOException {
		byte[] sigToVerify = new byte[SIGNATURE_LENGTH];
		File file = new File(signatureFile);
		RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
		randomAccessFile.seek(file.length() - SIGNATURE_LENGTH); 
		randomAccessFile.read(sigToVerify, 0, SIGNATURE_LENGTH);
		randomAccessFile.close();
		return sigToVerify;
	}
	
	private static void removeSignatureFromFile(String filePath) throws IOException {
		File file = new File(filePath);
		FileChannel outChan = new FileOutputStream(file, true).getChannel();
	    outChan.truncate(file.length()-SIGNATURE_LENGTH);
	    outChan.close();
	}
	
	private static Signature digestDataToBeSigned2(String fileToBeSigned, Signature digitalSig) throws IOException, SignatureException {
		int fileBytes = (int) (new File(fileToBeSigned).length());
		FileInputStream file = new FileInputStream(fileToBeSigned);			
		BufferedInputStream bufin = new BufferedInputStream(file);
		byte[] buffer = new byte[1024];
		int numberOfReads = fileBytes / buffer.length;
		int len;		
		for (int i = 0; i < numberOfReads; i++) {
			len = bufin.read(buffer);
			digitalSig.update(buffer, 0, len);
		}
		int offset;
//		This if-statement reads the last bytes from the file before the signature.
		if ((offset = (fileBytes % buffer.length) - SIGNATURE_LENGTH) > 0) {
			buffer = new byte[offset];
			len = bufin.read(buffer);
			digitalSig.update(buffer, 0, len);
		}
		bufin.close();
		return digitalSig;
	}

	public static void main(String[] args) throws InvalidKeyException, SignatureException {
		parseArguments(args);
		try{
//			the signature to be verified is read from the file
			byte[] signatureToVerify = readSignature(args[0]);
//			A new signature object is created in order to calculate a signature of the data file and then compare with the provided one
			Signature sig = Signature.getInstance(SIGNATURE_ALGORITHM);
//			initialized digital signature with the public key decoded
			sig.initVerify(decodePublicKey(args[1]));
			sig = digestDataToBeSigned2(args[0], sig);
			boolean verifies = sig.verify(signatureToVerify);
			if (verifies) {
				System.out.println("The file is authentic.");
//				The signature is removed from the file since the file is authentic
				removeSignatureFromFile(args[0]);
			} else {
				System.out.println("The file is not authentic.");
			}
			
		}catch (IOException | NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException e){
			System.out.println(e.getMessage());
			System.out.println("Some error occurred while signing the file. Aborting");
			System.exit(1);
		}
	}
}
