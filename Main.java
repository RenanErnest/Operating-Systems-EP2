import java.io.*;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.*;

class Reader extends Thread implements Common{
    boolean isReader = true;
    public void run(String[] words) {
        String[] read = new String[100];
        Random rand = new Random();
        for(int i = 0; i < 100; i++) {
            int j = rand.nextInt(words.length);
            read[i] = words[j];
        }
        try{
            Thread.sleep (1);
        }
        catch (InterruptedException e){}
    }
    public boolean isReader() { return isReader;}
}

class Writer extends Thread implements Common{
    boolean isReader = false;
    public void run(String[] words) {
        Random rand = new Random();
        for(int i = 0; i < 100; i++) {
            int j = rand.nextInt(words.length);
            words[j] = "MODIFICADO";
        }
        try{
            Thread.sleep (1);
        }
        catch (InterruptedException e){}
    }
    public boolean isReader() { return isReader;}
}

class Main{

    public static String[] words = new String[36242];
    public static Common[] readers_writers = new Common[100];
    public static Semaphore mutex = new Semaphore(1);
    public static Semaphore wrt = new Semaphore(1);
    public static int readcnt;
    public static int readers_num = 50;
    public static int writers_num = 50;

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

            reader.close();
        }
        catch(Exception e) {
            System.out.println("Não foi possível encontrar o arquivo 'bd.txt'");
        }
        for(int i = 0; i < words.length; i++) {
            System.out.print(words[i] + " ");
            if (i%15 == 0) System.out.println();
        }

        //importa collections
        ArrayList<Integer> numbers = new ArrayList<Integer>();
        Collections.shuffle(numbers);

        for (int i = 0; i < 100; i++) 
        {
            numbers.add(new Integer(i)); 
        }
        
        for(int k = 0; k < readers_num; k++) // criar readers_num readers
        {
            readers_writers[numbers.get(k)] = new Reader();
        }

        for(int k = readers_num; k < readers_num + writers_num; k++) // criar writers_num writers (começa no readers_num e vai até readers_num + writers_num: 100)
        {
            readers_writers[numbers.get(k)] = new Writer();
        }


        //a gente tem que inicializar o semaforo, mas ele leva um parametro que eu n entendi muito ainda
        //https://www.geeksforgeeks.org/semaphore-in-java/
        //acho que o mutex é 1, o outro eu n sei, parece que é 1 tbm, bora testar kkkk
        for(int i = 0; i < 100 ; i ++)
        {
            if(readers_writers[i].isReader() == false){
                //is writer
                do {
                    // writer requests for critical section
                    try { wrt.acquire();  } catch(Exception e) { System.out.println("A thread " + readers_writers[i].getName() + " foi interrompida"); } 
                    // performs the write
                    readers_writers[i].run(words);
                    // leaves the critical section
                    wrt.release();
                } while(true);
            }
            else{
                //isso se reader
                do {
        
                    // Reader wants to enter the critical section
                    try { mutex.acquire();  } catch(Exception e) { System.out.println("A thread " + readers_writers[i].getName() + " foi interrompida"); }
                
                    // The number of readers has now increased by 1
                    readcnt++;                          
                
                    // there is atleast one reader in the critical section
                    // this ensure no writer can enter if there is even one reader
                    // thus we give preference to readers here
                    if (readcnt==1)  
                    try { wrt.acquire();  } catch(Exception e) { System.out.println("A thread " + readers_writers[i].getName() + " foi interrompida"); }              
                
                    // other readers can enter while this current reader is inside 
                    // the critical section
                    mutex.release();           
                
                    // current reader performs reading here
                    readers_writers[i].run(words);
                    
                    // a reader wants to leave 
                    try { mutex.acquire();  } catch(Exception e) { System.out.println("A thread " + readers_writers[i].getName() + " foi interrompida"); }                
                
                    readcnt--;
                
                    // that is, no reader is left in the critical section,
                    if (readcnt == 0) 
                        wrt.release();         // writers can enter

                    mutex.release(); // reader leaves
                }
                    while(true);
            }
        }
        
    }

    //https://www.geeksforgeeks.org/readers-writers-problem-set-1-introduction-and-readers-preference-solution/
    //usar esse algoritmo aqui, pode ser?

    //fechar o leitor de arquivo, eu n lembro, simples assim kkk kkkkkk
    
    
}

interface Common
{
    public boolean isReader();
    public void run(String[] words);
    public String getName();
}