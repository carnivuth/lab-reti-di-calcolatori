package esercitazione_2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMultiplePut {
	public static void main(String args[]) {
		
		ServerSocket sv;
		Socket socket;
		ClientHandler ch;
		int port=0;
		
		//controllo argomenti 
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
		
		try {
			
			sv=new ServerSocket(port);
		
			//ciclo demone
			while(true) {
				
				//chiamata processo di gestione figlio
				socket=sv.accept();
				ch=new ClientHandler(socket);
				ch.start();
				
			}
		
		} catch (IOException e) {
		
			e.printStackTrace();
		}
		
		
		
	}
	
}
