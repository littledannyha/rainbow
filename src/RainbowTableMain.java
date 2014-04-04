import java.util.Scanner;

public class RainbowTableMain {

	private static int NUM_CHAINS = 40000;
	private static int CHAIN_LENGTH = 10;
	
	public RainbowTableMain() {
		this(NUM_CHAINS, CHAIN_LENGTH);
	}
	
	public RainbowTableMain(int numChains, int chainLength) {
		RainbowTable rt = new RainbowTable(numChains, chainLength);
		
		Scanner sc = new Scanner(System.in);
		String curr = "";
		while((curr = sc.next()) != null){
			if(curr.length() == 4){
				System.out.println("hashing: " + curr + "\noutput: " + rt.hash(curr));
				System.out.println();
			}
			else{
				String pt = rt.lookup(curr);
				System.out.println(pt);
			}
			
		}
	}
	
	public static void main(String[] args) {
		if(args.length == 2) {
			new RainbowTableMain(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
		}
		else { 
			new RainbowTableMain();
		}
		
		
	}
}
