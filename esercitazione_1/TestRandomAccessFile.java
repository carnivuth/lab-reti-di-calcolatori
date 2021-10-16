

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

public class TestRandomAccessFile {
	public static final int MAX_STRING_LENGTH=100;
	public static final int LINEA_1=0;
	public static final int LINEA_2=1;
	public static void main(String[] args) {
		
		//dichiarazioni e inizializzazioni
		int LSLength=System.lineSeparator().getBytes().length;
		int pos=0;
		int numLinee=0;
		long[] offsets= new long[2];
		String stringa1;
		String stringa2;
		int str1Length;
		int str2Length;
		int linea1=1;
		int linea2=4;
		
		try {
		
			pos=0;
			RandomAccessFile FReaderWriter= new RandomAccessFile("C:\\Users\\matti\\Documents\\GitHub\\lab-reti-di-calcolatori\\prova.txt", "rw");
			numLinee++;
			String strtmp;
			int sum=0;
			
			while((strtmp=FReaderWriter.readLine())!=null) {
				
				
				if(numLinee==linea1) {
					
					stringa1=strtmp;
					offsets[0]=FReaderWriter.getFilePointer()-(str1Length+LSLength);
				}
				
				if(numLinee==linea2) {
				
					stringa2=strtmp;
					offsets[0]=FReaderWriter.getFilePointer()-(str2Length+LSLength);
				}
				
				numLinee++;
			}
			if(stringa2!=null &&stringa1!=null )System.exit(-1);
			
			str2Length=stringa2.length();
			str1Length=stringa1.length();
			//calcolo differenza tra le stringhe
			int differenza=(str1Length>str2Length)?str1Length-str2Length:str2Length-str1Length;
			////////////hardcoding delle linee per debug
			linea1=1;
			linea2=4;
			//////////////
			//lettura delle stringhe target		
			FReaderWriter.seek((long)offsets[LINEA_1]);
			stringa1=FReaderWriter.readLine();
			FReaderWriter.seek((long)offsets[LINEA_2]);
			stringa2=FReaderWriter.readLine();
			////////////////////////////////////////////////////////////////////////
			
			long posizione=0; //nuovo offsets
			int lineaAttuale=0;
			
			
			if (str1Length>=str2Length) { //scrivo dalla prima linea
			
				lineaAttuale=linea1;
				byte[] b = new byte[256];
				FReaderWriter.seek((long)offsets[LINEA_1]);
				FReaderWriter.write(stringa2.getBytes(), 0/*offsets[linea1-1]*/, stringa2.getBytes().length);
				FReaderWriter.write(System.lineSeparator().getBytes(), 0, System.lineSeparator().getBytes().length);
					//forse bisogna scrivere anche lo /n
				
				posizione=(long)(offsets[LINEA_1]+str1Length+LSLength-differenza); //perchè ho swappato le linee e devo scrivere a partire dalla fine della seconda
				lineaAttuale++;
				while(lineaAttuale<linea2) {
					FReaderWriter.seek((long)offsets[lineaAttuale-1]);
					strtmp=FReaderWriter.readLine();
					System.out.println("Ho letto: "+strtmp);
					FReaderWriter.seek(posizione);
					FReaderWriter.writeBytes(strtmp);
					FReaderWriter.write(System.lineSeparator().getBytes(), 0, System.lineSeparator().getBytes().length);
					posizione=posizione+(offsets[lineaAttuale]-offsets[lineaAttuale-1]);
					lineaAttuale++;
				}
				
				
				FReaderWriter.write(stringa1.getBytes(), 0, stringa1.getBytes().length);
			}
			else { 
				//inizializzo buffer di appoggio per la lettura
				byte buffer1[]=new byte[MAX_STRING_LENGTH];
				byte buffer2[]=new byte[MAX_STRING_LENGTH];
				//mi posiziono per scrivere la riga 1 al posto della riga 2
				FReaderWriter.seek((long)offsets[linea2-1]+differenza);
				FReaderWriter.write(stringa1.getBytes(), 0,stringa1.getBytes().length);
				//muovo il pointer alla file della riga 1 
				FReaderWriter.seek((long)offsets[linea1]-LSLength);
				//salvo i byte che verranno sovrascritti
				FReaderWriter.read(buffer1,0,differenza);
				//riposizionamento e rescrittura del file 
				FReaderWriter.seek((long)offsets[linea1-1]);
				FReaderWriter.write(stringa2.getBytes(), 0,stringa2.getBytes().length);
				
				//ciclo di riscrittura del file (sezione linea1-linea2)
				while(FReaderWriter.getFilePointer()<(long)offsets[linea2-1]+differenza) {
					FReaderWriter.read(buffer2, 0, differenza);
					FReaderWriter.seek(FReaderWriter.getFilePointer()-differenza);
					FReaderWriter.write(buffer1, 0,differenza);
					buffer1=Arrays.copyOf(buffer2, buffer2.length);
					
				}
				
			
			
			}
			
			FReaderWriter.close();
			
			
		}catch(FileNotFoundException e){
			
			System.out.println("file non trovato");
			e.printStackTrace();
		
		}catch(IOException e){

			System.out.println("errore nell'interazione con il file");
			e.printStackTrace();
		}
		
		System.out.println("FINE");
	}

}
