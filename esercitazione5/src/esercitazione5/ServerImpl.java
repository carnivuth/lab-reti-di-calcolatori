package esercitazione5;

import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;

public class ServerImpl extends RemoteObject implements RemOp {

	@Override
	public int conta_righe(String filename, int numWords) throws RemoteException {
		return 0;
	}

	@Override
	public int elimina_riga(String filename, int row) throws RemoteException {
		return 0;
	}

	// parametri di invocazione [registryPort]
	public static void main(String args[]) {

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

	}
}
