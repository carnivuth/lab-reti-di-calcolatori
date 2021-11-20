package esercitazione5;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ServerImpl extends UnicastRemoteObject implements RemOp,Serializable {
	
	//struttura di gestione mutua esclusione
	public static File files[] = new File[256];
	public static boolean status[]= new boolean[256];
	
	//funzione di set status del file
	public static synchronized void setStatusFile(File f,boolean s) {

		int i=0;

		for(i=0;i<files.length && files[i]!=null;i++) {
		
			if(files[i].getAbsolutePath().equals(f.getAbsolutePath())) {
			
				status[i]=s;
				return;
			}
		}
		//caso file non presente
		files[i]=f;
		status[i]=s;
		
	}
	
	//funzione di lettura status del file
	public static synchronized boolean getStatusFile(File f) {

		int i=0;

		for(i=0;i<files.length && files[i]!=null;i++) {
		
			if(files[i].getAbsolutePath().equals(f.getAbsolutePath())) {
			
				return status[i];
			}
		}
		//caso file non presente
		files[i]=f;
		status[i]=true;
		return false;
		
	}
	
	
	protected ServerImpl() throws RemoteException {
		super();
		
	}

	private static final long serialVersionUID = 1L;

	@Override
	public int conta_righe(String filename, int numWords) throws RemoteException {
		
		int result=0;
		File file =new File(filename);
		
		//controllo su esistenza file
		if(!file.exists() || !file.isFile())throw new RemoteException();
		
		try {
			
			//apertura buffered reader 
			BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String row=null;
			
			//main loop
			while((row=reader.readLine())!=null) {
				
				if(row.split(" ").length>=numWords)result+=1;
				
			}
			reader.close();
		
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		return result;
	}

	@Override
	public String elimina_riga(String filename, int target) throws RemoteException {
		
		File file =new File(filename);
		File tmp =new File(filename+Thread.currentThread().getId()+".txt");
		int index=0;
		
		//controllo su esistenza file
		if(!file.exists() || !file.isFile())throw new RemoteException();
		
		//attesa attiva per mutua esclusione
		while(!getStatusFile(file));
		
		
		try {
			
			//apertura buffered reader 
			BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			PrintWriter pw=new PrintWriter(new OutputStreamWriter(new FileOutputStream(tmp)));
			String row=null;

			//main loop
			while((row=reader.readLine())!=null) {
				
				index++;
				if(index!=target)pw.write(row+System.lineSeparator());
			}
		
			//chiusura reader writer
			reader.close();
			pw.close();
			
			//controllo numero di righe del file
			if(index<target) {
				
				tmp.delete();
				//liberazione della risorsa nella struttura per la mutua esclusione
				setStatusFile(file,false);
				throw new RemoteException();
			}
			
			//sostituzione file con file temporaneo
			file.delete();
			tmp.renameTo(new File(filename));
			
			//liberazione della risorsa nella struttura per la mutua esclusione
			setStatusFile(new File(filename),false);
			
		
		} catch (IOException e) {
			
			//liberazione della risorsa nella struttura per la mutua esclusione
			setStatusFile(new File(filename),false);
			e.printStackTrace();
		}
		
		
		
		
		return index-1 + filename;
	}

	// parametri di invocazione [registryPort]
	public static void main(String args[]) {
		
		System.out.println("avvio server");

		int registryPort = 1099;

		// CONTROLLO ARGOMENTI
		if (args.length > 1) {
			System.out.println("errore invocazione");
			System.exit(-1);
		}
		if (args.length == 1) {

			try {

				registryPort = Integer.parseInt(args[0]);

			} catch (NumberFormatException e) {

				System.err.println("errore: inserire porta valida");
				System.exit(-1);
			}

			if (registryPort < 1024 || registryPort > 65535) {

				System.err.println("errore: inserire porta valida");
				System.exit(-1);
			}
		}
			
			
			// Registrazione del servizio RMI
			String completeName = "//" +"localhost" + ":" + registryPort + "/" + "server";
			try {
				
				System.out.println(completeName);
				ServerImpl serverRMI = new ServerImpl();
				Naming.bind(completeName, serverRMI);
				System.out.println("registrazione avvenuta con successo");
			} catch (Exception e) {
				
				e.printStackTrace();
				System.exit(1);
			}
		
		

	}
}
