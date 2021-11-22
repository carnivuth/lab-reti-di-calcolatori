package onlineRegistry;

import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RemoteRegistry extends UnicastRemoteObject implements ClientInterface, ServerInterface {

	private static final long serialVersionUID = 1L;
	Object[][] serviceTable;

	// costruttore
	protected RemoteRegistry() throws RemoteException {

		super();
		serviceTable = new Object[256][2];

		for (int i = 0; i < serviceTable.length; i++) {

			serviceTable[i][0] = null;
			serviceTable[i][1] = null;
		}

	}

	// metodo di aggiunta servizio
	@Override
	public synchronized boolean add(String serviceName, Remote service) throws RemoteException {

		for (int i = 0; i < serviceTable.length; i++) {

			if (serviceTable[i][0] == null && serviceTable[i][1] == null) {

				serviceTable[i][0] = serviceName;
				serviceTable[i][1] = service;
				return true;

			}

		}
		return false;

	}

	// metodo di cancellazione di un servizio
	@Override
	public synchronized boolean delete(String serviceName) throws RemoteException {

		for (int i = 0; i < serviceTable.length; i++) {

			if (serviceTable[i][0] == serviceName) {

				serviceTable[i][0] = null;
				serviceTable[i][1] = null;
				return true;

			}

		}
		return false;

	}

	// metodo di ricerca di servizio
	@Override
	public synchronized Remote find(String serviceName) throws RemoteException {

		for (int i = 0; i < serviceTable.length; i++) {

			if (serviceTable[i][0] == serviceName) {

				return (Remote) serviceTable[i][1];

			}

		}
		return null;
	}

	// metodo di ricerca di una lista di servizzi che corrispondono allo stesso nome
	@Override
	public synchronized Remote[] findAll(String serviceName) throws RemoteException {

		Remote[] result = new Remote[256];
		int j = 0;

		for (int i = 0; i < serviceTable.length; i++) {

			if (serviceTable[i][0] == serviceName) {

				result[j] = (Remote) serviceTable[i][1];
				j++;

			}

		}
		return result;

	}

	// parametri di invocazione [registryPort]
	public static void main(String args[]) {

		System.out.println("avvio server");

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

		// Registrazione del servizio RMI
		String completeName = "//" + "localhost" + ":" + registryPort + "/" + "server";
		try {

			System.out.println(completeName);
			RemoteRegistry serverRMI = new RemoteRegistry();
			Naming.bind(completeName, serverRMI);
			System.out.println("registrazione avvenuta con successo");
		} catch (Exception e) {

			e.printStackTrace();
			System.exit(1);
		}

	}

}
