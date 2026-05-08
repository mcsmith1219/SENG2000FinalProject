package datastructures;
/**
 * Class:
 * PriorityQueueEntry() - wrapper for game entries with scores for priority queue operations.
 *
 * Methods:
 * PriorityQueueEntry(String, double) - constructor with title and score.
 * getTitle() - returns the game title.
 * getScore() - returns the score.
 * toString() - returns formatted string representation.
 **/

public class PriorityQueueEntry {
    private final String title;
    private final double score;

    public PriorityQueueEntry(String title, double score) {
        this.title = title;
        this.score = score;
    }

    public String getTitle() {
        return title;
    }

    public double getScore() {
        return score;
    }

    @Override

    public String toString() {
        return title + " (" + score + ")";
    }
}
