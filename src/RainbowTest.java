import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;

import org.junit.Ignore;
import org.junit.Test;

public class RainbowTest {
	public static int NUMCHAINS = 1000;
	public static int CHAINLENGTH = 200;
	
	@Ignore
	@Test
	public void BuildChainLength2(){
		int i = 2;
		out("Chain length: %s",i);
		RainbowTable r = normalTable(i);
		for (String end : r.lastToFirst.keySet()) {
			out("End: %s",end);
			
			String start = r.lastToFirst.get(end);
			out("Start: %s",start);
			String target = start;
			target = r.hash(target);
			out("Hashed Start: %s", target);
			target = r.reduce(target, 0);
			out("Second plaintext: %s",target);
			out("Second hash: %s",r.hash(target));
			out("");
//			this.printHash(r);
			assertTrue(r.hash(target).equals(end));
		}
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
		return out.iterator();
	}
	
	@Test
	public void howAccurate(){
		
		int allNum = 26*26*26*26;
		int accurate = 0;
		
		int totalTime = 0;
		
		Iterator<String> itr = allFourLong();
		long genTime = System.currentTimeMillis();
		RainbowTable rt = normalTable(500000,3);
		System.out.println("generation took: " + ((System.currentTimeMillis()-genTime)) + " ms");
		
		int i = 0;
		while(itr.hasNext()){
			String curr = itr.next();
			String hash = rt.hash(curr);

			long startTime = System.currentTimeMillis();
			String lookup = rt.lookup(hash);
			long duration = System.currentTimeMillis()-startTime;
			totalTime += duration;
			out("Curr: %s -> Found: %s",curr,lookup);
//			assertTrue(lookup.equals(curr));
			if(lookup.equals(curr)){
				accurate += 1;
			}
			i++;
		}
		
		out("Average lookup(ms): %f\nAccuracy: %f",(double)totalTime/allNum,(double)accurate/allNum);
		

		
	}
	@Ignore
	@Test
	public void BuildChainLengthx(){
		int i = 5;
		
		out("Chain length: %s",i);
		RainbowTable r = normalTable(i);
		for (String end : r.lastToFirst.keySet()) {
			out("End: %s",end);
			
			String start = r.lastToFirst.get(end);
			out("Start: %s",start);
			String target = start;
			target = r.hash(target);
			out("Hashed Start: %s", target);
			for(int j = 0; j < i-1; j++){
				target = r.reduce(target, j);
				out("%d plaintext: %s",j,target);
				target = r.hash(target);
				out("%d hash: %s",j,target);
				
			}
			out("");
//			this.printHash(r);
			assertTrue(target.equals(end));
		}
	}
	
	@Ignore
	@Test 
	public void dictlookup(){
		RainbowTable r = Dictionary();
		for(String s: r.lastToFirst.keySet()){
			assertTrue(s.equals(r.hash(r.lookup(s))));
		}
	}
	
	@Ignore
	@Test 
	public void tablelookup(){
		int count = 0;
		RainbowTable r = normalTable(1,100);
		for(String end: r.lastToFirst.keySet()){
			String toTest = r.hash(r.lookup(end));
			assertTrue(end.equals(toTest));
			
			String start = r.lastToFirst.get(end);
			int i = 0;
			while(!r.hash(start).equals(end)){
				String target = r.hash(start);
				assertTrue(r.lookup(target).equals(start));
				String nextPlaintext = r.reduce(target, i);
				i++;
				count++;
				out(count);
				out("Plaintext: %s -> Cipher: %s",start,target);
				out("Cipher: %s -> Plaintext: %s",target,nextPlaintext);
				start = nextPlaintext;				
			}
//			assertTrue(start.equals(target));

		}
	}
	@Ignore
	@Test 
	public void simpletablelookup(){
		int count = 0;
		RainbowTable r = normalTable(1,3);
		String lastHash = r.lastToFirst.keySet().iterator().next();
		String firstPlain = r.lastToFirst.get(lastHash);
		String firstC = r.hash(firstPlain);
		String secondP = r.reduce(firstC, 0);
		String secondC = r.hash(secondP);
		String lastP = r.reduce(secondC, 1);
		out(firstPlain);
		out(firstC);
		out(secondP);
		out(secondC);
		out(lastP);
		out(lastHash);
		
//		Scanner sc = new Scanner(System.in);
//		String curr = "";
//		while((curr = sc.next()) != null){
//			if(curr.length() >= 4){
////				System.out.println("hashing: " + curr + "\noutput: " + r.hash(curr));
//				System.out.println("reducing: " + curr + "\noutput: " + r.reduce(curr,0));
//				System.out.println();
//			}
//			else{
//				long startTime = System.currentTimeMillis();
//				String pt = r.lookup(curr);
//				System.out.println("lookup took: " + ((System.currentTimeMillis()-startTime)) + " ms");
//
//				System.out.println(pt);
//			}
//			
//		}
		
		
		assertTrue(r.lookup(firstC).equals(firstPlain));
		assertTrue(r.lookup(secondC).equals(secondP));
		assertTrue(r.lookup(lastHash).equals(lastP));
		
	}
	
	@Ignore
	@Test 
	public void dictlookup2(){
		RainbowTable r = normalTable();
		for(String s: r.lastToFirst.keySet()){
			assertTrue(s.equals(r.hash(r.lookup(s))));
		}
	}
	@Ignore
	@Test
	public void BuildChainLength3(){
		int i = 3;
		out("Chain length: %s",i);
		RainbowTable r = normalTable(i);
		for (String end : r.lastToFirst.keySet()) {
			out("End: %s",end);
			
			String start = r.lastToFirst.get(end);
			out("Start: %s",start);
			String target = start;
			target = r.hash(target);
			out("Hashed Start: %s", target);
			target = r.reduce(target, 0);
			out("Second plaintext: %s",target);
			target = r.hash(target);
			out("Second hash: %s",r.hash(target));
			target = r.reduce(target, 1);
			out("Third plaintext: %s", target);
			
			
			out("");
//			this.printHash(r);
			assertTrue(r.hash(target).equals(end));
		}
	}
	
	
	@Ignore
	@Test
	public void BuildDictionaryTest() {
		RainbowTable r = Dictionary();
		for (String end : r.lastToFirst.keySet()) {
			String start = r.lastToFirst.get(end);
			assertTrue(r.hash(start).equals(end));
			
		}
	}

	public static RainbowTable Dictionary(){
		return new RainbowTable(NUMCHAINS,1);
	}
	
	public static RainbowTable normalTable(){
		return new RainbowTable(NUMCHAINS, CHAINLENGTH);
	}

	public static RainbowTable normalTable(int length){
		return new RainbowTable(NUMCHAINS, length);
	}
	

	public static RainbowTable normalTable(int numChains,int length){
		return new RainbowTable(numChains, length);
	}
	
	public void out(String s,Object... o){
		System.out.println(String.format(s, o));
	}
	
	public void out(Object s){
		System.out.println(s);
	}
	
	public void printHash(RainbowTable r){
		try(Scanner s = new Scanner(System.in)){
		String next = "";
		while((next = s.nextLine()) != null && next.length() > 0){
			out("Hashed %s to:\n%s",next,r.hash(next));
		}
		}
		catch(Exception e){
			out("exception");
		}
	
	}
}
