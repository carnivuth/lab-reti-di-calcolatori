package onlineRegistry;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {
	public boolean add(String serviceName,Remote service)throws RemoteException;
	public boolean delete(String service)throws RemoteException;
}
