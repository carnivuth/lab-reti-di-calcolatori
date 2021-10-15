package Swap;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int variabile=System.lineSeparator().getBytes().length;
		System.out.println(System.lineSeparator().toString());
		System.out.println(variabile);
		int pos=0;
			/*try {
				RandomAccessFile x= new RandomAccessFile("C:/Users/39320/eclipse-workspace/RetiEsercitazione1/src/Swap/ciao.txt", "rw");
				line=x.readLine();
				int i=1;
				while(line!=null) {
					System.out.println(line);
					//stampo tutto il file per vedere se lo leggo bene
					//qui c'è un if per prendere il puntatore dove dobbiamo sovrascivere
					//così sappiamo in che posizione iniziare
					//ho testato con un paio di righe quindi ho provato a scrivere dopo la prima
					if (i==1) pos=x.getFilePointer();
					line=x.readLine();
					i++;
				}
				System.out.println(pos);
				pos--;
				x.seek(pos); //mi sposto dove devo scrivere
				x.writeUTF("SONO NUOVO");
				
				System.out.println();
				System.out.println();
				//mi sposto all'inizio per stampare di nuovo tutto
				x.seek(0);
				line=x.readLine();
				while(line!=null) {
					System.out.println(line);
					line=x.readLine();
				}
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				System.out.println("\nFuck go back");
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("\nFuck go back2");
				e.printStackTrace();
			}*/
		
		
		int numLinee=0;
		//String stringa;
		int[] offsets= new int[20];
		offsets[0]=0;
		byte[] array= new byte[2];
		try {
			pos=0;
			RandomAccessFile x= new RandomAccessFile("C:/Users/39320/eclipse-workspace/RetiEsercitazione1/src/Swap/ciao.txt", "rw");
			/*while (x.read(array, 0, 1)>0) {
				stringa=new String(array, "UTF-8");
				System.out.print(stringa.charAt(0)); //I print all the file char for char
				pos++; //numero di caratteri per linea
				if (stringa.charAt(0)==System.lineSeparator().getBytes()) {
					numLinee++;
					offsets[numLinee]=pos; //salvo gli offset
					//pos=0;
				}
			}*/
			
			numLinee++;
			String strtmp;
			int sum=0;
			while((strtmp=x.readLine())!=null) {
				System.out.println(strtmp);
				sum=sum+strtmp.length()+variabile;
				offsets[numLinee]=sum;
				numLinee++;
			}
			System.out.println("");
			
			
			int linea1=1;
			int linea2=4;
					
			//in base alla riga che sposto uso gli offset
			//sono uguali alla fine della loro riga,  l'inizio della successiva
			int offset1, offset2;
			offset1=offsets[linea1];//offsets[line1-1] per ottenere da dove iniziare a leggere
			offset2=offsets[linea2]; 
			
			int length1=offsets[linea1]-offsets[linea1-1];//lungazza delle due linee
			int length2=offsets[linea2]-offsets[linea2-1];
						
			x.seek((long)offsets[linea1-1]);
			String stringa1=x.readLine();
			x.seek(0);
			
			x.seek((long)offsets[linea2-1]);
			String stringa2=x.readLine();
			x.seek(0);
			
			int differenza=length1-length2;
			int posizione=0; //nuovo offsets
			int lineaAttuale=0;
			
			
			if (length1>=length2) { //scrivo dalla prima linea
				//x.seek((long)offset1);
				//x.writeUTF(stringa2);
				//x.write(x., off, len);
				lineaAttuale=linea1;
				//char[] buffer = stringa2.toCharArray();
				byte[] b = new byte[256];
				//for (int i = 0; i < buffer.length; i++) {
				//	  b[i] = (byte) buffer[i];
				//	 }
				x.seek((long)offsets[linea1-1]);
				x.write(stringa2.getBytes(), 0/*offsets[linea1-1]*/, stringa2.getBytes().length);
				x.write(System.lineSeparator().getBytes(), 0, System.lineSeparator().getBytes().length);
					//forse bisogna scrivere anche lo /n
				
				//char aCapo=System.lineSeparator().getBytes();
				posizione=offsets[linea1]-differenza; //perchè ho swappato le linee e devo scrivere a partire dalla fine della seconda
				lineaAttuale++;
				while(lineaAttuale<linea2) {
					x.seek((long)offsets[lineaAttuale-1]);
					strtmp=x.readLine();
					System.out.println("Ho letto: "+strtmp);
					x.seek(posizione);
					x.writeBytes(strtmp);
					//x.writeChars(strtmp);
					x.write(System.lineSeparator().getBytes(), 0, System.lineSeparator().getBytes().length);
					
					/*x.read(b, 0, offsets[lineaAttuale]-offsets[lineaAttuale-1]);
					String tmp=new String(b, "UTF-8");
					System.out.println("Ho letto: "+tmp);
					x.seek(posizione);
					x.write(b, 0, offsets[lineaAttuale]-offsets[lineaAttuale-1]);
					*/
					posizione=posizione+(offsets[lineaAttuale]-offsets[lineaAttuale-1]);
					lineaAttuale++;
				}
				
				
				x.write(stringa1.getBytes(), 0, stringa1.getBytes().length);
					
				//System.exit(1);
				
				
				
				//lineaAttuale=linea1;
				//posizione=offset2; //perchè li ho swappati
				//while (lineaAttuale);
			}
			else { //scrivo dalla seconda linea
				
			}
			
			x.close();
			
			
		}catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("\nFuck go back");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("\nFuck go back2");
			e.printStackTrace();
		}
		
		System.out.println("FINE");
	}

}
