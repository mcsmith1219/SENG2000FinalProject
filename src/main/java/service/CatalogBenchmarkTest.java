package service;
import database.DatabaseManager;
import database.GameRepository;
import model.RecordGame;
import java.util.List;

/**
 * Class:
 * CatalogBenchmarkTest() - test runner that executes catalog benchmark with game data from GameRepository.
 *
 * Methods:
 * main(String[] args) - loads games from database and runs benchmark suite.
 * runBenchmarkFromApp(GameRepository gameRepository) - alternative method to run benchmark from application code.
 **/

public class CatalogBenchmarkTest {

    public static void main(String[] args) {
        System.out.println("Initializing Catalog Benchmark...");
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.initializeDatabase();
        GameRepository gameRepository = new GameRepository(dbManager);
        List<RecordGame> games = gameRepository.getAllGames();
        if (games.isEmpty()) {
            System.out.println("ERROR: No games found in database!");
            System.out.println("Please ensure app_data/games.tsv exists and has game data.");
            return;
        }
        System.out.println("Loaded " + games.size() + " games from database.");
        System.out.println("");
        CatalogBenchmark benchmark = new CatalogBenchmark();
        benchmark.runAllBenchmarks(games);
    }

    public static void runBenchmarkFromApp(GameRepository gameRepository) {
        List<RecordGame> games = gameRepository.getAllGames();
        if (!games.isEmpty()) {
            System.out.println("\n========== Running Catalog Benchmark ==========\n");
            CatalogBenchmark benchmark = new CatalogBenchmark();
            benchmark.runAllBenchmarks(games);
            System.out.println("\nResults saved to: data_structures/catalog_benchmark.txt\n");
        }
    }
}
