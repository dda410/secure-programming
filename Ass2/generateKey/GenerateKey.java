package generateKey;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;

public class GenerateKey {
	
	private static final String KEY_ALGORITHM = "RSA";
	
	public static void main(String[] args) {
		try {
			KeyPairGenerator keyGen;
			keyGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
			keyGen.initialize(1024, random);
			KeyPair pair = keyGen.generateKeyPair();
			PrivateKey priv = pair.getPrivate();
			PublicKey pub = pair.getPublic();
			
			FileOutputStream fos = new FileOutputStream("publicKey");
		    fos.write(pub.getEncoded());
		    fos.close();
		    
		    fos = new FileOutputStream("privateKey");
		    fos.write(priv.getEncoded());
		    fos.close();
		} catch (NoSuchAlgorithmException | NoSuchProviderException | IOException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}
}
