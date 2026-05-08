package service;
import model.RecordGame;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class:
 * CatalogHashMapImpl() - optimized catalog implementation using HashMap for fast O(1) lookups.
 * Maintains dual indexes for name and ID based searches.
 *
 * Methods:
 * CatalogHashMapImpl() - constructor to initialize HashMap indexes.
 * addGame(RecordGame game) - adds a game to dual index HashMap structures.
 * removeGame(int rawgId) - removes a game from both index HashMaps.
 * searchByName(String name) - searches for game by exact name in O(1) time.
 * searchByNameContains(String query) - searches for games containing query string.
 * sortByTitle() - sorts all games by title and returns sorted list.
 * getAll() - returns a copy of all games in the catalog.
 * size() - returns the number of games in the catalog.
 * contains(int rawgId) - checks if a game exists by RAWG ID in O(1) time.
 * clear() - removes all games from both index HashMaps.
 **/

public class CatalogHashMapImpl {
    private final Map<String, RecordGame> gamesByName;
    private final Map<Integer, RecordGame> gamesById;

    public CatalogHashMapImpl() {
        this.gamesByName = new HashMap<>();
        this.gamesById = new HashMap<>();
    }

    public void addGame(RecordGame game) {
        if (game != null && game.getName() != null) {
            String key = game.getName().toLowerCase();
            gamesByName.put(key, game);
            gamesById.put(game.getRawgId(), game);
        }
    }

    public boolean removeGame(int rawgId) {
        RecordGame game = gamesById.remove(rawgId);
        if (game != null && game.getName() != null) {
            gamesByName.remove(game.getName().toLowerCase());
            return true;
        }
        return false;
    }

    public RecordGame searchByName(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        return gamesByName.get(name.toLowerCase());
    }

    public List<RecordGame> searchByNameContains(String query) {
        List<RecordGame> results = new ArrayList<>();
        if (query == null || query.isBlank()) {
            return results;
        }
        String q = query.toLowerCase();
        for (RecordGame game : gamesByName.values()) {
            if (game.getName().toLowerCase().contains(q)) {
                results.add(game);
            }
        }
        return results;
    }

    public List<RecordGame> sortByTitle() {
        List<RecordGame> sorted = new ArrayList<>(gamesByName.values());
        sorted.sort(Comparator.comparing(g -> g.getName().toLowerCase()));
        return sorted;
    }

    public List<RecordGame> getAll() {
        return new ArrayList<>(gamesByName.values());
    }

    public int size() {
        return gamesByName.size();
    }

    public boolean contains(int rawgId) {
        return gamesById.containsKey(rawgId);
    }

    public void clear() {
        gamesByName.clear();
        gamesById.clear();
    }
}
