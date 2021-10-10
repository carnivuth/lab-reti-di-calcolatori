//package esercitazione0;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

public class Consumatore {
	public static void main(String[] args) {
		int val;
		char valChar;
		
		File inputFile;
		//creazione del riferimento allo stream di input (unificazione dei due casi)
		InputStream newInput = System.in;
		
		//controllo argomenti
		if (args.length == 2){
			//senza ridirezione
			try {
				inputFile = new File(args[1]);
			    newInput = new FileInputStream(inputFile);
			} catch(FileNotFoundException e){
				System.out.println("File non trovato");
				System.exit(1);
			}
		}else if(args.length != 1) {
			System.err.println("Argomenti non validi");
			System.exit(2);
		}
	  
		//main loop
		try {
			
			while ((val = newInput.read()) >= 0) { 
				
				if (args[0].indexOf(val) == -1) {
					valChar = (char) val;
					System.out.print(valChar);
				}
			}
		} catch(IOException ex){
			System.out.println("Errore di input");
			System.exit(2);
		}
	}
}
