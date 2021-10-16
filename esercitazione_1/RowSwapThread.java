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
				
				int LSLength=System.lineSeparator().getBytes().length;
				int numLinee=0;
				long[] offsets= new long[2];
				String stringa1;
				String stringa2;
				int str1Length;
				int str2Length;
				stringa1="";stringa2="";
				str1Length=0; str2Length=0;
				
				try {
				
					RandomAccessFile FReaderWriter= new RandomAccessFile((this.nomeFile, "rw");
					numLinee++;
					String strtmp;
					
					
					while((strtmp=FReaderWriter.readLine())!=null) {
						
						
						if(numLinee==linea1) {
							
							stringa1=strtmp;
							str1Length=stringa1.length();
							offsets[LINEA_1]=FReaderWriter.getFilePointer()-(str1Length+LSLength);
						}
						
						if(numLinee==linea2) {
						
							stringa2=strtmp;
							str2Length=stringa2.length();
							offsets[LINEA_2]=FReaderWriter.getFilePointer()-(str2Length+LSLength);
						}
						
						numLinee++;
					}
					if(stringa1=="" || stringa2=="")System.exit(-1);
						//calcolo differenza tra le stringhe
						int differenza=(str1Length>str2Length)?str1Length-str2Length:str2Length-str1Length;
						////////////hardcoding delle linee per debug
						linea1=1;
						linea2=3;
						long posizione=0; //nuovo offsets
						int lineaAttuale=0;
						
						
						if (str1Length>=str2Length) { //scrivo dalla prima linea
						
							lineaAttuale=linea1;
							//mi posiziono all'inizio della prima linea
							FReaderWriter.seek((long)offsets[LINEA_1]);
							//e ci scrivo la seconda (perchè appunto vanno scambiate)
							FReaderWriter.write(stringa2.getBytes(), 0, str2Length);
							FReaderWriter.write(System.lineSeparator().getBytes(), 0, LSLength);
							//avendo scritto la seconda linea sovrascrivendo la prima
							//ed essendo la seconda più piccola, ho alcuni caratteri rimasti
							//quindi mi posiziono all'inizio di questi caratteri
							//tutto ciò è memorizzato in posizione, che di volta in volta verrà incrementata
							//così sposto man mano indietro di alcuni caratteri (cioè della differenza tra le due stringhe)
							//tutte le linee
							posizione=(long)(offsets[LINEA_1]+str1Length+LSLength-differenza);
							lineaAttuale++;
							while(FReaderWriter.getFilePointer()<(long)offsets[LINEA_2]) {
								//mi sposto all'inizio della riga successiva
								FReaderWriter.seek((long)offsets[lineaAttuale-1]);
								//leggo la linea
								strtmp=FReaderWriter.readLine();
								System.out.println("Ho letto: "+strtmp);
								//mi sposto dove devo iniziare a scriverla e la scrivo
								FReaderWriter.seek(posizione);
								FReaderWriter.writeBytes(strtmp);
								FReaderWriter.write(System.lineSeparator().getBytes(), 0, System.lineSeparator().getBytes().length);
								//aggiorno le variabili
								posizione=posizione+(offsets[lineaAttuale]-offsets[lineaAttuale-1]);
								lineaAttuale++;
							}
							
							
							FReaderWriter.write(stringa1.getBytes(), 0, stringa1.getBytes().length);
						}
						else { 
							//inizializzo buffer di appoggio per la lettura
							byte buffer1[]=new byte[differenza];
							byte buffer2[]=new byte[differenza];
							//mi posiziono per scrivere la riga 1 al posto della riga 2
							FReaderWriter.seek((long)offsets[LINEA_2]+differenza);
							FReaderWriter.write(stringa1.getBytes(), 0,str1Length);
							//muovo il pointer alla file della riga 1 
							FReaderWriter.seek((long)offsets[LINEA_1]+str1Length);
							//salvo i byte che verranno sovrascritti
							FReaderWriter.read(buffer1,0,differenza);
							//riposizionamento e rescrittura del file 
							FReaderWriter.seek((long)offsets[LINEA_1]);
							FReaderWriter.write(stringa2.getBytes(), 0,str2Length);
							
							//ciclo di riscrittura del file (sezione linea1-linea2)
							while(FReaderWriter.getFilePointer()<(long)offsets[LINEA_2]+differenza) {
								FReaderWriter.read(buffer2, 0, differenza);
								FReaderWriter.seek(FReaderWriter.getFilePointer()-differenza);
								FReaderWriter.write(buffer1, 0,differenza);
								buffer1=Arrays.copyOf(buffer2, buffer2.length);
								
							}
							
						
						
						}
					
					FReaderWriter.close();
					
					
				}catch(FileNotFoundException e){
					
					System.out.println("file non trovato");
					e.printStackTrace();
				
				}catch(IOException e){

					System.out.println("errore nell'interazione con il file");
					e.printStackTrace();
				}
				
				System.out.println("FINE");
			}

			}
		}catch(SocketException e) {
			e.printStackTrace();
		}catch(IOException e) {
			e.printStackTrace();
		}
		
	}
}
