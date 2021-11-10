package esercitazione_2;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientMultiplePut {
	
	public static int BUFF_DIM_C=1;
	public static int DEFAULT_MINIMUM_SIZE=0;
	public static int DEFAULT_PORT_C=0;
	public static String DEFAULT_SERVER_ADDRESS="";
	
	//parametri di invocazione [ip_server] [porta] [dimensione]
	public static void main(String args[]) { 
		
		clientSetup();
		int numFilesToSend=0;
		int port=DEFAULT_PORT_C;
		File [] files;
		byte buffer[];
		String directory=null;
		String address=null;
		BufferedReader cl=new BufferedReader(new InputStreamReader(System.in));
		DataInputStream sockReader=null;
		DataOutputStream sockWriter=null;
		DataInputStream fileReader=null;
		Socket socket = null;
		int readed=0;
		buffer =new byte[BUFF_DIM_C];
		int minimumSize=DEFAULT_MINIMUM_SIZE;
		
		//controllo argomenti e loro assegnazione (se presenti)
		if(args.length>3){
		
			System.err.println("Errore nell'inserimento delgi argomenti");
			System.out.println("Usage: java ClientMultiplePut IP port minimumSize");
			System.exit(1);
		}
		
		if(args.length>=1){
		
			address=args[0];
		}
		
		if(args.length>=2){
			
			try {
		
				port= Integer.parseInt(args[1]); 
				
			}catch(NumberFormatException e) {
				
				System.err.println("errore: inserire porta valida");
				System.exit(-1);
			}
	
			if(port<1024||port>65535) {
				
				System.err.println("errore: inserire porta valida");
				System.exit(-1);
			}
		}
		if(args.length==3) {

			try {
				
				minimumSize=Integer.parseInt(args[2]);
				
			}catch(NumberFormatException e) {
				
				System.err.println("errore: inserire dimensione minima valida");
				System.exit(-1);
			}
		}
		
		try {
			
			//creazione socket e estrazione stream di IO
			socket = new Socket();
			socket.connect(new InetSocketAddress(InetAddress.getByName(address), port));
			sockReader=new DataInputStream(socket.getInputStream());
			sockWriter=new DataOutputStream(socket.getOutputStream());
		
		} catch (UnknownHostException e1) {
			
			System.err.println("errore: inserire indirizzo di rete valido");
			System.exit(-1);
			e1.printStackTrace();
		
		} catch (IOException e1) {
		
			e1.printStackTrace();
		}
		
		try {
		
			System.out.println("inserire nome directory\n");
			
			while((directory=cl.readLine())!=null) {
				
				//caricamento lista di file presenti nella directory
				files=(new File(directory)).listFiles();
				
				//calcolo numero file con dimensione maggiore di dimensione soglia
				numFilesToSend=0;
				
				for (int i=0; i<files.length; i++){
				
					if(files[i].length()>=minimumSize && !files[i].isDirectory())numFilesToSend++;
				
				}
				
				//invio numero di file directory
				sockWriter.writeInt(numFilesToSend);
				
				
				//ciclo scrittura file directory 
				for(int i =0; i<files.length;i++) {
					
					if(files[i].length()>=minimumSize && !files[i].isDirectory()) {
					
						//invio nome file a servitore 
						System.out.println("invio nome file a servitore\n");
						sockWriter.writeUTF(files[i].getName());
						
						//controllo presenza o meno del file nel File System del servitore 
						if(sockReader.readUTF().equals("attiva")) {
					
							//apertura stream di lettura del file
							System.out.println("file mancante invio in corso\n");
							fileReader=new DataInputStream(new FileInputStream(files[i]));
							
							//invio dimensione del file
							sockWriter.writeLong(files[i].length());							
							
							//ciclo di scrittura file su socket
							readed=0;
							while((readed=fileReader.read(buffer))!=-1) {
								
								sockWriter.write(buffer,0,readed);
								
							}
						
						}else {
								
							//stampa messaggio di avviso presenza del file nel File System del servitore 
							System.out.println("file: "+ files[i].getName() +" gia presente nel File System del servitore ");
						}
						
					}
				}
				
			}
			socket.shutdownOutput();
			socket.close();
		
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		
		
	}
	
	//metodo di setup client
		private  static void clientSetup() {
			
			try {
				
				//lettura da file "settings\\cSettings.txt"
				BufferedReader buffer =new BufferedReader(new FileReader("settings\\cSettings.txt"));
				DEFAULT_SERVER_ADDRESS=buffer.readLine().split(":")[1].trim();
				BUFF_DIM_C=Integer.parseInt(buffer.readLine().split(":")[1].trim());
				DEFAULT_PORT_C=Integer.parseInt(buffer.readLine().split(":")[1].trim());
				DEFAULT_MINIMUM_SIZE=Integer.parseInt(buffer.readLine().split(":")[1].trim());
				buffer.close();
			
			} catch (FileNotFoundException e) {} catch (IOException e) {}
		}

}
