import static org.junit.Assert.assertTrue;

import java.util.Scanner;

import org.junit.Ignore;
import org.junit.Test;

public class RainbowTest {
	public static int NUMCHAINS = 50;
	public static int CHAINLENGTH = 2;
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
	
	public void out(String s,Object... o){
		System.out.println(String.format(s, o));
	}
	
	public void out(String s){
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
