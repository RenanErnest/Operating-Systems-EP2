import java.io.*;

class Main {

    static long part1 = 0;
    static long part2 = 0;
    static long part3 = 0;
    static long part1_total = 0;
    static long part2_total = 0;
    static long part3_total = 0;

    public static String[] words = new String[36242];

    public static long part1() {

        ReadersPriority proporcao = new ReadersPriority(words);

        for(int i = 0; i < 100; i++) {
            part1 = 0;

            for(int j = 0; j < 50; j++) {
                proporcao.bake(i, 100-i);
                long p = System.currentTimeMillis();
                proporcao.execute();
                for (Thread thread : proporcao.readers_writers) {
                    try{
                    thread.join();
                    } catch (Exception e){e.printStackTrace();}

                }
                p = System.currentTimeMillis() - p;
                part1 += p;
            }

            part1 /= 50;
            part1_total += part1;
            System.out.println("Prioridade para leitores. Leitores:" + i + " escritores:" + (100-i)  + " média de tempo:" + part1);
        }

        return part1_total;
    }

    public static void part2() {
        for(int i = 0; i < 100; i++) {
            WritersPriority proporcao = new WritersPriority(words, i, 100-i);
            part2 = 0;
            
            for(int j = 0; j < 50; j++) {
                proporcao.bake();
                long p = System.currentTimeMillis();
                proporcao.execute();
                for (Thread thread : proporcao.readers_writers) {
                    try{
                    thread.join();
                    } catch (Exception e){}
                }
                p = System.currentTimeMillis() - p;
                part2 += p;
            }

            part2 /= 50;
            part2_total += part2;
            System.out.println("Prioridade para escritores. Leitores:" + i + " escritores:" + (100-i) + " média de tempo:" + part2);
        }

        part2_total /= 100;
        System.out.println("Prioridade para escritores. MEDIA TOTAL:" + part2_total);
    }

    public static void part3() {
        for(int i = 0; i < 100; i++) {
            UniqueAccess proporcao = new UniqueAccess(words, i, 100-i);
            part3 = 0;
            
            for(int j = 0; j < 50; j++) {
                proporcao.bake();
                long p = System.currentTimeMillis();
                proporcao.execute();
                for (Thread thread : proporcao.readers_writers) {
                    try{
                    thread.join();
                    } catch (Exception e){}

                }
                p = System.currentTimeMillis() - p;
                part3 += p;
            }

            part3 /= 50;
            part3_total += part3;
            System.out.println("Sem prioridade. Leitores:" + i + " escritores:" + (100-i) + " média de tempo:" + part3);
        }

        part3_total /= 100;
        System.out.println("Sem prioridade. MEDIA TOTAL:" + part3_total);

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

        //long media_total1 = part1();
        //System.out.println("Prioridade para leitores. MEDIA TOTAL:" + (media_total1/100));
        part2();
        //part3();
    }
}