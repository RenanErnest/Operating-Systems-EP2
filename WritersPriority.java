import java.util.*;

class CriticalRegionWritersPriority {
    String[] words;
    int readers = 0;
    int writers = 0;

    public CriticalRegionWritersPriority(String[] words) {
        this.words = words;
    }

    public String[] read() {
        synchronized(this)
        {
            while (this.writers != 0) {
                try {
                    this.wait();
                }
                catch (InterruptedException e) {}
            }
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
        this.writers++;

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

        this.writers--;
        this.notifyAll();
    }
}

class ReaderWP extends Thread {

    CriticalRegionWritersPriority crit;

    public ReaderWP(CriticalRegionWritersPriority crit) {
        this.crit = crit;
    }

    public void run() {
        System.out.println("Run da thread " +  this.getName());
        String[] read = crit.read();
        System.out.println("A thread " + this.getName() + " leu");
    }

}

class WriterWP extends Thread {

    CriticalRegionWritersPriority crit;

    public WriterWP(CriticalRegionWritersPriority crit) {
        this.crit = crit;
    }

    public void run() {
        System.out.println("Run da thread " +  this.getName());
        crit.write();
        System.out.println("A thread " + this.getName() + " modificou");
    }
    
}

class WritersPriority{

    public String[] words;
    public Thread[] readers_writers = new Thread[100];
    public CriticalRegionWritersPriority crit;
    public int readers_num;
    public int writers_num;

    public WritersPriority(String[] words, int readers, int writers) {
        this.words = words;
        this.crit = new CriticalRegionWritersPriority(words);
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
            readers_writers[numbers.get(k)] = new ReaderWP(crit);
        }
        for(int k = readers_num; k < readers_num + writers_num; k++) // criar writers_num writers (começa no readers_num e vai até readers_num + writers_num: 100)
        {
            readers_writers[numbers.get(k)] = new WriterWP(crit);
        }

        // executando as threads
        for(int i = 0; i < 100 ; i++)
        {
            readers_writers[i].start();
        }
    }
    
}

