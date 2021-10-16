import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Client {
	
	public static void main(String[] args) {
		
		//controllo argomenti
		if (args.length != 3) {
			System.err.println("Argomenti non validi");
			System.exit(1);
		}
		
		InetAddress addr;
		DatagramSocket sock;
		DatagramPacket packet;
		ByteArrayOutputStream boStream;
		DataOutputStream doStream;
		BufferedReader in;
		ByteArrayInputStream biStream;
		DataInputStream diStream;
		byte[] buff = new byte[256];
		
		//java Client IP PORTA nomefile
		try {
			//setup
			addr = InetAddress.getByName(args[0]);
			sock = new DatagramSocket();
			sock.setSoTimeout(30000);
			packet = new DatagramPacket(buff, buff.length,addr,Integer.parseInt(args[1]));
			boStream = new ByteArrayOutputStream();
			doStream = new DataOutputStream(boStream);
			in = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("IP= "+args[0]+" PORTA= "+args[1]+" NOMEFILE= "+args[2]);
			
			//invio nome file e ricezione risposta
			doStream.writeUTF(args[2]);
			
			buff = boStream.toByteArray();
			System.out.println(buff.toString());
			packet.setData(buff);
			sock.send(packet);
			buff = boStream.toByteArray();
			sock.receive(packet);
			
			biStream = new ByteArrayInputStream(packet.getData(),0,packet.getLength());
			diStream = new DataInputStream(biStream);
			buff = packet.getData();
			String portaRowSwap = diStream.readUTF();
			System.out.println("risposta: "+portaRowSwap);
			
			if (portaRowSwap.equals("file non presente")) {
				System.err.println("File non presente");
				System.exit(2);
			}
			
			//setup indirizzo e porta del server di scambio righe
			packet.setAddress(addr);
			packet.setPort(Integer.parseInt(portaRowSwap));
			//sock = new DatagramSocket(Integer.parseInt(portaRowSwap), rsAddr);
			
			
			
			//loop
			String firstLine;
			String secondLine;
			//attenzione alla lettura, potrebbero rimanere caratteri speciali non consumati
			while( (firstLine = in.readLine()) != null) {
				secondLine = in.readLine();
				System.out.println(firstLine+":"+secondLine);
				doStream.writeUTF(firstLine+":"+secondLine);
				buff = boStream.toByteArray();
				packet.setData(buff);
				sock.send(packet);
				
				sock.receive(packet);
				if (diStream.readUTF().equals("riga non presente")) {
					System.err.println("Riga non presente");
					System.exit(2);
				}
			}
			
			
			
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		
		
		
		
		
		
		
		
	}
}