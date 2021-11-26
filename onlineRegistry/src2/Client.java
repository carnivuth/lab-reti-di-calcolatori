package esercitazione7;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Client {

	public static void main(String[] args) { //args: ipRegistry portRegistry nameRegistry
		
		System.setProperty("Java.rmi.server.hostname", "127.0.0.1");
		
		if (args.length != 3) {
			System.err.println("Argomenti invalidi");
			System.exit(1);
		}
		
		String nomeCompleto = "//"+args[0]+":"+args[1]+"/"+args[2];
		System.out.println(nomeCompleto);
		String[] tags;
		try {
			RegistryRemotoTagClient remote = (RegistryRemotoTagClient) Naming.lookup(nomeCompleto);
			tags = remote.cercaTag("echo");
			for(String s : tags) {
				if(s != null)System.out.println("tag received: " + s);
			}
			
			EchoServer echo = (EchoServer) remote.cerca(tags[0]);
			String str = "prova echo";
			System.out.println("echo: " + echo.echo(str));
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	
	
	
	
}
