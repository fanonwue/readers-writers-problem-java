package readerswriters_nostarve;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Semaphore;

public class Writer implements Runnable {
    private final Semaphore writeLock;
    private final Semaphore orderLock;
    private final Path file;

    public Writer(Path file, Semaphore writeLock, Semaphore orderLock) {
        this.file = file;
        this.writeLock = writeLock;
        this.orderLock = orderLock;
    }

    @Override
    public void run() {
        try {
            while (true) {
                write();
                // Etwas warten
                Thread.sleep(Math.round(1000 * Math.random()));
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void write() throws IOException {
        // Warten, bis keine Reader mehr aktiv sind
        try {
            // Ankunftsreihenfolge merken
            orderLock.acquire();
            writeLock.acquire();
            // Wir wurden "bedient", also Sperre lösen
            orderLock.release();
            Files.writeString(
                    file,
                    Thread.currentThread().getName() + System.lineSeparator(),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.APPEND
            );
            writeLock.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
