

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

public class TestRandomAccessFile {
	public static final int LINEA_1=0;
	public static final int LINEA_2=1;
	public static void main(String[] args) {
		
		//dichiarazioni e inizializzazioni
		int LSLength=System.lineSeparator().getBytes().length;
		int numLinee=0;
		long[] offsets= new long[2];
		String stringa1;
		String stringa2;
		int str1Length;
		int str2Length;
		int linea1=1;
		int linea2=5;
		stringa1="";stringa2="";
		str1Length=0; str2Length=0;
		
		try {
		
			RandomAccessFile FReaderWriter= new RandomAccessFile("C:\\Users\\matti\\Documents\\GitHub\\lab-reti-di-calcolatori\\prova.txt", "rw");
			numLinee++;
			String strtmp;
			
			
			while((strtmp=FReaderWriter.readLine())!=null) {
				
				
				if(numLinee==linea1) {
					
					stringa1=strtmp;
					str1Length=stringa1.length();
					offsets[LINEA_1]=FReaderWriter.getFilePointer()-(str1Length+LSLength);
				}
				
				if(numLinee==linea2) {
				
					stringa2=strtmp;
					str2Length=stringa2.length();
					offsets[LINEA_2]=FReaderWriter.getFilePointer()-(str2Length+LSLength);
				}
				
				numLinee++;
			}
			if(stringa1=="" || stringa2=="")System.exit(-1);
				//calcolo differenza tra le stringhe
				int differenza=(str1Length>str2Length)?str1Length-str2Length:str2Length-str1Length;
				////////////hardcoding delle linee per debug
				linea1=1;
				linea2=4;
				long posizione=0; //nuovo offsets
				int lineaAttuale=0;
				
				
				if (str1Length>=str2Length) { //scrivo dalla prima linea
				
					lineaAttuale=linea1;
					FReaderWriter.seek((long)offsets[LINEA_1]);
					FReaderWriter.write(stringa2.getBytes(), 0, str2Length);
					FReaderWriter.write(System.lineSeparator().getBytes(), 0, LSLength);
					posizione=(long)(offsets[LINEA_1]+str1Length+LSLength-differenza);
					//perchè ho swappato le linee e devo scrivere a partire dalla fine della seconda
					lineaAttuale++;
					while(FReaderWriter.getFilePointer()<(long)offsets[LINEA_2]) {
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
					byte buffer1[]=new byte[differenza];
					byte buffer2[]=new byte[differenza];
					//mi posiziono per scrivere la riga 1 al posto della riga 2
					FReaderWriter.seek((long)offsets[LINEA_2]+differenza);
					FReaderWriter.write(stringa1.getBytes(), 0,str1Length);
					//muovo il pointer alla file della riga 1 
					FReaderWriter.seek((long)offsets[LINEA_1]+str1Length);
					//salvo i byte che verranno sovrascritti
					FReaderWriter.read(buffer1,0,differenza);
					//riposizionamento e rescrittura del file 
					FReaderWriter.seek((long)offsets[LINEA_1]);
					FReaderWriter.write(stringa2.getBytes(), 0,str2Length);
					
					//ciclo di riscrittura del file (sezione linea1-linea2)
					while(FReaderWriter.getFilePointer()<(long)offsets[LINEA_2]+differenza) {
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
