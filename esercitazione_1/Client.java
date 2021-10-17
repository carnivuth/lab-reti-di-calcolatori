package Swap;

import java.io.*;
import java.net.*;

public class Client {
	public static void main(String[] args) {
		//riferimenti al server
		InetAddress addr = null;
		int port = -1;
		
		//controllo degli argomenti
		if (args.length!=3) {
			System.out.println("Usage: java LineClient serverIP serverPort fileName");
		    System.exit(1);
		}
		
		try {
			addr = InetAddress.getByName(args[0]);
		}
		catch (UnknownHostException e){
			System.out.println("Problemi nella determinazione dell'endpoint del server : ");
			e.printStackTrace();
			System.exit(2);
		}
		try {
			port = Integer.parseInt(args[1]);
		}
		catch(NumberFormatException e){
			System.out.println("Problemi nella determinazione della porta del server, il numero e' stato inserito in modo non corretto : ");
			e.printStackTrace();
			System.exit(3);
		}
		
		String fileName=args[2];
		
		
		/*try {
			if (args.length == 2) {
		    addr = InetAddress.getByName(args[0]);
		    port = Integer.parseInt(args[1]);
			} else {
				System.out.println("Usage: java LineClient serverIP serverPort");
			    System.exit(1);
			}
		} catch (UnknownHostException e) {
			System.out
		      .println("Problemi nella determinazione dell'endpoint del server : ");
			e.printStackTrace();
			System.out.println("LineClient: interrompo...");
			System.exit(2);
		}*/
	
		DatagramSocket socket = null;
		DatagramPacket packet = null;
		byte[] buf = new byte[256];
		
		// creazione della socket datagram, settaggio timeout di 30s
		// e creazione datagram packet
		try {
			socket = new DatagramSocket();
			socket.setSoTimeout(30000);
			packet = new DatagramPacket(buf, buf.length, addr, port);
			System.out.println("\nLineClient: avviato");
			System.out.println("Creata la socket: " + socket);
		} catch (SocketException e) {
			System.out.println("Problemi nella creazione della socket: ");
			e.printStackTrace();
			System.out.println("LineClient: interrompo...");
			System.exit(3);
		}

		
		//adesso devo chiedere al server a chi devo rivolgermi per il mio file
		//quindi creo prima uno stream di basso livello e poi uno di alto livello
		//in questo modo posso usare la writeUTF per scrivere la stringa
		//e poi averla gia' nello stream di basso livello pronta per la conversione in byteArray
		ByteArrayOutputStream boStream = new ByteArrayOutputStream();
		DataOutputStream doStream = new DataOutputStream(boStream);
		try {
			doStream.writeUTF(fileName);
		} catch (IOException e) {
			System.out.println("Errore nella scrittura del messaggio nel DataOutputStream: ");
			e.printStackTrace();
			System.exit(4);
		}
		byte[] data = boStream.toByteArray();
		//inserisco pacchetto e invio
		packet.setData(data);
		try {
			socket.send(packet);
		}catch(IOException e) {
			System.out.println("Errore Nell'invio del messaggio: ");
			e.printStackTrace();
			System.exit(5);
		}
		
		//parte di ricezione della risposta
		try {
			// settaggio del buffer di ricezione
			packet.setData(buf);
			socket.receive(packet);
			// sospensiva solo per i millisecondi indicati, dopodich solleva una
			// SocketException
		} catch (IOException e) {
			System.out.println("Problemi nella ricezione del datagramma con la nuova porta: ");
			e.printStackTrace();
			System.exit(6);
		}
		
		//decodifica del messaggio
		ByteArrayInputStream biStream = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());;
		DataInputStream diStream = new DataInputStream(biStream);
		String portaRowSwap=null;
		try {
			portaRowSwap = diStream.readUTF();
			System.out.println("Risposta: " + portaRowSwap);
		} catch (IOException e) {
			System.out.println("Problemi nella lettura della risposta con la nuova porta: ");
			e.printStackTrace();
			System.exit(6);
		}
		
		//controlli sulla risposta
		if (portaRowSwap.equals("file non presente")) {
			System.err.println("File non presente");
			System.exit(7);
		}
		
		try{
			packet.setPort(Integer.parseInt(portaRowSwap));
		}catch(NumberFormatException e) {
			System.out.println("Errore nella conversione della nuova porta in intero: ");
			e.printStackTrace();
			System.exit(8);
		}
		
