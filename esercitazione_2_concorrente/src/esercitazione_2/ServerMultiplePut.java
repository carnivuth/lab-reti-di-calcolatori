package esercitazione_2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMultiplePut {
	
	//dichiarazione parametri
	public static String PATH_TO_OUTPUT="output\\";
	public static int BUFF_DIM_S=64000;
	public static int DEFAULT_PORT=50000;
	
	
	//parametri di invocazione [porta]
	public static void main(String args[]) {
		
		//server setup
		serverSetup();
		ServerSocket sv;
		Socket socket;
		ClientHandler ch;
		int port=DEFAULT_PORT;
		
		//controllo argomenti e loro assegnazione (se presenti)
		if(args.length>1){
			
			System.err.println("Errore nell'inserimento delgi argomenti");
			System.out.println("Usage: java serverMultiplePut port");
			System.exit(1);
		}
		if(args.length==1) {
			
			try {
				
				port= Integer.parseInt(args[0]); 
				
			}catch(NumberFormatException e) {
				
				System.err.println("errore: inserire porta valida");
				System.exit(-1);
			}
			
			if(port<1024||port>65535) {
				
				System.err.println("errore: inserire porta valida");
				System.exit(-1);
			}
		}
	
		try {
			
			sv=new ServerSocket(port);
		
			//ciclo demone
			while(true) {
				
				System.out.println("attesa client\n");
				socket=sv.accept();
				//chiamata processo di gestione figlio
				ch=new ClientHandler(socket);
				ch.start();
				
			}
		
		} catch (IOException e) {
		
			e.printStackTrace();
		}
		
		
		
	}
	
	//metodo di setup server
	private  static void serverSetup() {
		
		try {
			
			//lettura da file "settings\\sSettings.txt"
			BufferedReader buffer =new BufferedReader(new FileReader("settings\\sSettings.txt"));
			PATH_TO_OUTPUT=buffer.readLine().split(":")[1].trim();
			BUFF_DIM_S=Integer.parseInt(buffer.readLine().split(":")[1].trim());
			DEFAULT_PORT=Integer.parseInt(buffer.readLine().split(":")[1].trim());
			buffer.close();
		
		} catch (FileNotFoundException e) {} catch (IOException e) {}
	}
	
	
}
