package esercitazione5;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemOp extends Remote {
	
	public int conta_righe(String filename ,int numWords)throws RemoteException;
	public int elimina_riga(String filename ,int row)throws RemoteException;
}
