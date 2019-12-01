import java.util.*;

class CriticalRegionUnique {
    String[] words;
    int readers = 0;
    int writers = 0;

    public CriticalRegionUnique(String[] words) {
        this.words = words;
    }

    public synchronized String[] read() {
        
        String[] read = new String[100];
        Random rand = new Random();
        for(int i = 0; i < 100; i++) {
            int j = rand.nextInt(words.length);
            read[i] = words[j];
        }

        //DELAY
        try{
            Thread.sleep (1);
        }
        catch (InterruptedException e){}
     
        return read;
    }

    public synchronized void write()
    {
        Random rand = new Random();
        for(int i = 0; i < 100; i++) {
            int j = rand.nextInt(words.length);
            words[j] = "MODIFICADO";
        }

        //DELAY
        try{
            Thread.sleep (1);
        }
        catch (InterruptedException e){}

        this.notifyAll();
    }
}

class ReaderUnique extends Thread {

    CriticalRegionUnique crit;

    public ReaderUnique(CriticalRegionUnique crit) {
        this.crit = crit;
    }

    public void run() {
        String[] read = crit.read();
        //System.out.println(this.getName() + " leu");
    }

}

class WriterUnique extends Thread {

    CriticalRegionUnique crit;

    public WriterUnique(CriticalRegionUnique crit) {
        this.crit = crit;
    }

    public void run() {
        crit.write();
        //System.out.println(this.getName() + " escreveu");
    }
    
}

class UniqueAccess{

    public String[] words;
    public Thread[] readers_writers = new Thread[100];
    public CriticalRegionUnique crit;
    public int readers_num;
    public int writers_num;

    public UniqueAccess(String[] words, int readers, int writers) {
        this.words = words;
        this.crit = new CriticalRegionUnique(words);
        this.readers_num = readers;
        this.writers_num = writers;
    }

    public void bake() {

        // embaralhamento das threads
        ArrayList<Integer> numbers = new ArrayList<Integer>();
        for (int i = 0; i < 100; i++) 
        {
            numbers.add(new Integer(i)); 
        }
        Collections.shuffle(numbers);
        
        // preenchendo threads em ordem aleatoria seguindo as proporcoes
        for(int k = 0; k < readers_num; k++) // criar readers_num readers
        {
            readers_writers[numbers.get(k)] = new ReaderUnique(crit);
        }
        for(int k = readers_num; k < readers_num + writers_num; k++) // criar writers_num writers (começa no readers_num e vai até readers_num + writers_num: 100)
        {
            readers_writers[numbers.get(k)] = new WriterUnique(crit);
        }
    }

    public void execute() {
        // executando as threads
        for(int i = 0; i < 100 ; i++)
        {
            readers_writers[i].start();
        }
    }
    
}

