//package esercitazione7;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class Server implements EchoServer, Test1, Serializable{


	private static final long serialVersionUID = 1L;


	public Server() {}

	public String echo(String s) throws RemoteException {
		return s;
	}

	public int test1() throws RemoteException {
		return 1;
	}

	
	public static void main(String args[]) { //args: ipRegistry, portRegistry, nameRegistry
		
		if (args.length != 3) {
			for(int i=0; i< 10; i++) {
				if (args[i] != null) System.out.println("arg: " + args[i]);
			}
			System.err.println("Argomenti invalidi");
			System.exit(1);
		}
		
		String nomeCompletoRegistry = "//"+args[0]+":"+args[1]+"/"+args[2];
		System.out.println(nomeCompletoRegistry);
		try {
			System.out.println(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			//LocateRegistry.createRegistry(Integer.parseInt(args[3]));
			RegistryRemotoTagServer remote = (RegistryRemotoTagServer) Naming.lookup(nomeCompletoRegistry);
			Server echoServer = new Server();
			Server test1 = new Server();
			System.out.println("iniziando le operazioni");
			if (remote.aggiungi("echoServer", echoServer)) System.out.println("aggiunto echoServer");
			else System.out.println("non aggiunto server");
			if (remote.associaTag("echoServer", "echo")) System.out.println("aggiunto tag echo a echoServer");
			else System.out.println("non aggiunto tag echo a echoServer");
			System.out.println("iniziando le operazioni");
			if (remote.aggiungi("test1", test1)) System.out.println("aggiunto test1");
			else System.out.println("non aggiunto server");
			if (remote.associaTag("test1", "test1")) System.out.println("aggiunto tag test1 a test1");
			else System.out.println("non aggiunto tag test1 a test1");
				
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
		
	}

	
	
	
}
