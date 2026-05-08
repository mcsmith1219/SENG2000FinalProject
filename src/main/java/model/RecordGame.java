package model;
/**
 * Class:
 * RecordGame() - model representing a game record with RAWG data and user rating.
 *
 * Methods:
 * RecordGame(Integer, String, String, Double, Integer, Double, String, String) - constructor.
 * getRawgId() - returns the RAWG game ID.
 * getName() - returns the game name.
 * getReleaseDate() - returns the release date.
 * getPersonalRating() - returns the user's personal rating.
 * setPersonalRating(Double) - sets the user's personal rating.
 * getNumberOfRatings() - returns the number of ratings.
 * getRatingRawg() - returns the RAWG rating.
 * getSynopsis() - returns the game synopsis.
 * getImageUrl() - returns the game image URL.
 **/

public class RecordGame {
    private final Integer rawgId;
    private final String name;
    private final String releaseDate;
    private Double personalRating;
    private final Integer numberOfRatings;
    private final Double ratingRawg;
    private final String synopsis;
    private final String imageUrl;
    public RecordGame(Integer rawgId, String name, String releaseDate,
                      Double personalRating, Integer numberOfRatings,
                      Double ratingRawg, String synopsis, String imageUrl) {
        this.rawgId = rawgId;
        this.name = name;
        this.releaseDate = releaseDate;
        this.personalRating = personalRating;
        this.numberOfRatings = numberOfRatings;
        this.ratingRawg = ratingRawg;
        this.synopsis = synopsis;
        this.imageUrl = imageUrl;
    }

    public Integer getRawgId() {
        return rawgId;
    }

    public String getName() {
        return name;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public Double getPersonalRating() {
        return personalRating;
    }

    public void setPersonalRating(Double personalRating) {
        this.personalRating = personalRating;
    }

    public Integer getNumberOfRatings() {
        return numberOfRatings;
    }

    public Double getRatingRawg() {
        return ratingRawg;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
