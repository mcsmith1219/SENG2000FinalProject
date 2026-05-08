package service;
import model.RecordGame;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Class:
 * CatalogArrayListImpl() - basic catalog implementation using ArrayList with linear search as baseline.
 *
 * Methods:
 * CatalogArrayListImpl() - constructor to initialize the games list.
 * addGame(RecordGame game) - adds a game to the catalog if not already present.
 * removeGame(int rawgId) - removes a game from catalog by RAWG ID.
 * searchByName(String name) - searches for game by exact name match using linear search O(n).
 * searchByNameContains(String query) - searches for games containing query string.
 * sortByTitle() - sorts all games by title and returns sorted list.
 * getAll() - returns a copy of all games in the catalog.
 * size() - returns the number of games in the catalog.
 * contains(int rawgId) - checks if a game exists by RAWG ID.
 * clear() - removes all games from the catalog.
 **/

public class CatalogArrayListImpl {
    private final List<RecordGame> games;

    public CatalogArrayListImpl() {
        this.games = new ArrayList<>();
    }

    public void addGame(RecordGame game) {
        if (game != null && !games.contains(game)) {
            games.add(game);
        }
    }

    public boolean removeGame(int rawgId) {
        return games.removeIf(game -> game.getRawgId() == rawgId);
    }

    public RecordGame searchByName(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        String query = name.toLowerCase();
        for (RecordGame game : games) {
            if (game.getName().toLowerCase().equals(query)) {
                return game;
            }
        }
        return null;
    }

    public List<RecordGame> searchByNameContains(String query) {
        List<RecordGame> results = new ArrayList<>();
        if (query == null || query.isBlank()) {
            return results;
        }
        String q = query.toLowerCase();
        for (RecordGame game : games) {
            if (game.getName().toLowerCase().contains(q)) {
                results.add(game);
            }
        }
        return results;
    }

    public List<RecordGame> sortByTitle() {
        List<RecordGame> sorted = new ArrayList<>(games);
        Collections.sort(sorted, Comparator.comparing(g -> g.getName().toLowerCase()));
        return sorted;
    }

    public List<RecordGame> getAll() {
        return new ArrayList<>(games);
    }

    public int size() {
        return games.size();
    }

    public boolean contains(int rawgId) {
        for (RecordGame game : games) {
            if (game.getRawgId() == rawgId) {
                return true;
            }
        }
        return false;
    }

    public void clear() {
        games.clear();
    }
}
