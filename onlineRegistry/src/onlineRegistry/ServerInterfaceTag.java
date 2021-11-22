package onlineRegistry;


import java.rmi.RemoteException;

public interface ServerInterfaceTag extends ServerInterface {
	public boolean addTagToService(String ServiceName,String tag)throws RemoteException;
	
	
}
