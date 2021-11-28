//package esercitazione7;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface EchoServer extends Remote{
	public String echo(String s) throws RemoteException;
}
