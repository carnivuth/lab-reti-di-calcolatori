package Swap;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class TestRandomAccessFile {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String line;
		long pos=0;
			try {
				RandomAccessFile x= new RandomAccessFile("INSERIRE IL PATH DEL FILE", "rw");
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
			}
			System.out.println("FINE");
	}

}
