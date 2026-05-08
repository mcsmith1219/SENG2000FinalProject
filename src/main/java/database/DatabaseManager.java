package database;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Class:
 * DatabaseManager() - manages database directory and file storage initialization.
 *
 * Methods:
 * initializeDatabase() - creates the data directory if it doesn't exist.
 * getDataDirectory() - returns the path to the data directory.
 **/

public class DatabaseManager {
    private static final Path DATA_DIR = Paths.get("app_data");

    public void initializeDatabase() {
        try {
            Files.createDirectories(DATA_DIR);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize file storage.", e);
        }
    }

    public Path getDataDirectory() {
        return DATA_DIR;
    }
}
