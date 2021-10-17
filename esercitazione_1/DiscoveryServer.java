package Swap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class DiscoveryServer {

	final static String COMPLETE_PATH = "D:\\OneDrive - Alma Mater Studiorum Universita' di Bologna\\Sync\\Reti_Calcolatori\\Java\\Esercitazione1\\src\\";

	public static void main(String[] args) {
		
		//java DiscoveryServer PORTA nomefile1.txt PORTA1 ...
		
		//controllo argomenti
		if (args.length < 3 || args.length % 2 == 0) {
			System.err.println("argomenti non validi");
			System.exit(1);
		}
		File chkFile;
		for (int i=0; i<((args.length)-1)/2; i++) {
			if (!args[2*i + 2].matches("\\d+")) {
				System.err.println("La porta del " + i + " file non e' un numero");
				System.exit(4);
			}
			chkFile = new File(COMPLETE_PATH+args[2*i + 1]);
			if (!chkFile.exists()) {
				System.err.println("Il " + i + " file non esiste");
				System.exit(5);
			}
		}
		for (int i=0; i<((args.length - 1)/2); i++) {
			for (int j=0; j<((args.length - 1)/2); j++) {
				if (i!=j && args[2*i + 2].equals(args[2*j + 2])){
					System.err.println("Porte duplicate");
					System.exit(6);
				}
			}
		}
		
		try {
			//setup
			//metto il server in ascolto sulla porta
			// ecreo il pacchetto
			DatagramSocket sock = new DatagramSocket(Integer.parseInt(args[0]));
			byte[] buff = new byte[256];
			DatagramPacket packet = new DatagramPacket(buff, buff.length);
			
			ByteArrayOutputStream boStream = new ByteArrayOutputStream();
			DataOutputStream doStream = new DataOutputStream(boStream);
			ByteArrayInputStream biStream = new ByteArrayInputStream(buff);
			DataInputStream diStream = new DataInputStream(biStream);
			
			//creazione struttura file-porta e thread
			String[] files = new String[(args.length-1)/2];
			int[] ports = new int[(args.length-1)/2];
			RowSwapThread[] threads = new RowSwapThread[(args.length-1)/2];
			
			for(int i=0; i<(args.length-1)/2; i++) {
				files[i] = args[2*i + 1];
				ports[i] = Integer.parseInt(args[2*i + 2]);
				threads[i] = new RowSwapThread(files[i], ports[i]);
				threads[i].start();
			}

			
			//main loop
			while(true) {
				System.out.println("waiting for packets...");
				buff=new byte[256];
				 boStream = new ByteArrayOutputStream();
				 doStream = new DataOutputStream(boStream);
				 biStream = new ByteArrayInputStream(buff);
				 diStream = new DataInputStream(biStream);
				
				
				packet.setData(buff);
				sock.receive(packet);
				String nomeFileTarget = diStream.readUTF();
				System.out.println("file richiesto: "+ nomeFileTarget);
				boolean found = false;
				
				buff=new byte[256];
				packet.setData(buff);
				 boStream = new ByteArrayOutputStream();
				 doStream = new DataOutputStream(boStream);
				 biStream = new ByteArrayInputStream(buff);
				 diStream = new DataInputStream(biStream);
				
				for (int i=0; i<files.length && !found; i++) {
					if (files[i].equals(nomeFileTarget)) {
						found = true;
						System.out.println("porta trovata: " + ports[i]);
						doStream.writeUTF(Integer.toString(ports[i])); //meglio lasciarle come stringhe al posto che interi
						//si suppone che mittente e destinatario si invertano in automatico
						buff = boStream.toByteArray();
						//packet.setData(buff,0,boStream.size());
						packet.setData(buff);
						sock.send(packet);
					}
				}
				if(!found) {
					doStream.writeUTF("file non presente");
					buff = boStream.toByteArray();
					//packet.setData(buff,0,boStream.size());
					packet.setData(buff);
					sock.send(packet);
				}
				
				
			}
			
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		
	}

}
