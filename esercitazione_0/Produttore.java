//package esercitazione0;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class Produttore {
	public static void main(String[] args) {		
		BufferedReader input = null;
		int res = 0;
		
		if (args.length != 1){
			System.out.println("Utilizzo: produttore <inputFilename>");
			System.exit(0);
		}
		
		input = new BufferedReader(new InputStreamReader(System.in));
			
		FileWriter fout;
		int val;
		try {
			fout = new FileWriter(args[0]);
			val = input.read();
			while(val != -1) {	
				fout.write(val);
				val = input.read();
			}		
			fout.close();
		} 
		catch (NumberFormatException nfe) { 
			nfe.printStackTrace(); 
			System.exit(1);
		}
	    catch (IOException e) { 
			e.printStackTrace();
			System.exit(2);
		}
	}
}

