//package esercitazione7;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RegistryRemotoTagServer extends RegistryRemotoTagClient, RegistryRemotoServer{
	
	public boolean associaTag(String nomeLogico, String tag) throws RemoteException;
	
}
