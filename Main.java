import java.io.*;

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