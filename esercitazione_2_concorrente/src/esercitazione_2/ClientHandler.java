package esercitazione_2;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler extends Thread {
	
	//dichiarazione dimensione buffer
	
	private Socket socket;
	private DataOutputStream fileWriter;
	private DataInputStream sockReader;
	private DataOutputStream sockWriter;
	
	public ClientHandler(Socket socket) {
		
		this.socket=socket;
		
		try {
			
			//inizializzazione canali di comunicazione socket 
			sockReader=new DataInputStream(socket.getInputStream());
			sockWriter=new DataOutputStream(socket.getOutputStream());
		
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		
	}
	
	public void run() {
		
		long fileLength;
		int numFiles=0;
		String fileName;
		File file;
		byte buffer[]= new byte[ServerMultiplePut.BUFF_DIM_S];
		int readed=0;
		String directory="";
		
		//algoritmica implementazione scambio file
		try {
			
			while(!socket.isInputShutdown()) {
				
				System.out.println("leggo nome e numero file cartella\n");
				
				try {
					directory= sockReader.readUTF();
					numFiles = sockReader.readInt();
				
				}catch(EOFException e) {
					
					//terminazione in caso di EOF inviato da client
					System.out.println("comunicazione terminata\n");
					socket.close();
					return;
				}
				
				try {
				System.out.println(directory+(new File(directory.trim())).mkdir());
				}catch(SecurityException e) {e.printStackTrace();}
				
				for(int i =0; i<numFiles;i++) {
					
					
					fileName = sockReader.readUTF();
					System.out.println("file di nome "+fileName+" letto\n");
					
					//controllo esistenza file nel File System del servitore
					System.out.println(ServerMultiplePut.PATH_TO_OUTPUT+File.separator+directory+File.separator+fileName.trim());
					if((file = new File(ServerMultiplePut.PATH_TO_OUTPUT+File.separator+fileName.trim())).createNewFile()) {
						
						//richiesta file al cliente
						System.out.println("richiedo file\n");
						sockWriter.writeUTF("attiva");
						fileLength=sockReader.readLong();
						fileWriter=new DataOutputStream(new FileOutputStream(file));
						
						//ciclo di lettura/scrittura file
						System.out.println("scrivo file "+fileName+" \n");
						long timeStamp=System.currentTimeMillis();
						
						for(int j=0; j<fileLength;j+=readed) {
							
							readed=sockReader.read(buffer,0,(int)((fileLength-j>=ServerMultiplePut.BUFF_DIM_S)?ServerMultiplePut.BUFF_DIM_S:fileLength-j));
							fileWriter.write(buffer,0,readed);
							
						}
						
						readed=0;
						fileWriter.close();
						System.out.println(System.currentTimeMillis()-timeStamp);
						
					}else {
						
						//segnalazione al cliente dell'esistenza del file nel File System del servitore
						sockWriter.writeUTF("salta");
					}
				}
		
			}
			
			socket.close();
			
		} catch (IOException e) {
			
			try {
				
				socket.close();
			
			} catch (IOException e1) {
				
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		
	}
	
}
