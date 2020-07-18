package readerswriters_single;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

public class Reader implements Runnable {
    private final Lock readersLock;
    private final Path file;
    private final Lock writeLock;
    private final AtomicInteger readerCounter;

    public Reader(Path file, Lock mutex, Lock writeLock, AtomicInteger readerCounter) {
        this.file = file;
        this.readersLock = mutex;
        this.writeLock = writeLock;
        this.readerCounter = readerCounter;
    }

    public void run() {
        try {
            while (true) {
                read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void read() throws IOException {
        // Warten, bis Datei frei ist
        readersLock.lock();
        if (readerCounter.incrementAndGet() == 1) writeLock.lock();
        readersLock.unlock();
        // Datei lesen und mit Zusatzinfos ausgeben
        System.out.println(Thread.currentThread().getName() + ": " + Files.readString(file));
        readersLock.lock();
        // Freigeben, wenn letzter Reader
        if (readerCounter.decrementAndGet() == 0) writeLock.unlock();
        readersLock.unlock();
    }

}
