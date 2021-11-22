package onlineRegistry;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote {

	public Remote find(String service)throws RemoteException;
	public Remote[] findAll(String service)throws RemoteException;
}
