package onlineRegistry;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterfaceTag extends ClientInterface {
	
	public Remote[] findTag(String tag)throws RemoteException;
	
}
