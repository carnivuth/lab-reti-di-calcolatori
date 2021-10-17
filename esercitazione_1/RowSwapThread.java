package swap
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;


public class RowSwapThread extends Thread {
  final static String COMPLETE_PATH = "";//path to file system
  private int port;
  private String nomeFile;
  
  public RowSwapThread(String nomeFile, int port) {
		this.nomeFile = COMPLETE_PATH  + nomeFile;
		this.port = port;
	}
  
  public void run() {
	  DatagramSocket sock = null;
	  try {
		  sock = new DatagramSocket(port);
	} catch (SocketException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	  byte[] buff = new byte[256];
	  DatagramPacket packet = new DatagramPacket(buff, buff.length);
	  ByteArrayOutputStream boStream = new ByteArrayOutputStream();
	  //DataOutputStream doStream = new DataOutputStream(boStream);
	  ByteArrayInputStream biStream = new ByteArrayInputStream(buff);
	  DataInputStream diStream = new DataInputStream(biStream);
		
	  while(true) {
		  //pulisco tutti i buffer
		  buff=new byte[256];
		  packet.setData(buff);
		  boStream = new ByteArrayOutputStream();
		  biStream = new ByteArrayInputStream(buff);
		  diStream = new DataInputStream(biStream);
		  
			//attendo richiseta client
			try {
				sock.receive(packet);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			//leggo risposta
			String risposta = null;
			try {
				risposta = diStream.readUTF();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println("Mi sono arrivate linee "+risposta);
			String[] lines = risposta.split(":");
			//conversione linee da scambiare
			int riga1=Integer.parseInt(lines[0]);
			int riga2=Integer.parseInt(lines[1]);

    //System.out.println("Swappo riga " + riga1 + " e " + riga2);
	long start = System.currentTimeMillis();
    boolean res = false;
	try {
		res = swapper(riga1, riga2, nomeFile);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    long elapsedTimeMillis = System.currentTimeMillis()-start;
    System.out.print("Esito: " + res + " Tempo impiegato : "+elapsedTimeMillis/1000F);
    
    //invio della risposta al client
    //di nuovo ricreo tutto altrimenti invia vecchi dati
    buff=new byte[256];
    boStream = new ByteArrayOutputStream();
    DataOutputStream doStream = new DataOutputStream(boStream);
    try {
    	if (res=true) doStream.writeUTF("T'apposto");
    	else doStream.writeUTF("riga non presente");
    }catch(IOException e){
    	System.out.println("Errore nella scrittura del messaggio di esito al client");
    	e.printStackTrace();
    }
    buff = boStream.toByteArray();
    packet.setData(buff); 
    try {
		sock.send(packet);
	} catch (IOException e) {
		System.out.println("Errore nell'invio del messaggio di esito al client");
		e.printStackTrace();
		sock.close();
	}
  }
  }
  public boolean swapper(int riga1, int riga2, String nome) throws IOException {
	    String r1;
	    String result1 = null, result2 = null;
	    String tmp1 = null, tmp2 = null;
	    FileWriter fwTmp = new FileWriter("tmp"+this.port+".txt");
	    PrintWriter pwTmp = new PrintWriter(fwTmp);
	    int i = 0;
	    BufferedReader in = null;

	    System.out.println("nome file: "+nome);
	    in = ReadFile(nome);
	    
	    //cosi' non ciclo tutto il file
	    while ((r1 = in .readLine()) != null || (result2 == null && result1 == null) ) {
	      i++;
		System.out.println("Riga letta dallo swapper: "+i+" : "+r1);
	      if (riga1 == i) {
	        result1 = r1;
	      } else if (riga2 == i) {
	        result2 = r1;
	      }
	    } in.close();
		if(i<riga1 || i< riga2) { 
	    	System.out.println("Linea errata, guasto, pericolo!");
	    	pwTmp.close();
	    	return false;
	    }

	    in = ReadFile(nome);
	    i = 0;
	    while ((r1 = in .readLine()) != null) {
	      i++;
		if (r1.isEmpty() || ("".equals(r1))) System.out.println("Trovato l'errore! riga: "+i);
		System.out.println("Riga letta dallo swapper: "+i+" : "+r1);
	      if (riga1 == i) {
	        //System.out.println("Scrivo " + result2);
	        if (result2 != null) pwTmp.println(result2);
	        tmp1 = result2;
	      } else if (riga2 == i) {
	        //System.out.println("Scrivo " + result1);
	        if (result1 != null) pwTmp.println(result1);
	        tmp2 = result1;
	      } else pwTmp.println(r1);
	    } 
	    in .close();
	    pwTmp.close();
	     
	    FileWriter fileWriter = new FileWriter(nome);
	    PrintWriter printWriter = new PrintWriter(fileWriter); 
	    in = ReadFile("tmp"+this.port+".txt");
	  
	    //ciclo di sovrascrittura file origine
	    while ((r1 = in.readLine()) != null  ) {
	   
	    	printWriter.println(r1);
	    }
	   
	    printWriter.close();
	    return (result1.equals(tmp2) && result2.equals(tmp1));

	  }
  public static BufferedReader ReadFile(String path) {
    BufferedReader in = null;
    try {
      //simulo lettura da nomefile
      in = new BufferedReader(new FileReader(path));
    } catch (FileNotFoundException e) {
      System.out.println(path);
      System.err.print("Errore creazione BufferedReader");
    }
    return in;
  }
}