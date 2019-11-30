import java.io.*;
import java.util.Random;

class Reader extends Thread{
    public void run(String[] words) {
        String[] read = new String[100];
        Random rand = new Random();
        for(int i = 0; i < 100; i++) {
            int j = rand.nextInt(words.length);
            read[i] = words[j];
        }
        //dormir por 1ms
    }
}

class Writer extends Thread{
    public void run(String[] words) {
        Random rand = new Random();
        for(int i = 0; i < 100; i++) {
            int j = rand.nextInt(words.length);
            words[j] = "MODIFICADO";
        }
        //dormir por 1ms
    }
}

class Main{

    public static String[] words = new String[36242];

    public static void main(String[] args) {
        try{
            FileReader arq = new FileReader("bd.txt");
            BufferedReader reader = new BufferedReader(arq);

            int i = 0;
            while(true) {
                String line = reader.readLine();
                if (line == null) break;
                words[i] = line;
                i++;
            }
        }
        catch(Exception e) {
            System.out.println("Não foi possível encontrar o arquivo 'bd.txt'");
        }
        for(int i = 0; i < words.length; i++) {
            System.out.print(words[i] + " ");
            if (i%15 == 0) System.out.println();
        }
    }
}