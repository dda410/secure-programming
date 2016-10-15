package generateSignature;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

public class GenerateSignature {
	
	private static final String KEY_ALGORITHM = "RSA";
	private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";
	
//	This method generates the private key by decoding it.
	private static PrivateKey decodePrivateKey(String privateKeyFile) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, IOException {
		FileInputStream keyfis = new FileInputStream(privateKeyFile);
		byte[] privEncKey = new byte[keyfis.available()];
		keyfis.read(privEncKey);
		keyfis.close();
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(privEncKey);
		PrivateKey p = keyFactory.generatePrivate(privKeySpec);
		return p; 	
	}
	
	private static void parseArguments(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: GenerateSignature <Input File> <privateKey File>");
            System.exit(1);
        }
	}
	
	private static Signature digestDataToBeSigned(String fileToBeSigned, Signature digitalSig) throws IOException, SignatureException {
		FileInputStream file = new FileInputStream(fileToBeSigned);			
		BufferedInputStream bufin = new BufferedInputStream(file);
		byte[] buffer = new byte[1024];
		int len;
		while ((len = bufin.read(buffer)) >= 0) {
			digitalSig.update(buffer, 0, len);
		}
		bufin.close();
		return digitalSig;
	}
	
	private static void writeSignatureToFile(byte[] sig, String fileToBeSigned) throws IOException {
		FileOutputStream sigfos = new FileOutputStream(fileToBeSigned, true);
		sigfos.write(sig);
		sigfos.close();
	}
	
	public static void main(String[] args) {
		parseArguments(args);
		try {
			Signature digitalSig = Signature.getInstance(SIGNATURE_ALGORITHM);
//			initialize digital signature with the private key
			digitalSig.initSign(decodePrivateKey(args[1]));
//			updates the Signature object with the data contained into the file to be signed.
			digitalSig = digestDataToBeSigned(args[0], digitalSig);			
			byte[] realSig = digitalSig.sign();
			writeSignatureToFile(realSig, args[0]);
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException | IOException | SignatureException | InvalidKeySpecException e) {
			System.out.println(e.getMessage());
			System.out.println("Some error occurred while signing the file. Aborting");
			System.exit(1);
		}
	}
}
