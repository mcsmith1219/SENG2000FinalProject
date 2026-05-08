package service;
import model.RecordGame;
import storage.FileStorage;
import java.util.*;

/**
 * Class:
 * CatalogBenchmark() - class to compare performance of CatalogArrayListImpl and CatalogHashMapImpl.
 * This class runs benchmarks for adding games, searching by name, checking existence by ID, and sorting.
 * Results are printed to console and saved to a file for analysis.
 *
 * Methods:
 * CatalogBenchmark() - constructor to initialize file storage and results list.
 * runAllBenchmarks(List<RecordGame> testGames) - runs all benchmarks on provided test games.
 * benchmarkAddGames(List<RecordGame> games) - benchmarks adding games to both implementations.
 * benchmarkSearch(List<RecordGame> games) - benchmarks searching by exact name.
 * benchmarkContainsSearch(List<RecordGame> games) - benchmarks checking existence by ID.
 * benchmarkSorting(List<RecordGame> games) - benchmarks sorting by title.
 * addResult(String line) - helper method to add a result line to the results list and print it.
 * saveResults() - helper method to save all results to a file.
 * main(String[] args) - main entry point for running benchmarks from command line.
 **/

public class CatalogBenchmark {
    private final FileStorage fileStorage;
    private List<String> results;

    public CatalogBenchmark() {
        this.fileStorage = new FileStorage("data_structures");
        this.results = new ArrayList<>();
    }

    public void runAllBenchmarks(List<RecordGame> testGames) {
        if (testGames == null || testGames.isEmpty()) {
            System.out.println("No test games provided for benchmarking.");
            return;
        }
        results.clear();
        addResult("============================================");
        addResult("CATALOG PERFORMANCE BENCHMARK");
        addResult("============================================");
        addResult("");
        int[] testSizes = {10, 50, 100, 500, 1000};
        List<RecordGame> scaledGames = new ArrayList<>(testGames);
        while (scaledGames.size() < testSizes[testSizes.length - 1]) {
            for (RecordGame game : new ArrayList<>(testGames)) {
                if (scaledGames.size() >= testSizes[testSizes.length - 1]) {
                    break;
                }
                RecordGame scaled = new RecordGame(
                        game.getRawgId() + scaledGames.size(),
                        game.getName() + " v" + (scaledGames.size() / testGames.size()),
                        game.getReleaseDate(),
                        game.getPersonalRating(),
                        game.getNumberOfRatings(),
                        game.getRatingRawg(),
                        game.getSynopsis(),
                        game.getImageUrl()
                );
                scaledGames.add(scaled);
            }
        }
        for (int size : testSizes) {
            List<RecordGame> testSet = scaledGames.subList(0, Math.min(size, scaledGames.size()));
            addResult("");
            addResult("--- Testing with " + testSet.size() + " games ---");
            benchmarkAddGames(testSet);
            benchmarkSearch(testSet);
            benchmarkContainsSearch(testSet);
            benchmarkSorting(testSet);
        }
        addResult("");
        addResult("============================================");
        addResult("Benchmark complete! Results saved.");
        addResult("============================================");
        saveResults();
    }

    private void benchmarkAddGames(List<RecordGame> games) {
        addResult("\nAdding " + games.size() + " games:");
        long startTime = System.nanoTime();
        CatalogArrayListImpl arrayList = new CatalogArrayListImpl();
        for (RecordGame game : games) {
            arrayList.addGame(game);
        }
        long arrayListTime = System.nanoTime() - startTime;
        startTime = System.nanoTime();
        CatalogHashMapImpl hashMap = new CatalogHashMapImpl();
        for (RecordGame game : games) {
            hashMap.addGame(game);
        }
        long hashMapTime = System.nanoTime() - startTime;
        addResult(String.format("  ArrayList: %,d ns (%.4f ms)", arrayListTime, arrayListTime / 1_000_000.0));
        addResult(String.format("  HashMap:   %,d ns (%.4f ms)", hashMapTime, hashMapTime / 1_000_000.0));
        addResult(String.format("  Speedup:   %.2fx", (double) arrayListTime / hashMapTime));
    }

