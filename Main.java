import java.io.*;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.*;


class CriticalRegion {
    String[] words;
    int readers = 0;

    public CriticalRegion(String[] words) {
        this.words = words;
    }

    public String[] read() {
        synchronized(this)
        {
            this.readers++;
        }
        
        String[] read = new String[100];
        Random rand = new Random();
        for(int i = 0; i < 100; i++) {
            int j = rand.nextInt(words.length);
            read[i] = words[j];
        }

        //DELAY
        try{
            Thread.sleep (1000);
        }
        catch (InterruptedException e){}
     
        synchronized(this)
        {
            this.readers--;
            if (this.readers == 0)
            {
            this.notifyAll();
            }
        }
        return read;
    }

    public synchronized void write()
    {
        while (this.readers != 0) {
            try {
                this.wait();
            }
            catch (InterruptedException e) {}
        }

        Random rand = new Random();
        for(int i = 0; i < 100; i++) {
            int j = rand.nextInt(words.length);
            words[j] = "MODIFICADO";
        }

        //DELAY
        try{
            Thread.sleep (1000);
        }
        catch (InterruptedException e){}

        this.notifyAll();
    }
}

class Reader extends Thread implements Common{
    boolean isReader = true;
    CriticalRegion crit;

    public Reader(CriticalRegion crit) {
        this.crit = crit;
    }

    public void run() {
        String[] read = crit.read();
        System.out.println("A thread " + this.getName() + " leu");
    }

    public boolean isReader() { return isReader;}
}

class Writer extends Thread implements Common{
    boolean isReader = false;
    CriticalRegion crit;

    public Writer(CriticalRegion crit) {
        this.crit = crit;
    }

    public void run() {
        crit.write();
        System.out.println("A thread " + this.getName() + " modificou");
    }
    public boolean isReader() { return isReader;}
}

interface Common
{
    public boolean isReader();
    public String getName();
    public void start();
}

class Main{

    public static String[] words = new String[36242];
    public static Common[] readers_writers = new Common[100];
    public static CriticalRegion crit = new CriticalRegion(words);
    public static int readers_num = 50;
    public static int writers_num = 50;

    public static void main(String[] args) {
        //Leitura de arquivo
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


        ArrayList<Integer> numbers = new ArrayList<Integer>();
        for (int i = 0; i < 100; i++) 
        {
            numbers.add(new Integer(i)); 
        }
        Collections.shuffle(numbers);
        
        for(int k = 0; k < readers_num; k++) // criar readers_num readers
        {
            readers_writers[numbers.get(k)] = new Reader(crit);
        }

        for(int k = readers_num; k < readers_num + writers_num; k++) // criar writers_num writers (começa no readers_num e vai até readers_num + writers_num: 100)
        {
            readers_writers[numbers.get(k)] = new Writer(crit);
        }

        for(int i = 0; i < 100 ; i++)
        {
            if(readers_writers[i].isReader() == false){
                System.out.println("Iniciando write num " + i + " " + readers_writers[i].getName());
                readers_writers[i].start();
            }
            else{
                System.out.println("Iniciando read num " + i + " " + readers_writers[i].getName());
                readers_writers[i].start();
            }
        }
        
    }
    
}

