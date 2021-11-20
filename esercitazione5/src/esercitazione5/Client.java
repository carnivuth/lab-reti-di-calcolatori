package esercitazione5;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.NotBoundException;

public class Client {
	
	//parametri di invocazione registryHost [registryPort]
	public static void main(String args[]) {
		
		int registryPort=1099;
		BufferedReader in= new BufferedReader(new InputStreamReader(System.in));
		String registryHost;
		
		//CONTROLLO ARGOMENTI
		if(args.length>2 || args.length==0) {
			System.out.println("errore invocazione");
			System.exit(-1);
		}
		
		registryHost=args[0];
		
		if(args.length>1) {
			
			try {
				
				registryPort= Integer.parseInt(args[1]); 
				
			}catch(NumberFormatException e) {
				
				System.err.println("errore: inserire porta valida");
				System.exit(-1);
			}
			
			if(registryPort<1024||registryPort>65535) {
				
				System.err.println("errore: inserire porta valida");
				System.exit(-1);
			}
		}
		
		//tentativo di recupero servizio da registry
		try {
			RemOp server =(RemOp)Naming.lookup("//" +registryHost + ":" + registryPort + "/" + "server");
			String command=null;
			String filename=null;
			int row=0;
			int numWords=0;
			System.out.println("selezionare operazione da eseguire 1: contariga, 2:eliminariga EOF per terminare");
			
			//main loop di interazone con utente
			while((command=in.readLine())!=null) {
				
				row=0;
				numWords=0;
				filename=null;
				
				//esecuzione di metodo remoto conta righe
				if(command.equals("1")) {
				
					System.out.println("inserire nome file");
					filename=in.readLine();
					System.out.println("inserire numero minimo di parole");
					numWords=Integer.parseInt(in.readLine());
					System.out.println(server.conta_righe(filename, numWords));
				
				//esecuzione di metodo remoto elimina riga
				}else if(command.equals("2")) {
					
					System.out.println("inserire nome file");
					filename=in.readLine();
					System.out.println("inserire numero di riga da eliminare");
					row=Integer.parseInt(in.readLine());
					System.out.println(server.elimina_riga(filename, row));
				
				}
				
			}
			 
		
		} catch (IOException | NotBoundException e) {
			
			System.out.println("errore di comunicazione");
			e.printStackTrace();
			System.exit(-1);
		}
		
		}

}
