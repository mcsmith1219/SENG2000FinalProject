package storage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Class:
 * FileStorage() - handles file I/O operations for persisting data to disk.
 *
 * Methods:
 * FileStorage(String) - constructor that initializes storage directory.
 * writeLines(String, List<String>) - writes list of strings to file.
 **/

public class FileStorage {
    private final Path baseDir;

    public FileStorage(String baseDirName) {
        this.baseDir = Paths.get(baseDirName);
        try {
            Files.createDirectories(baseDir);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create storage directory", e);
        }
    }

    public void writeLines(String fileName, List<String> lines) {
        try {
            Files.write(baseDir.resolve(fileName), lines);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write file: " + fileName, e);
        }
    }
}
