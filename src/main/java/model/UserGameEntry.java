package model;
/**
 * Class:
 * UserGameEntry() - model representing a user's entry for a game with metadata.
 *
 * Methods:
 * UserGameEntry(RecordGame, Double, String, boolean, String, boolean, double, String, String) - constructor.
 * getGame() - returns the associated RecordGame.
 * getPersonalRating() - returns the user's personal rating.
 * setPersonalRating(Double) - sets the user's personal rating.
 * getUserRecord() - returns the user's record/notes.
 * setUserRecord(String) - sets the user's record/notes.
 * isPublic() - checks if entry is public.
 * getStatus() - returns the game status.
 * isFavorite() - checks if marked as favorite.
 * getHoursPlayed() - returns hours played.
 * getDateAdded() - returns the date added.
 * getDateCompleted() - returns the date completed.
 **/

public class UserGameEntry {
    private final RecordGame game;
    private Double personalRating;
    private String userRecord;
    private boolean isPublic;
    private String status;
    private boolean isFavorite;
    private double hoursPlayed;
    private String dateAdded;
    private String dateCompleted;

    public UserGameEntry(RecordGame game, Double personalRating, String userRecord, boolean isPublic, String status, boolean isFavorite, double hoursPlayed, String dateAdded, String dateCompleted) {
        this.game = game;
        this.personalRating = personalRating;
        this.userRecord = userRecord;
        this.isPublic = isPublic;
        this.status = status;
        this.isFavorite = isFavorite;
        this.hoursPlayed = hoursPlayed;
        this.dateAdded = dateAdded;
        this.dateCompleted = dateCompleted;
    }

    public RecordGame getGame() {
        return game;
    }

    public Double getPersonalRating() {
        return personalRating;
    }

    public void setPersonalRating(Double personalRating) {
        this.personalRating = personalRating;
        this.game.setPersonalRating(personalRating);
    }

    public String getUserRecord() {
        return userRecord;
    }

    public void setUserRecord(String userRecord) {
        this.userRecord = userRecord;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public String getStatus() {
        return status;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public double getHoursPlayed() {
        return hoursPlayed;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public String getDateCompleted() {
        return dateCompleted;
    }
}
