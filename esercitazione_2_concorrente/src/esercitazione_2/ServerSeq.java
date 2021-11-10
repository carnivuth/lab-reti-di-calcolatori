package esercitazione_2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class ServerSeq {
	//porta di default in caso non venga inserito nessun parametro
	public static final int PORT=54321;
	public static final String PATH="./";
	
	public static void main(String[] args) {
		int port=-1; 
		ServerSocket socket=null;
		Socket clientSocket = null;
		DataInputStream inSock = null;
		DataOutputStream outSock = null;
		
		//controllo degli argomenti
		if (args.length>1) {
			System.err.println("Usage: java ServerSeq or java ServerSeq port");
			System.exit(1);
		}
		else if(args.length==1) {
			try {
				port=Integer.parseInt(args[0]);
			}
			catch(NumberFormatException e){
				System.err.println("Errore nell'inserimento della porta: deve essere un numero compreso tra 1024 e 65535");
				System.err.println("Usage: java ServerSeq or java ServerSeq port");
				System.exit(2);
			}
			if(port<1024 || port>65535) {
				System.err.println("Errore nell'inserimento della porta: deve essere un numero compreso tra 1024 e 65535");
				System.err.println("Usage: java ServerSeq or java ServerSeq port");
				System.exit(2);
			}
		}
		else port=PORT;
		
		
		//preparazione Socket
		try {
			socket=new ServerSocket(port);
		} catch (Exception e) {
			System.err.println("Errore nella creazione della socket: ");
			e.printStackTrace();
			System.exit(3);
		}
		
		try {
			socket.setReuseAddress(true);
		} catch (SocketException e) {
			System.err.println("Errore nel settare il ReuseAddress: ");
			e.printStackTrace();
			//chiudo la socket in ogni try catch prima di uscire
			try {
				socket.close();
			} catch (IOException e1) {
				System.err.println("Errore nella chiusura della socket");
				e1.printStackTrace();
				System.exit(3);
			}
			System.exit(3);
		}
		System.out.println("Server avviato, attendo...");
		
		
		//main loop
		while(true) {
			//creazione della socket
			try {
				clientSocket = socket.accept();
				//timeout altrimenti server sequenziale si sospende
				clientSocket.setSoTimeout(30000);
			}
			catch(SocketTimeoutException e) {
				System.err.println("Non ho ricevuto nulla dal client per 30 secondi"+System.lineSeparator()+"Interrompo la comunicazione e accetto nuove richieste.");
				// il server continua a fornire il servizio ricominciando dall'inizio
			}
			catch(SocketException e){
				System.err.println("Errore nell'inserimento del timeout");
				e.printStackTrace();
				System.exit(4);
			}
			catch(IOException e) {
				System.err.println("Errore nell'accettazione di una richiesta");
				e.printStackTrace();
				System.exit(4);
			}
			
			System.out.println("Connessione accettata: " + clientSocket + "\n");
			
			//preprazione di input e output della socket			
			try {
				inSock=new DataInputStream(clientSocket.getInputStream());
			}
			catch(IOException e) {
				System.err.println("Errore nella creazione del DataInputStream");
				e.printStackTrace();
				System.exit(6);
			}
			try {
				outSock = new DataOutputStream(clientSocket.getOutputStream());
			}
			catch(IOException e) {
				System.err.println("Errore nella creazione del DataOutputStream");
				e.printStackTrace();
				System.exit(6);
			}
			
			
			
			//dialogo con un client
			while(!clientSocket.isClosed()) {
							
				//recupero tutti i file dalla directory in cui li devo salvare
				File folder=new File(PATH);
				File[] files=folder.listFiles();
				if (files==null) {
					System.err.println("Errore nel recuperare i file nella directory corrente");
					System.exit(5);
				}
				
				
				//leggo il numero dei file che mi deve inviare
				int numFile=-1;
				try {
					numFile=inSock.readInt();
				} catch (Exception e1) {
					//System.err.println("Errore nella lettura del numero dei file");
					//e1.printStackTrace();
					//System.exit(6);
					try {
						clientSocket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					//chiudo la socket e continuo in modo da uscire dal ciclo in caso un client abbia terminato la sua esecuzione
					continue;
				}
				System.out.println("Devo ricevere "+numFile+" file");
				
				for(int i=0; i<numFile; i++) {
					//leggo il nome del file
					String nome=null;
					try {
						nome=inSock.readUTF();
						System.out.println("E' arrivato questo nome: "+nome);
					} catch (Exception e) {
						System.err.println("Errore nella lettura del nome del file");
						e.printStackTrace();
						System.exit(7);
					}
					
					
					//creo il file su cui scrivere e verifico se e' presente
					File file=null;
					try {
						if((file = new File(nome.trim())).createNewFile()) {
							//invio la risposta
							try {
								outSock.writeUTF("attiva");
							} catch (Exception e) {
								System.err.println("Errore nella scrittura di 'attiva'");
								e.printStackTrace();
								System.exit(8);
							}
							
							//attendo la dimensione del file
							long dim=-1;
							try {
								dim=inSock.readLong();
								System.out.println("Il suo peso e': "+dim);
							} catch (IOException e) {
								System.err.println("Errore nella ricezione della dimensione");
								e.printStackTrace();
								System.exit(9);
							}
							
							//preparo il file di output
							FileOutputStream out=null;
							try {
								out=new FileOutputStream(file);
							} catch (FileNotFoundException e) {
								System.err.println("Errore nella creazione del file di output");
								e.printStackTrace();
								System.exit(10);
							}
							
							
							//ricevo il file
							int buffer=-1;
							for(int j=0; j<dim; j++) {
								//leggo il byte
								try {
									buffer=inSock.read();
								} catch (IOException e) {
									System.err.println("Errore nella ricezione del byte "+i+" del file");
									e.printStackTrace();
									System.exit(11);
								}
								//scrivo sul file cio' che ho letto
								try {
									out.write(buffer);
								} catch (IOException e) {
									System.err.println("Errore nella scrittura su file del byte "+i);
									e.printStackTrace();
									System.exit(12);
								}
							}
							
							//chiudo il file
							try {
								out.close();
							} catch (IOException e) {
								System.err.println("Errore nella chiusura del file "+file.getName());
								e.printStackTrace();
								System.exit(13);
							}
							System.out.println("Ho ricevuto tutto il file "+file.getName());
												
							System.out.println("Attendo il prossimo file...");
							
						}
						else {
							//invio che ho gia' il file
							try {
								outSock.writeUTF("non attivare");
							} catch (Exception e) {
								System.err.println("Errore nella scrittura di 'non attivare'");
								e.printStackTrace();
								System.exit(8);
							}
							System.out.println("Attendo...");
						}
					} catch (IOException e) {
						System.err.println("Errore nella creazione del file "+nome);
						e.printStackTrace();
						System.exit(5);
					}
				}
			}
			//ho finito di trattare col client
			//chiusura della socket con il client
			try {
				clientSocket.close();
			} catch (IOException e) {
				System.err.println("Errore nella chiusura della socket col client");
				e.printStackTrace();
			}
		}
	}
}
