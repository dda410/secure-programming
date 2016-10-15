package compareHashValues;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class CompareHashValues {

	private static final String HASH_ALGORITHM = "SHA-1";
	
	private static void parseArguments(String[] args) {
		if (args.length != 3) {
			System.out.println("Usage: CompareHashValues <Input File1> <Input File2> <Number of chunks>");
			System.exit(1);
		}
	}

	private static byte[] digestByteArray(byte[] chunk) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest sha = MessageDigest.getInstance(HASH_ALGORITHM);
		return sha.digest(chunk);
	}

	private static void numberOfChuncksBiggerThanFileBytes(int noOfChunks, int file1NoOfBytes, int file2NoOfBytes) {
		if (noOfChunks > file1NoOfBytes | noOfChunks > file2NoOfBytes){
			System.out.println("The file cannot be divided in "+noOfChunks+" since "
					+( (file1NoOfBytes < file2NoOfBytes)? "first file has "+file1NoOfBytes: "second file has "+file2NoOfBytes )+" bytes");
			System.exit(1);
		}
	}
	
	private static byte[] readChunk(byte[] buf, BufferedInputStream bufin) throws IOException {
		bufin.read(buf);
		return buf;
	}

	public static void main(String[] args) {
		parseArguments(args);		
		int numberOfChunks = Integer.parseInt(args[2]);
		File file1 = new File(args[0]);
		File file2 = new File(args[1]);
		int fileLength1 = (int) file1.length();
		int fileLength2 = (int) file2.length();
//		in the case the number of bytes of a file is smaller then the number of chunks argument then the file is not divisible and the program will exit.
		numberOfChuncksBiggerThanFileBytes(numberOfChunks, fileLength1, fileLength2);
		int chunkSize1 = fileLength1 / numberOfChunks;
		int chunkSize2 = fileLength2 / numberOfChunks;
//		in the case the files are not divisible by the number of chunks argument then the offset variables will contain the remaining bytes for both files
		int offset1 = fileLength1 % numberOfChunks;
		int offset2 = fileLength2 % numberOfChunks;
		byte[] buffer1 = new byte [chunkSize1];
		byte[] buffer2 = new byte [chunkSize2];
		try {
			BufferedInputStream bufin1 = new BufferedInputStream(new FileInputStream(file1));
			BufferedInputStream bufin2 = new BufferedInputStream(new FileInputStream(file2));
			for(int i = 0; i<numberOfChunks-1; i++) {
				System.out.print("Hash values of chunk number "+(i+1)+" match: ");
//				The chunk is read from both files. They are then digested and the hash values are compared. The match is printed to standard output
				System.out.println(Arrays.equals(digestByteArray(readChunk(buffer1, bufin1)), digestByteArray(readChunk(buffer2, bufin2))));
			}
			System.out.print("Hash values of chunk number "+numberOfChunks+" match: ");
//			The last chunk is read, it has a dedicated buffer since it contains also the bytes offset
			System.out.println(Arrays.equals(digestByteArray(readChunk(new byte [chunkSize1+offset1], bufin1)), digestByteArray(readChunk(new byte [chunkSize2+offset2], bufin2))));
			bufin1.close();
			bufin2.close();
		} catch (NoSuchAlgorithmException | IOException e) {
			System.out.println(e.getMessage());
			System.out.println("Some error occurred while reading/digesting the files. Aborting");
			e.printStackTrace();
			System.exit(1);
		}
	}
}
