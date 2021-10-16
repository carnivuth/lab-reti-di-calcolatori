package esercitazione1;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class RowSwapThread extends Thread{
	
	private String nomeFile;
	private int port;
	
	public RowSwapThread(String nomeFile, int port) {
		this.nomeFile = nomeFile;
		this.port = port;
	}
	
	public void run() {
		
		try {
			DatagramSocket sock = new DatagramSocket(port);
			byte[] buff = new byte[256];
			DatagramPacket packet = new DatagramPacket(buff, buff.length);
			ByteArrayOutputStream boStream = new ByteArrayOutputStream();
			DataOutputStream doStream = new DataOutputStream(boStream);
			ByteArrayInputStream biStream = new ByteArrayInputStream(buff);
			DataInputStream diStream = new DataInputStream(biStream);
			LineUtility lu = new LineUtility();
			//ciclo infinito di attersa
			while(true) {
				//attendo richiseta client
				sock.receive(packet);
				//leggo risposta
				String risposta = diStream.readUTF();
				String[] lines = risposta.split(":");
				//conversione linee da scambiare
				int Linea1=Integer.parseInt(lines[0]);
				int Linea2=Integer.parseInt(lines[1]);
				
				/*
				if (firstLine.equals("Linea non trovata") || secondLine.equals("Linea non trovata")) {
				
					System.err.println("Linee non trovate");
					System.exit(3);
				}
				
				BufferedReader fileStream = new BufferedReader(new FileReader(new File(nomeFile)));
				PrintWriter pw = new PrintWriter(new File(nomeFile));
				
				for (int i=0; i<Integer.parseInt(lines[0]) - 1; i++) {
					
					fileStream.readLine();
					fileStream.skip
				}*/
			}
		}catch(SocketException e) {
			e.printStackTrace();
		}catch(IOException e) {
			e.printStackTrace();
		}
		
	}
}
