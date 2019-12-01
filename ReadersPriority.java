import java.util.*;

class CriticalRegionReadersPriority {
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

class ReaderRP extends Thread {

    CriticalRegionReadersPriority crit;

    public ReaderRP(CriticalRegionReadersPriority crit) {
        this.crit = crit;
    }

    public void run() {
        String[] read = crit.read();
        //System.out.println(this.getName() + " leu");
    }

}

class WriterRP extends Thread {

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

    public ReadersPriority(String[] words, int readers, int writers) {
        this.words = words;
        this.crit = new CriticalRegionReadersPriority(words);
        this.readers_num = readers;
        this.writers_num = writers;
    }

    public void execute() {
        
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
            readers_writers[numbers.get(k)] = new ReaderRP(crit);
        }
        for(int k = readers_num; k < readers_num + writers_num; k++) // criar writers_num writers (começa no readers_num e vai até readers_num + writers_num: 100)
        {
            readers_writers[numbers.get(k)] = new WriterRP(crit);
        }

        // executando as threads
        for(int i = 0; i < 100 ; i++)
        {
            readers_writers[i].start();
        }
    }
    
}

