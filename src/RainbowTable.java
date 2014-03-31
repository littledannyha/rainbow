import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;


public class RainbowTable {
	
	// This is the data structure to use for the rainbow table, mapping end of chains to beginning of chains.
	Map<String, String> lastToFirst;
	
	public MessageDigest message_digest = null;
	BigInteger bi;

	byte[] res;
	final int NUM_CHARACTERS = 4;
	char[] characters = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
	BigInteger passwordSpaceSize;
	BigInteger charactersLength;
	
	int chainLength;
	int numRows;
	
	public RainbowTable(int numChains, int chainLength) {
		this.numRows = numChains;
		this.chainLength = chainLength;

		passwordSpaceSize = new BigInteger(""+ ((int)Math.pow(characters.length, NUM_CHARACTERS)));
		charactersLength = new BigInteger(""+characters.length);
		
		try {
			message_digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}				
		
		String filename = "table" + numChains+"x"+chainLength+".txt";
		if(!readFromFile(filename)) {
			long startTime = System.currentTimeMillis();
			buildTable();
			System.out.println("Building table took: " + ((System.currentTimeMillis()-startTime)/1000) + " s");
			writeToFile(filename);
		}
		
	}
	
	private void buildTable() {
		// TODO: Build the rainbow table
	}
	
	/**
	 * Looks up a known password by lookup up its hash value.
	 * Useful for testing the table.
	 */
	
	public String lookupPwd(String pwd) {
		return lookup(hash(pwd));
	}
	
	/**
	 * Note, this is not truly a "random" string. It simply reduces the given string to
	 * a password and looks up that password.
	 */
	public String lookupRandomString(String randomString) {
		return lookup(reduce(randomString, 0));
	}
	
	public String lookup(String hash) {
		//TODO: Lookup a given hash in the rainbow table.
		// Return null if the password is not found
		return null;
	}

	/**
	 * Returns the String representation of the hash of the given password.
	 * message_digest is initialized to md5, so this will be the md5 hash if 
	 * nothing is changed.
	 */
	private String hash(String passwd) {
		try {
			res = message_digest.digest(passwd.getBytes("US-ASCII"));
			bi = new BigInteger(1, res);
			return String.format("%0" + (res.length * 2) + "x", bi);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	

	/**
	 * This method reduces a hash to a password in the password space.
	 * The method will (likely) return a different password depending on the subscript passed in.
	 * It is deterministic (i.e. same input will lead to the same output).
	 */
	private String reduce(String hash, int subscript) {
		try {
			hash += subscript;
			res = message_digest.digest(hash.getBytes("US-ASCII"));
			bi = (new BigInteger(res)).mod(passwordSpaceSize);
			StringBuilder pwd = new StringBuilder();
			for(int i=0; i<NUM_CHARACTERS; i++) {
				pwd.append(characters[bi.mod(charactersLength).intValue()]);
				bi = bi.divide(charactersLength);
			}
			return pwd.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * This method simply writes the lastToFirst map to a file so it can be read later.
	 * The file just has each key/value pair on its own line
	 * 
	 */
	private void writeToFile(String filename) {
		File f = new File(filename);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(f));
			for (String key : lastToFirst.keySet()) {
				bw.write(key + " " + lastToFirst.get(key) + "\n");
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method reads a file of key/value pairs into the lastToFirst map.
	 * 
	 */
	private boolean readFromFile(String filename) {
		File f = new File(filename);
		if (!f.exists()) {
			System.out.println("File does not exist yet, building file");
			return false;
		}
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line = null;
			String[] pair;
			while ((line = br.readLine()) != null) {
				pair = line.split("\\s");
				if (pair.length != 2) {
					System.err.println("Invalid format in table file: " + line);
					continue;
				}
				lastToFirst.put(pair[0], pair[1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
}