		String firstLine=null;
		String secondLine=null;
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		//l'dea e' quella di tenerlo sempre nel ciclo
		//anche se c'e' un'eccezzione vorrei che le reiserisse da capo
		System.out.println("Inserire la prima riga da swappare: ");
		try {
			while((firstLine=stdIn.readLine())!=null) {
				System.out.println(System.lineSeparator()+"Inserire la seconda riga da swappare: ");
				secondLine=stdIn.readLine();
				
				System.out.println("Sono state inserite: "+firstLine+":"+secondLine);
				//preparazione del pacchetto
				//devo ricreare tutto perche' altrimenti si prende le cose vecchie
				buf=new byte[256];
				boStream = new ByteArrayOutputStream();
				doStream = new DataOutputStream(boStream);
				
				doStream.writeUTF(firstLine+":"+secondLine);
				buf = boStream.toByteArray();
				packet.setData(buf);
				socket.send(packet);
				
				
				//in fase di lettura devo di nuovo ricreare tutto altrimenti mi picchia
				buf=new byte[256];
				packet.setData(buf);
				socket.receive(packet);
				
				biStream = new ByteArrayInputStream( packet.getData(),0,packet.getLength());
				diStream = new DataInputStream(biStream);
				String risposta = diStream.readUTF();
				
				if (risposta.equals("riga non presente")) {
					System.err.println("Riga non presente");
				}
				
				System.out.println("Inserire la prima riga da swappare: ");
			}
		}
		catch(IOException e) {
			System.out.println("Errore nella lettura della linea, per favore reiserirle da capo");
		}

		/*try {
			//ByteArrayOutputStream boStream = null;
			//DataOutputStream doStream = null;
			byte[] data = null;
			String nomeFile = null;
			int numLinea = -1;
			String richiesta = null;
			String risposta = null;
			ByteArrayInputStream biStream = null;
			DataInputStream diStream = null;
		
			while ((nomeFile = stdIn.readLine()) != null) {
				// interazione con l'utente
				try {
					System.out.print("Numero della linea? ");
					numLinea = Integer.parseInt(stdIn.readLine());
					richiesta = nomeFile;
				} catch (Exception e) {
					System.out.println("Problemi nell'interazione da console: ");
					e.printStackTrace();
					System.out
						.print("\n^D(Unix)/^Z(Win)+invio per uscire, altrimenti inserisci nome file (con estensione): ");
					continue;
				}

				// riempimento e invio del pacchetto
				try {
					boStream = new ByteArrayOutputStream();
					doStream = new DataOutputStream(boStream);
					doStream.writeUTF(richiesta);
					data = boStream.toByteArray();
					packet.setData(data);
					socket.send(packet);
					System.out.println("Richiesta inviata a " + addr + ", " + port);
				} catch (IOException e) {
					System.out.println("Problemi nell'invio della richiesta: ");
					e.printStackTrace();
					System.out
				      .print("\n^D(Unix)/^Z(Win)+invio per uscire, altrimenti inserisci nome file (con estensione): ");
					continue;
					// il client continua l'esecuzione riprendendo dall'inizio del ciclo
				}

				try {
					// settaggio del buffer di ricezione
					packet.setData(buf);
					socket.receive(packet);
					// sospensiva solo per i millisecondi indicati, dopodich solleva una
					// SocketException
				} catch (IOException e) {
					System.out.println("Problemi nella ricezione del datagramma: ");
					e.printStackTrace();
					System.out
						.print("\n^D(Unix)/^Z(Win)+invio per uscire, altrimenti inserisci nome file (con estensione): ");
					continue;
					// il client continua l'esecuzione riprendendo dall'inizio del ciclo
				}
				try {
					biStream = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
					diStream = new DataInputStream(biStream);
					risposta = diStream.readUTF();
					System.out.println("Risposta: " + risposta);
				} catch (IOException e) {
					System.out.println("Problemi nella lettura della risposta: ");
					e.printStackTrace();
					System.out
				      .print("\n^D(Unix)/^Z(Win)+invio per uscire, altrimenti inserisci nome file (con estensione): ");
					continue;
					// il client continua l'esecuzione riprendendo dall'inizio del ciclo
				}
			
				// tutto ok, pronto per nuova richiesta
				System.out
			    	.print("\n^D(Unix)/^Z(Win)+invio per uscire, altrimenti inserisci nome file (con estensione): ");
			} // while
		}
		// qui catturo le eccezioni non catturate all'interno del while
		// in seguito alle quali il client termina l'esecuzione
		catch (Exception e) {
			e.printStackTrace();
		}*/

		System.out.println("LineClient: termino...");
		socket.close();

	}
}
