import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.StringJoiner;

public class Swapper {
  final static String COMPLETE_PATH = "D:\\OneDrive - Alma Mater Studiorum Università di Bologna\\Sync\\Reti_Calcolatori\\Java\\Esercitazione1\\src\\test.txt";
  final static String WRITE_PATH = "D:\\OneDrive - Alma Mater Studiorum Università di Bologna\\Sync\\Reti_Calcolatori\\Java\\Esercitazione1\\src\\result.txt";

  public static void main(String[] args) throws IOException {
	String nomeFile=COMPLETE_PATH;
    BufferedReader stdIn =
      new BufferedReader(new InputStreamReader(System.in));
    System.out.println("\n^D(Unix)/^Z(Win) invio per uscire, " +
      "altrimenti inserisci nome file");
    System.out.println("Inserisci le due righe");
    int riga1 = 0;
    int riga2 = 0;
    try {
      riga1 = Integer.parseInt(stdIn.readLine());
    } catch (NumberFormatException e) {
      System.err.println("Errore inserimento riga 1!");
    }
    try {
      riga2 = Integer.parseInt(stdIn.readLine());
    } catch (NumberFormatException e) {
      System.err.println("Errore inserimento riga 2!");
    }
    System.out.println("Swappo riga " + riga1 + " e " + riga2);
	long start = System.currentTimeMillis();
    boolean res = swapper(riga1, riga2, nomeFile);
    long elapsedTimeMillis = System.currentTimeMillis()-start;
    System.out.print("Esito: " + res + " Tempo impiegato : "+elapsedTimeMillis/1000F);
  }

  public static boolean swapper(int riga1, int riga2, String nome) throws IOException {
    String r1;
    String result1 = null, result2 = null;
    String tmp1 = null, tmp2 = null;
    StringJoiner sj = new StringJoiner(System.lineSeparator());
    int i = 0;
    BufferedReader in = null;
    FileWriter fileWriter = new FileWriter(WRITE_PATH);
    PrintWriter printWriter = new PrintWriter(fileWriter); in = ReadFile(COMPLETE_PATH);

    while ((r1 = in .readLine()) != null || (result2 == null && result1 == null) ) {
      i++;
      if (riga1 == i) {
        result1 = r1;
      } else if (riga2 == i) {
        result2 = r1;
      }
    } in.close();

    in = ReadFile(COMPLETE_PATH);
    i = 0;
    while ((r1 = in .readLine()) != null) {
      i++;
      if (riga1 == i) {
        System.out.println("Scrivo " + result2);
        if (result2 != null) sj.add(result2);
        tmp1 = result2;
      } else if (riga2 == i) {
        System.out.println("Scrivo " + result1);
        if (result1 != null) sj.add(result1);
        tmp2 = result1;
      } else sj.add(r1);
    } in .close();

    System.out.println("Riga " + riga1 + " = " + result1 + "\nRiga " + riga2 + " = " + result2);
    System.out.println("tmp = " + tmp1 + " tmp2 = " + tmp2);

//    System.out.println(sj.toString());
    printWriter.print(sj.toString());
    printWriter.close();
    return (result1.equals(tmp2) && result2.equals(tmp1));

  }
  public static BufferedReader ReadFile(String path) {
    BufferedReader in = null;
    try {
      //simulo lettura da nomefile
      in = new BufferedReader(new FileReader(COMPLETE_PATH));
    } catch (FileNotFoundException e) {
      System.err.print("Errore creazione BufferedReader");
    }
    return in;
  }
}