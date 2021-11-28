//package esercitazione7;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Test1 extends Remote{
	public int test1() throws RemoteException;
}
