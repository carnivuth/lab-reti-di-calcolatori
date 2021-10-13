//package Swap;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class TestRandomAccessFile {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String line;
		long pos=0;
			try {
				RandomAccessFile x= new RandomAccessFile("INSERIRE PATH FILE", "rw");
				line=x.readLine();
				int i=1;
				while(line!=null) {
					System.out.println(line);
					if (i==1) pos=x.getFilePointer();
					line=x.readLine();
					i++;
				}
				System.out.println(pos);
				pos--;
				//pos--;
				x.seek(pos);
				x.writeUTF("SONO NUOVO");
				
				System.out.println();
				System.out.println();
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
