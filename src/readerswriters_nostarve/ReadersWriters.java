package readerswriters_nostarve;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class ReadersWriters {

    private static final int READER_COUNT = 6;
    private static final int WRITER_COUNT = 3;

    private static Path file;
    private static Semaphore readersLock;
    private static Semaphore writeLock;
    private static Semaphore orderLock;
    private static AtomicInteger readerCounter;

    public static void main(String[] args) throws IOException {
        readersLock = new Semaphore(1);
        writeLock           = new Semaphore(1);
        orderLock           = new Semaphore(1);
        file = Paths.get("test.txt");
        // Datei leeren
        Files.write(file, new byte[0], StandardOpenOption.TRUNCATE_EXISTING);
        readerCounter = new AtomicInteger(0);

        var reader = new Reader(file, readersLock, writeLock, orderLock, readerCounter);
        var writer = new Writer(file, writeLock, orderLock);

        List<Thread> threadList = new ArrayList<>();
        makeThreads(threadList, reader, READER_COUNT);
        makeThreads(threadList, writer, WRITER_COUNT);

        Collections.shuffle(threadList);

        // Alle Threads starten
        threadList.forEach(Thread::start);
    }

    private static void makeThreads(List<Thread> threadList, Runnable runnable, int threadCount) {
        IntStream.iterate(0, n -> n < threadCount, n -> n + 1)
                .forEach(i -> threadList.add(new Thread(runnable)));
    }

}
