package record;

//*
// RecordGame is a class that represents a video game record with attributes such as name, release date, personal rating, number of ratings, IGN rating, and synopsis. 
// It provides constructors, getters, setters, and a toString method for easy representation of the game record
// 
// Classes: RecordGame
// Methods: RecordGame, getName, getReleaseDate, getPersonalRating, setPersonalRating, getNumberofratings, getRatingIGN, getSynopsis, toString
//*

public class RecordGame {
    // Attributes of the RecordGame class //
    private final String name;
    private String releaseDate;
    private Double personalRating;
    private Integer numberofratings;
    private Double ratingIGN;
    private String synopsis;

    // Constructor to initialize all attributes of the RecordGame class //
    public RecordGame(String name, String releaseDate, Double personalRating, Integer numberofratings, Double ratingIGN, String synopsis) {
        this.name = name;
        this.releaseDate = releaseDate;
        this.personalRating = personalRating;
        this.numberofratings = numberofratings;
        this.ratingIGN = ratingIGN;
        this.synopsis = synopsis;
    }

    // Overloaded constructor to initialize only the name of the game //
    public RecordGame(String name) {
        this.name = name;
    }

    // Getters and setters for the attributes of the RecordGame class //
    public String getName() {
        return name;
    }

    // Getter for release date of the game //
    public String getReleaseDate() {
        return releaseDate;
    }

    // Getter for personal rating of the game //
    public Double getPersonalRating() {
        return personalRating;
    }

    // Setter for personal rating of the game //
    public void setPersonalRating(Double personalRating) {
        this.personalRating = personalRating;
    }

    // Getter for the number of ratings of the game //
    public Integer getNumberofratings() {
        return numberofratings;
    }

    // Getter for IGN rating of the game //
    public Double getRatingIGN() {
        return ratingIGN;
    }

    // Getter for the synopsis of the game //
    public String getSynopsis() {
        return synopsis;
    }
    
    // toString method to provide a string representation of the RecordGame object //
    @Override
    public String toString() {
        return "RecordGame{" +
                "name='" + name + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", personalRating=" + personalRating +
                ", numberofratings=" + numberofratings +
                ", ratingIGN=" + ratingIGN +
                ", synopsis='" + synopsis + '\'' +
                '}';
    }
}