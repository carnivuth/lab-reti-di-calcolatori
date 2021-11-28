//package esercitazione7;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Client {

	public static void main(String[] args){ //args: ipRegistry portRegistry nameRegistry
		
		
		if (args.length != 3) {
			System.err.println("Argomenti invalidi");
			System.exit(1);
		}
		
		
		String nomeCompleto = "//"+args[0]+":"+args[1]+"/"+args[2];
		System.out.println(nomeCompleto);
		String[] tags;
		try {
			System.out.println("Inserisci nome tag da richiedere al server");

			BufferedReader in= new BufferedReader(new InputStreamReader(System.in));
			String line="";
			
			RegistryRemotoTagClient remote = (RegistryRemotoTagClient) Naming.lookup(nomeCompleto);
		
			while((line=in.readLine())!= null){		
			tags = remote.cercaTag(line);
	
		        for(String s : tags) {	
				if(s != null)System.out.println("tag received: " + s);					                     
			}		
			System.out.println("Scegli servizio");
			String res=in.readLine();
				
			String str="";
			switch(res){
				case "echoServer":
				EchoServer echo = (EchoServer) remote.cerca(res);
				str = "prova echo";
				System.out.println("echo: " + echo.echo(str));
				break;
				case "test1":
				Test1 test1 = (Test1) remote.cerca(res);
	                        str = "prova test1";
			        System.out.println("test: " + test1.test1());	
				break;
				default: System.out.println("Non trovato");
			}

			System.out.println("Inserisci nome tag da richiedere al server");
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	
	
	
}
