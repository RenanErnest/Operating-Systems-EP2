import java.io.*;

class Main {

    public static String[] words = new String[36242];

    public static void part1() {
        for(int i = 0; i < 100; i++) {
            ReadersPriority proporcao = new ReadersPriority(words, i, 100-i);
            
            for(int j = 0; j < 50; j++) {
                proporcao.execute();
            }
        }
    }

    public static void part2() {
        for(int i = 0; i < 100; i++) {
            UniqueAccess proporcao = new UniqueAccess(words, i, 100-i);
            
            for(int j = 0; j < 50; j++) {
                proporcao.execute();
            }
        }
    }

    public static void main(String[] args) {
         // Leitura de arquivo
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

            reader.close();
        }
        catch(Exception e) {
            System.out.println("Não foi possível encontrar o arquivo 'bd.txt'");
        }

        part1();

        part2();
    }
}