    private void benchmarkSearch(List<RecordGame> games) {
        if (games.isEmpty()) return;
        addResult("\nSearching by exact game name (" + games.size() + " searches):");
        CatalogArrayListImpl arrayList = new CatalogArrayListImpl();
        CatalogHashMapImpl hashMap = new CatalogHashMapImpl();
        for (RecordGame game : games) {
            arrayList.addGame(game);
            hashMap.addGame(game);
        }
        long startTime = System.nanoTime();
        int arrayListHits = 0;
        for (RecordGame game : games) {
            if (arrayList.searchByName(game.getName()) != null) {
                arrayListHits++;
            }
        }
        long arrayListTime = System.nanoTime() - startTime;
        startTime = System.nanoTime();
        int hashMapHits = 0;
        for (RecordGame game : games) {
            if (hashMap.searchByName(game.getName()) != null) {
                hashMapHits++;
            }
        }
        long hashMapTime = System.nanoTime() - startTime;
        addResult(String.format("  ArrayList: %,d ns (%.4f ms) - %d hits", arrayListTime, arrayListTime / 1_000_000.0, arrayListHits));
        addResult(String.format("  HashMap:   %,d ns (%.4f ms) - %d hits", hashMapTime, hashMapTime / 1_000_000.0, hashMapHits));
        if (arrayListTime > 0) {
            addResult(String.format("  Speedup:   %.2fx", (double) arrayListTime / hashMapTime));
        }
    }

    private void benchmarkContainsSearch(List<RecordGame> games) {
        if (games.isEmpty()) return;
        addResult("\nChecking existence by ID (" + games.size() + " checks):");
        CatalogArrayListImpl arrayList = new CatalogArrayListImpl();
        CatalogHashMapImpl hashMap = new CatalogHashMapImpl();
        for (RecordGame game : games) {
            arrayList.addGame(game);
            hashMap.addGame(game);
        }
        long startTime = System.nanoTime();
        int arrayListHits = 0;
        for (RecordGame game : games) {
            if (arrayList.contains(game.getRawgId())) {
                arrayListHits++;
            }
        }
        long arrayListTime = System.nanoTime() - startTime;
        startTime = System.nanoTime();
        int hashMapHits = 0;
        for (RecordGame game : games) {
            if (hashMap.contains(game.getRawgId())) {
                hashMapHits++;
            }
        }
        long hashMapTime = System.nanoTime() - startTime;
        addResult(String.format("  ArrayList: %,d ns (%.4f ms) - %d hits", arrayListTime, arrayListTime / 1_000_000.0, arrayListHits));
        addResult(String.format("  HashMap:   %,d ns (%.4f ms) - %d hits", hashMapTime, hashMapTime / 1_000_000.0, hashMapHits));
        if (arrayListTime > 0) {
            addResult(String.format("  Speedup:   %.2fx", (double) arrayListTime / hashMapTime));
        }
    }

    private void benchmarkSorting(List<RecordGame> games) {
        if (games.isEmpty()) return;
        addResult("\nSorting by title (" + games.size() + " games):");
        CatalogArrayListImpl arrayList = new CatalogArrayListImpl();
        CatalogHashMapImpl hashMap = new CatalogHashMapImpl();
        for (RecordGame game : games) {
            arrayList.addGame(game);
            hashMap.addGame(game);
        }
        long startTime = System.nanoTime();
        List<RecordGame> arrayListSorted = arrayList.sortByTitle();
        long arrayListTime = System.nanoTime() - startTime;
        startTime = System.nanoTime();
        List<RecordGame> hashMapSorted = hashMap.sortByTitle();
        long hashMapTime = System.nanoTime() - startTime;
        addResult(String.format("  ArrayList: %,d ns (%.4f ms)", arrayListTime, arrayListTime / 1_000_000.0));
        addResult(String.format("  HashMap:   %,d ns (%.4f ms)", hashMapTime, hashMapTime / 1_000_000.0));
        addResult(String.format("  Difference: %.2f%%", Math.abs((double) (arrayListTime - hashMapTime) / arrayListTime * 100)));
    }

    private void addResult(String line) {
        results.add(line);
        System.out.println(line);
    }

    private void saveResults() {
        fileStorage.writeLines("catalog_benchmark.txt", results);
    }

    public static void main(String[] args) {
        System.out.println("Catalog Benchmark Utility");
        System.out.println("To run: mvnd exec:java -Dexec.mainClass=\"service.CatalogBenchmark\"");
        System.out.println("");
        System.out.println("This tool requires a GameRepository instance with test data.");
        System.out.println("See CatalogBenchmark class for integration with your application.");
    }
}
