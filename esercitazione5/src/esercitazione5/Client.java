package esercitazione5;

public class Client {
	
	//parametri di invocazione registryHost [registryPort]
	public static void main(String args[]) {
		
		int registryPort=1099;
		String registryHost;
		
		//CONTROLLO ARGOMENTI
		if(args.length>2 || args.length==0) {
			System.out.println("errore invocazione");
			System.exit(-1);
		}
		
		registryHost=args[0];
		
		if(args.length>1) {
			
			try {
				
				registryPort= Integer.parseInt(args[1]); 
				
			}catch(NumberFormatException e) {
				
				System.err.println("errore: inserire porta valida");
				System.exit(-1);
			}
			
			if(registryPort<1024||registryPort>65535) {
				
				System.err.println("errore: inserire porta valida");
				System.exit(-1);
			}
		}
		
		
	}

}
