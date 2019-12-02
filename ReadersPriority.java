import java.util.*;

class CriticalRegionReadersPriority{
    String[] words;
    int readers = 0;

    public CriticalRegionReadersPriority(String[] words) {
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
            Thread.sleep (1);
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
            Thread.sleep (1);
        }
        catch (InterruptedException e){}
        this.notifyAll();
    }
}

class ReaderRP implements Runnable {

    CriticalRegionReadersPriority crit;

    public ReaderRP(CriticalRegionReadersPriority crit) {
        this.crit = crit;
    }

    public void run() {
        String[] read = crit.read();
        //System.out.println(this.getName() + " leu");
    }

}

class WriterRP implements Runnable  {

    CriticalRegionReadersPriority crit;

    public WriterRP(CriticalRegionReadersPriority crit) {
        this.crit = crit;
    }

    public void run() {
        crit.write();
        //System.out.println(this.getName() + " escreveu");
    }
    
}

class ReadersPriority{

    public String[] words;
    public Thread[] readers_writers = new Thread[100];
    public CriticalRegionReadersPriority crit;
    public int readers_num;
    public int writers_num;
    ArrayList<Integer> numbers = new ArrayList<Integer>();
    ///////////
    Runnable[] writers_pool = new Runnable[100];
    Runnable[] readers_pool = new Runnable[100];
    ///////////

    public ReadersPriority(String[] words) {
        this.words = words;
        this.crit = new CriticalRegionReadersPriority(words);
        for (int i = 0; i < 100; i++) 
        {
            readers_pool[i] = new ReaderRP(crit);
            writers_pool[i] = new WriterRP(crit);
            numbers.add(new Integer(i)); 
        }
    }

    public void bake(int readers, int writers) {
        this.readers_num = readers;
        this.writers_num = writers;
        
        // embaralhamento das threads
        Collections.shuffle(numbers);
        
        // preenchendo threads em ordem aleatoria seguindo as proporcoes
        for(int k = 0; k < readers_num; k++) // criar readers_num readers
        {
            readers_writers[k] = new Thread(readers_pool[k]); 
        }
        for(int k = readers_num; k < readers_num + writers_num; k++) // criar writers_num writers (começa no readers_num e vai até readers_num + writers_num: 100)
        {
            readers_writers[k] = new Thread(writers_pool[k]);
        }
    }

    public void execute() {
        // executando as threads
        for (Thread thread : readers_writers) {
            try{
            thread.start();
            }
            catch (Exception e){}
        }
    }
    
}

