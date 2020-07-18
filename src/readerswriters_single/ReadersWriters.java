package readerswriters_single;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class ReadersWriters {

    private static final int READER_COUNT = 1;
    private static final int WRITER_COUNT = 1;

    private static Path file;
    private static Lock readersLock;
    private static Lock writeLock;
    private static AtomicInteger readerCounter;

    public static void main(String[] args) throws IOException {
        readersLock = new ReentrantLock();
        writeLock           = new ReentrantLock();
        file = Paths.get("test.txt");
        // Datei leeren
        Files.write(file, new byte[0], StandardOpenOption.TRUNCATE_EXISTING);
        readerCounter = new AtomicInteger(0);

        var reader = new Reader(file, readersLock, writeLock, readerCounter);
        var writer = new Writer(file, writeLock);

        List<Thread> threadList = new ArrayList<>();
        makeThreads(threadList, reader, READER_COUNT);
        makeThreads(threadList, writer, WRITER_COUNT);

        Collections.shuffle(threadList);

        threadList.forEach(Thread::start);
    }

    private static void makeThreads(List<Thread> threadList, Runnable runnable, int threadCount) {
        IntStream.iterate(0, n -> n < threadCount, n -> n + 1)
                .forEach(i -> threadList.add(new Thread(runnable)));
    }

}
