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
import java.util.StringJoiner;

public class RowSwapThread extends Thread {
  final static String COMPLETE_PATH = "D:\\OneDrive - Alma Mater Studiorum Università di Bologna\\Sync\\Reti_Calcolatori\\Java\\Esercitazione1\\src\\";
 
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
  }
  }
  public static boolean swapper(int riga1, int riga2, String nome) throws IOException {
    String r1;
    String result1 = null, result2 = null;
    String tmp1 = null, tmp2 = null;
    StringJoiner sj = new StringJoiner(System.lineSeparator());
    int i = 0;
    BufferedReader in = null;

    in = ReadFile(COMPLETE_PATH);
    
    //così non ciclo tutto il file
    while ((r1 = in .readLine()) != null || (result2 == null && result1 == null) ) {
      i++;
      if (riga1 == i) {
        result1 = r1;
      } else if (riga2 == i) {
        result2 = r1;
      }
    } in.close();

    in = ReadFile(COMPLETE_PATH);
    i = 0;
    while ((r1 = in .readLine()) != null) {
      i++;
      if (riga1 == i) {
        //System.out.println("Scrivo " + result2);
        if (result2 != null) sj.add(result2);
        tmp1 = result2;
      } else if (riga2 == i) {
        //System.out.println("Scrivo " + result1);
        if (result1 != null) sj.add(result1);
        tmp2 = result1;
      } else sj.add(r1);
    } in .close();
    FileWriter fileWriter = new FileWriter(COMPLETE_PATH);
    PrintWriter printWriter = new PrintWriter(fileWriter); 
    //System.out.println("Riga " + riga1 + " = " + result1 + "\nRiga " + riga2 + " = " + result2);
    //System.out.println("tmp = " + tmp1 + " tmp2 = " + tmp2);

//    System.out.println(sj.toString());
    printWriter.print(sj.toString());
    printWriter.close();
    return (result1.equals(tmp2) && result2.equals(tmp1));

  }
  public static BufferedReader ReadFile(String path) {
    BufferedReader in = null;
    try {
      //simulo lettura da nomefile
      in = new BufferedReader(new FileReader(COMPLETE_PATH));
    } catch (FileNotFoundException e) {
      System.err.print("Errore creazione BufferedReader");
    }
    return in;
  }
}