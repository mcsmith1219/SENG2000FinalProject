package catalog;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import record.RecordGame;

//*
// HashMapCatalog is a concrete implementation of CatalogBehaviour that uses a HashMap to store RecordGame objects.
// 
// Classes: HashMapCatalog
// Methods: HashMapCatalog, addGame, searchByName, updatePersonalRating, deleteGame, getAllGames, getGamesSortedByName, getGamesSortedByReleaseDate, size
//
//*

public class HashMapCatalog implements CatalogBehaviour {
    // HashMap to store games with the game name as the key and the RecordGame object as the value //
    private final Map<String, RecordGame> games;

    // Constructor initializes the games HashMap //
    public HashMapCatalog() {
        this.games = new HashMap<>();
    }

    // Adds a game to the catalog if it doesn't already exist //
    @Override
    public boolean addGame(RecordGame game) {
        if (games.containsKey(game.getName())) {
            return false; // Game already exists
        }
        games.put(game.getName(), game);
        return true;
    }

    // Searches for a game by a non case sensitive name //
    @Override
    public RecordGame searchByName(String name) {
        return games.get(name);
    }

    // Updates the personal rating of a game by name //
    @Override
    public boolean updatePersonalRating(String name, double newRating) {
        RecordGame game = games.get(name);
        if (game != null) {
            game.setPersonalRating(newRating);
            return true;
        }
        return false;
    }

    // Deletes a game from the catalog by name //
    @Override
    public boolean deleteGame(String name) {
        return games.remove(name) != null;
    }

    // Retrieves all games in the catalog //
    @Override
    public List<RecordGame> getAllGames() {
        return new ArrayList<>(games.values());
    }

    // Sorts games by name in alphabetical order //
    @Override
    public List<RecordGame> getGamesSortedByName() {
        List<RecordGame> sortedGames = new ArrayList<>(games.values());
        sortedGames.sort(Comparator.comparing(RecordGame::getName, String.CASE_INSENSITIVE_ORDER));
        return sortedGames;
    }

    // Sorts games by release date in chronological order //
    @Override
    public List<RecordGame> getGamesSortedByReleaseDate() {
        List<RecordGame> sortedGames = new ArrayList<>(games.values());
        sortedGames.sort(Comparator.comparing(RecordGame::getReleaseDate));
        return sortedGames;
    }

    // Returns the number of games in the catalog //
    @Override
    public int size() {
        return games.size();
    }
}
