package readerswriters_nostarve;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Reader implements Runnable {
    private final Semaphore readersLock;
    private final Semaphore orderLock;
    private final Path file;
    private final Semaphore writeLock;
    private final AtomicInteger readerCounter;

    public Reader(Path file, Semaphore readersLock, Semaphore writeLock, Semaphore orderLock, AtomicInteger readerCounter) {
        this.file = file;
        this.readersLock = readersLock;
        this.writeLock = writeLock;
        this.readerCounter = readerCounter;
        this.orderLock = orderLock;
    }

    public void run() {
        try {
            while (true) {
                read();
                // Etwas warten
                Thread.sleep(Math.round(1000 * Math.random()));
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void read() throws IOException {
        try {
            // Ankunftsreihenfolge merken
            orderLock.acquire();
            // Warten, bis Datei frei ist
            readersLock.acquire();
            if (readerCounter.incrementAndGet() == 1) writeLock.acquire();
            // Wir wurden "bedient", also Sperre lösen
            orderLock.release();
            readersLock.release();
            // Datei lesen und mit Zusatzinfos ausgeben
            System.out.println(Thread.currentThread().getName() + ": " + Files.readString(file));
            readersLock.acquire();
            if (readerCounter.decrementAndGet() == 0) writeLock.release();
            readersLock.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
