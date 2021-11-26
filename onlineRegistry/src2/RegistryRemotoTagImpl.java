package esercitazione7;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class RegistryRemotoTagImpl extends UnicastRemoteObject implements RegistryRemotoTagServer{
	private static final long serialVersionUID = 1L;
	final int tableSize= 100;
	// Tabella: la prima colonna contiene i nomi, la seconda i riferimenti remoti
	Object [][] table = new Object[tableSize][2];
	// primo elemento nome logico, secondo lista di tag
	Object [][] tagsNames = new Object[tableSize][2];
	
	public RegistryRemotoTagImpl() throws RemoteException{
		super();
		for( int i=0; i<tableSize; i++ ){
			table[i][0]=null; table[i][1]=null;
			tagsNames[i][0]=null; tagsNames[i][1] = new ArrayList<String>();
		}
	}
	
	public synchronized Remote cerca(String nomeLogico) throws RemoteException{
		Remote risultato= null;
		if( nomeLogico== null ) return null;
		for( int i=0; i<tableSize; i++ ) {
			if( nomeLogico.equals((String)table[i][0]) ){
				risultato= (Remote) table[i][1];
				break;
			} 
		}
		return risultato;
	}
	
	
	
	public synchronized Remote[] cercaTutti(String nomeLogico) throws RemoteException{
		int cont= 0;
		if( nomeLogico== null ) return new Remote[0];
		for( int i=0; i<tableSize; i++ ) {
			if( nomeLogico.equals((String)table[i][0]) ) {
				cont++;
			}
		}
		Remote[] risultato= new Remote[cont];
		cont=0;
		for( int i=0; i<tableSize; i++ ) {
			if( nomeLogico.equals((String)table[i][0]) ) {
				risultato[cont++] = (Remote)table[i][1];     
			}
		}
		return risultato;
	}	
	
	
	public synchronized Object[][] restituisciTutti()throws RemoteException{
		int cont= 0;
		for (int i= 0; i< tableSize; i++) {
			if (table[i][0] != null) {
				cont++;
				
			}
		}
		Object[][] risultato= new Object[cont][2];
		cont= 0;
		for (int i= 0; i< tableSize; i++) {
			if (table[i][0] != null) {
				risultato[cont][0] = table[i][0];
				risultato[cont][1] = table[i][1];
			}
		}
		return risultato;
	}
	
	
	public synchronized boolean aggiungi(String nomeLogico,Remote riferimento) throws RemoteException{
		System.out.println("aggiungendo " + nomeLogico);
		boolean risultato = false;
		if((nomeLogico== null)||(riferimento == null)) return risultato;
		for(int i=0; i<tableSize; i++) {
			if( table[i][0] == null ){
				table[i][0]= nomeLogico;
				tagsNames[i][0] = nomeLogico; //aggiunta
				table[i][1]=riferimento;
				risultato = true;
				break;
			}
		}
	    return risultato;
	}
	
	
	public synchronized boolean eliminaPrimo(String nomeLogico) throws RemoteException{
		boolean risultato= false;
		if( nomeLogico== null) return risultato;
		for( int i=0; i<tableSize; i++ ) {
			if( nomeLogico.equals( (String)table[i][0]) ){
				table[i][0]=null;
				table[i][1]=null;
				risultato=true;
				break;
			}
			   
		}
		return risultato; 
	}	
	
	
	
	public synchronized boolean eliminaTutti(String nomeLogico) throws RemoteException{ 
		boolean risultato= false;
		if( nomeLogico== null) return risultato;
		for( int i=0; i<tableSize; i++ ) {
			if( nomeLogico.equals((String)table[i][0]) ){
				if( risultato== false ) risultato= true;
				table[i][0]=null;
				table[i][1]=null;
			}
		}
		return risultato;
	}

	
	public String[] cercaTag(String tag) throws RemoteException {
		
		String[] res = new String[tableSize];
		int nextFree = 0;
		
		for(int i=0; i<tableSize; i++) {
			if (tagsNames[i][1] != null && ((ArrayList<String>)tagsNames[i][1]).contains(tag)) {
				res[nextFree] = (String)tagsNames[i][0];
				System.out.println("found " + (String)tagsNames[i][0]);
				nextFree++;
			}
		}
		
		return res;
	}

	
	public boolean associaTag(String nomeLogico, String tag) {
		System.out.println("associa");
		boolean done = false;
		for(int i=0; i<tableSize && !done; i++) {
			System.out.println("table string " + ((String)tagsNames[i][0]));
			if (tagsNames[i][0] != null && ((String)tagsNames[i][0]).equals(nomeLogico)) {
				System.out.println("associando " + tag);
				((ArrayList<String>) tagsNames[i][1]).add(tag);
				System.out.println(((ArrayList<String>) tagsNames[i][1]).size());
				done = true;
			}
		}
		
		return done;
	}
	
	
	public static void main(String args[]) {//args: ip, port, nomeServizio
		
		System.setProperty("Java.rmi.server.hostname", "127.0.1.1");
		
		try {
			System.out.println(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		if (args.length != 3) {
			System.err.println("Argomenti invalidi");
			System.exit(1);
		}
		
		String nomeCompleto = "//"+args[0]+":"+args[1]+"/"+args[2];
		System.out.println(nomeCompleto);
		try {
			LocateRegistry.createRegistry(Integer.parseInt(args[1]));
			RegistryRemotoTagImpl server = new RegistryRemotoTagImpl();
			Naming.rebind(nomeCompleto, server);
			
			
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	
	
}