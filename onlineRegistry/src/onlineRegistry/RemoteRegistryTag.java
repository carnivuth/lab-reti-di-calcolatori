package onlineRegistry;

import java.rmi.Remote;
import java.rmi.RemoteException;



public class RemoteRegistryTag extends RemoteRegistry implements ClientInterfaceTag, ServerInterfaceTag {

	private static final long serialVersionUID = 1L;
	private Object[][] tagTable;
	

	protected RemoteRegistryTag() throws RemoteException {
		
		super();
		tagTable=new Object[256][256];
		for (int i = 0; i < tagTable.length; i++) {

			for(int j = 0; j < tagTable.length; j++) {

				tagTable[i][j] = null;
			}
		}
	}

	@Override
	public synchronized boolean addTagToService(String ServiceName, String tag) throws RemoteException {
		
		return false;
	}

	@Override
	public synchronized  Remote[] findTag(String tag) throws RemoteException {
		return null;
	}

}
