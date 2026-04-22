package catalog;
import java.util.List;
import record.RecordGame;

//*
// CatalogBehaviour is an interface that defines the behavior of a video game catalog.
// It includes methods for adding, searching, updating, deleting, and retrieving games in various sorted orders. 
// Implementations of this interface can use different data structures (e.g., ArrayList, HashMap) to manage the collection of RecordGame objects.
//
// Classes: CatalogBehaviour
// Methods: addGame, searchByName, updatePersonalRating, deleteGame, getAllGames, getGamesSortedByName, getGamesSortedByReleaseDate, size
//*

public interface CatalogBehaviour {
    boolean addGame(RecordGame game);
    RecordGame searchByName(String name);
    boolean updatePersonalRating(String name, double newRating);
    boolean deleteGame(String name);
    List<RecordGame> getAllGames();
    List<RecordGame> getGamesSortedByName();
    List<RecordGame> getGamesSortedByReleaseDate();
    int size();
}