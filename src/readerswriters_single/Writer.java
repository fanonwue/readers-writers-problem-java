package readerswriters_single;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.locks.Lock;

public class Writer implements Runnable {
    private final Lock writeLock;
    private final Path file;

    public Writer(Path file, Lock writeLock) {
        this.file = file;
        this.writeLock = writeLock;
    }

    @Override
    public void run() {
        try {
            while (true) {
                write();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write() throws IOException {
        // Warten, bis keine Reader mehr aktiv sind
        writeLock.lock();
        Files.writeString(
                file,
                Thread.currentThread().getName() + System.lineSeparator(),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND,
                StandardOpenOption.WRITE
        );
        writeLock.unlock();
    }
}
