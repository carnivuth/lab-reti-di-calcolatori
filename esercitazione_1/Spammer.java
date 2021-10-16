import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Spammer {
public static void main(String[] args) throws IOException {
	final String WRITE_PATH = "D:\\OneDrive - Alma Mater Studiorum Università di Bologna\\Sync\\Reti_Calcolatori\\Java\\Esercitazione1\\src\\test.txt";
	FileWriter fileWriter = new FileWriter(WRITE_PATH);
    PrintWriter printWriter = new PrintWriter(fileWriter);
    for(int i = 1; i <= 100000 ; i++) {
    printWriter.println("Linea : "+i);
    }
    printWriter.close();
}
}
