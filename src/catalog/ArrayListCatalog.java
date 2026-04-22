package catalog;
import java.util.ArrayList;
import java.util.List;
import record.RecordGame;

//*
// ArrayListCatalog is a concrete implementation of CatalogBehaviour that uses an ArrayList to store RecordGame objects. 
// It provides methods to add, search, update, delete, and retrieve games in various sorted orders. 
// The class ensures that game names are treated in a case-insensitive manner for searching and sorting.
// 
// Classes: ArrayListCatalog
// Methods: addGame, searchByName, updatePersonalRating, deleteGame, getAllGames, getGamesSortedByName, getGamesSortedByReleaseDate, size
//*

public class ArrayListCatalog implements CatalogBehaviour {
    private final List<RecordGame> games;

    // Constructor initializes the games list //
    public ArrayListCatalog() {
        this.games = new ArrayList<>();
    }

    // Adds a game to the catalog if it doesn't already exist //
    @Override
    public boolean addGame(RecordGame game) {
        return games.add(game);
    }

    // Searches for a game by a non case sensitive name //
    @Override
    public RecordGame searchByName(String name) {
        for (RecordGame game : games) {
            if (game.getName().equalsIgnoreCase(name)) {
                return game;
            }
        }
        return null;
    }

    // Updates the personal rating of a game by name //
    @Override
    public boolean updatePersonalRating(String name, double newRating) {
        RecordGame game = searchByName(name);
        if (game != null) {
            game.setPersonalRating(newRating);
            return true;
        }
        return false;
    }

    // Deletes a game from the catalog by name //
    @Override
    public boolean deleteGame(String name) {
        RecordGame game = searchByName(name);
        if (game != null) {
            return games.remove(game);
        }
        return false;
    }

    // Returns a list of all games in the catalog //
    @Override
    public List<RecordGame> getAllGames() {
        return new ArrayList<>(games);
    }

    // Sorts games by name in alphabetical order //
    @Override
    public List<RecordGame> getGamesSortedByName() {
        List<RecordGame> sortedGames = new ArrayList<>(games);
        sortedGames.sort((g1, g2) -> g1.getName().compareToIgnoreCase(g2.getName()));
        return sortedGames;
    }

    // Sorts games by release date in ascending order //
    @Override
    public List<RecordGame> getGamesSortedByReleaseDate() {
        List<RecordGame> sortedGames = new ArrayList<>(games);
        sortedGames.sort((g1, g2) -> g1.getReleaseDate().compareTo(g2.getReleaseDate()));
        return sortedGames;
    }

    // Returns the number of games in the catalog //
    @Override
    public int size() {
        return games.size();
    }
}
