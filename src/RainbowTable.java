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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;


public class RainbowTable {
	
	// This is the data structure to use for the rainbow table, mapping end of chains to beginning of chains.
	Map<String, String> lastToFirst;
	
	public MessageDigest message_digest = null;
	BigInteger bi;
	final int NUM_THREADS = 20;
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
		this.lastToFirst = new HashMap<>();

		String filename = "table" + numChains+"x"+chainLength+".txt";
		if(!readFromFile(filename)) {
			long startTime = System.currentTimeMillis();
			buildTable();
			System.out.println("Building table took: " + ((System.currentTimeMillis()-startTime)/1000) + " s");
			writeToFile(filename);
		}
		else{
			out("table exists");
		}
		
	}
	
	
	
	private void buildTable() {
		// TODO: Build the rainbow table
		Iterator<String> all = this.allFourLong();
		
		for (int i = 0; i < numRows; i++) {
			out("row %d",i);
			String first = all.hasNext()?all.next():null;
			if(first==null){
				return;
			}
			String last = first.substring(0);
			//hash and reduce chainlength times
			//first reduction subscript is 0
//			out("plaintext 0: %s",first);

			for(int j = 0; j < chainLength-1;j++){
				last = this.hash(last);
//				out("ciphertext %d:%s",j,last);
				last = this.reduce(last, j);
//				out("plaintext %d:%s",j+1,last);

			}
//			out("last ciphertext: %s",this.hash(last));
//			hash last one more time because last is currently a plaintext
			this.lastToFirst.put(this.hash(last), first);
		}
		out("number of entries:" + lastToFirst.size());
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
	
	
//	
//	public String lookup(String hash){
//		String s = hash;
//		int numReductions = 0;
//		
//		if(!lastToFirst.containsKey(hash)){
//			for(; numReductions < this.chainLength-1;numReductions++){
//				s = hash.substring(0);
//
//				s = reduce(s,numReductions);
//				s = hash(s);
////				if the reduction wasn't found, continue searching
//				if(lastToFirst.containsKey(s)){
//					break;
//				}
//			}
//		}
//
//		
//		if(!lastToFirst.containsKey(s)){
//			return null;
//		}
////		if reduction was found, start searching
//		else{
//			String start = lastToFirst.get(s);
//			for(int i = 0; i < chainLength - numReductions - 1; i++){
//				start = hash(start);
//				start = reduce(start,i);
//			}
//			return start;
//		}
//		
//	}
	public String lookup(String hash) {
		long startTime = System.currentTimeMillis();
		
		//TODO: Lookup a given hash in the rainbow table.
		// Return null if the password is not found
		ExecutorService t = java.util.concurrent.Executors.newFixedThreadPool(1);
		ConcurrentHashMap<String,Boolean> c = new ConcurrentHashMap<>();
		Set<String> outputs = new HashSet<String>();
//		for each possible depth up to chain length
		for (int i = 0; i < this.chainLength; i++) {
			t.execute(new Runnable(){
				private String hash;
				private int i;
				private Set<String> outputs;
				@Override
				public void run() {
					// TODO Auto-generated method stub
					//already found
					if(!outputs.isEmpty()){
						return;
					}
					else{
						lookupDepth(hash, i, outputs);
					}

				}
				
				public Runnable init(String hash,int i, Set<String> outputs){
					this.hash = hash;
					this.i = i;
					this.outputs = outputs;
					
					return this;
				};
				
			}.init(hash, i, outputs));
			
			
			
		}
		t.shutdown();
		try {
			t.awaitTermination(3, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("AWAIT EXCEPTION");
			System.exit(0);
		}
		if(outputs.isEmpty()){
			out("search for %s returned null",hash);
			return null;
		}
		else{
			String out = outputs.iterator().next();
			out("search for %s returned %s",hash,out);

			return out;
		}
		
		
		
//		
////		NONTHREADED VERSION
//		
//		//TODO: Lookup a given hash in the rainbow table.
//				// Return null if the password is not found
//
//				Set<String> outputs = new HashSet<String>();
////				for each possible depth up to chain length
//				for (int distanceFromLast = 0; distanceFromLast < this.chainLength; distanceFromLast++) {
//					this.lookupDepth(hash, distanceFromLast, outputs);
//					if(!outputs.isEmpty()){
//						break;
//					}
//				}
//				if(outputs.isEmpty()){
//					System.out.println("return null");
//					return null;
//				}
//				else{
//					String out = outputs.iterator().next();
//					System.out.println("lookup: " + hash + "\noutput: " + out);
//					return out;
//				}

		
	}
	
	/**
	 * Looks up the hash for a certain depth inside each of the chains
	 * @param hash 
	 * 	the hash to be reversed
	 * @param depth
	 * 	how far from the end of the chain we're searching 
	 * @return
	 * 	The Plaintext of the hash if it exists at that depth in the table, or null if not found
	 */	
	public void lookupDepth(String hash,int depth,Set<String> set){
		String s = hash;
		for(int i = 0;i<depth;i++){
			int reductionLevel = this.chainLength-depth+i-1;
			s = this.reduce(s, reductionLevel);
			s = this.hash(s);
		}
		if(!this.lastToFirst.containsKey(s)){
			return;
		}
		else{
			String start = this.lastToFirst.get(s);
			
			for(int i = 0; i < this.chainLength - depth - 1; i++){
				start = this.hash(start);
				
				start = this.reduce(start, i);
			}
			synchronized(set){
				set.add(start);
			}
			return;
		}
			
	}

	/**
	 * Returns the String representation of the hash of the given password.
	 * message_digest is initialized to md5, so this will be the md5 hash if 
	 * nothing is changed.
	 */
	public synchronized String hash(String passwd) {
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
	protected synchronized String reduce(String hash, int subscript) {
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
	
	private String randomPass(){
		StringBuffer s = new StringBuffer();
		for(int i = 0; i < this.NUM_CHARACTERS;i++){
			int index = (int)(Math.random() * this.characters.length);
			s.append(this.characters[index]);
		}
		return s.toString();
	}
	
	private void out(Object o){
		System.out.println(o);
	}
	
	private void out(String s, Object... o){
		System.out.println(String.format(s, o));
	}
	
	public Iterator<String> allFourLong(){
		HashSet<String> out = new HashSet<String>();
		char[] ch = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
		int total = 26*26*26*26;
		for(int i = 0; i < total; i++){
			StringBuffer currString = new StringBuffer();
			
				int a = i%26;
				int b = (i/26)%26;
				int c = (i/(26*26))%26;
				int d = (i/(26*26*26))%26;
				currString.append(ch[a]);
				currString.append(ch[b]);
				currString.append(ch[c]);
				currString.append(ch[d]);
				out.add(currString.toString());
		}
		out(out.size());
		return out.iterator();
	}
}
