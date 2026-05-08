package database;
import model.RecordGame;
import model.User;
import model.UserGameEntry;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Class:
 * UserGameRepository() - data access object for user game entries, comments, likes, and follows.
 * Manages relationships between users and games with TSV file persistence.
 *
 * Methods:
 * UserGameRepository(DatabaseManager, GameRepository, UserRepository) - constructor.
 * addGameToUser(int, int) - adds a game to user's catalog.
 * removeGameFromUser(int, int) - removes a game from user's catalog.
 * updateUserGameEntry(...) - updates game entry metadata.
 * getUserGames(int) - retrieves user's games.
 * getPublicUserGames(int) - retrieves user's public games.
 * addComment(...), deleteComment(...) - manages game comments.
 * addLike(...), removeLike(...), getLikeCount(...), getComments(...) - manages likes and comments.
 * followUser(int, int), unfollowUser(int, int), getAllFollowPairs() - manages user follows.
 * loadAll(), loadUserGames(), loadComments(), loadLikes(), loadFollows() - load from files.
 * Save methods and helper encode/decode/split methods.
 **/

public class UserGameRepository {
    private final Path userGamesFile;
    private final Path commentsFile;
    private final Path likesFile;
    private final Path followsFile;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final List<UserGameRow> userGameRows = new ArrayList<>();
    private final List<CommentRow> commentRows = new ArrayList<>();
    private final List<LikeRow> likeRows = new ArrayList<>();
    private final List<FollowRow> followRows = new ArrayList<>();

    public UserGameRepository(DatabaseManager db, GameRepository gameRepository, UserRepository userRepository) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.userGamesFile = db.getDataDirectory().resolve("user_games.tsv");
        this.commentsFile = db.getDataDirectory().resolve("comments.tsv");
        this.likesFile = db.getDataDirectory().resolve("likes.tsv");
        this.followsFile = db.getDataDirectory().resolve("follows.tsv");
        loadAll();
    }

    public boolean addGameToUser(int userId, int rawgId) {
        for (UserGameRow row : userGameRows) {
            if (row.userId == userId && row.rawgId == rawgId) {
                return false;
            }
        }
        UserGameRow row = new UserGameRow();
        row.userId = userId;
        row.rawgId = rawgId;
        row.personalRating = 0.0;
        row.userRecord = "";
        row.isPublic = true;
        row.status = "backlog";
        row.isFavorite = false;
        row.hoursPlayed = 0.0;
        row.dateAdded = LocalDateTime.now().toString();
        row.dateCompleted = "";
        userGameRows.add(row);
        saveUserGames();
        return true;
    }

    public boolean removeGameFromUser(int userId, int rawgId) {
        boolean removed = userGameRows.removeIf(row -> row.userId == userId && row.rawgId == rawgId);
        if (removed) {
            commentRows.removeIf(row -> row.ownerUserId == userId && row.rawgId == rawgId);
            likeRows.removeIf(row -> row.ownerUserId == userId && row.rawgId == rawgId);
            saveUserGames();
            saveComments();
            saveLikes();
        }
        return removed;
    }

    public boolean updateUserGameEntry(int userId, int rawgId, String recordText, double personalRating, boolean isPublic, String status, boolean isFavorite, double hoursPlayed, String dateCompleted) {
        for (UserGameRow row : userGameRows) {
            if (row.userId == userId && row.rawgId == rawgId) {
                row.userRecord = recordText == null ? "" : recordText;
                row.personalRating = personalRating;
                row.isPublic = isPublic;
                row.status = status == null ? "backlog" : status;
                row.isFavorite = isFavorite;
                row.hoursPlayed = hoursPlayed;
                row.dateCompleted = dateCompleted == null ? "" : dateCompleted;
                saveUserGames();
                return true;
            }
        }
        return false;
    }

    public List<UserGameEntry> getUserGames(int userId) {
        return getUserGamesInternal(userId, false);
    }

    public List<UserGameEntry> getPublicUserGames(int userId) {
        return getUserGamesInternal(userId, true);
    }

    private List<UserGameEntry> getUserGamesInternal(int userId, boolean onlyPublic) {
        List<UserGameEntry> entries = new ArrayList<>();
        for (UserGameRow row : userGameRows) {
            if (row.userId != userId) {
                continue;
            }
            if (onlyPublic && !row.isPublic) {
                continue;
            }
            RecordGame game = gameRepository.findByRawgId(row.rawgId);
            if (game != null) {
                entries.add(new UserGameEntry(
                        game, row.personalRating, row.userRecord, row.isPublic, row.status, row.isFavorite, row.hoursPlayed, row.dateAdded, row.dateCompleted.isBlank() ? null : row.dateCompleted));
            }
        }
        return entries;
    }

    public boolean addComment(int ownerUserId, int commenterUserId, int rawgId, String commentText) {
        for (CommentRow row : commentRows) {
            if (row.ownerUserId == ownerUserId
                    && row.commenterUserId == commenterUserId
                    && row.rawgId == rawgId
                    && row.commentText.equals(commentText)) {
                return false;
            }
        }
        CommentRow row = new CommentRow();
        row.ownerUserId = ownerUserId;
        row.commenterUserId = commenterUserId;
        row.rawgId = rawgId;
        row.commentText = commentText;
        row.createdAt = LocalDateTime.now().toString();
        commentRows.add(row);
        saveComments();
        return true;
    }

    public boolean deleteComment(int ownerUserId, int commenterUserId, int rawgId, String commentText) {
        boolean removed = commentRows.removeIf(row -> row.ownerUserId == ownerUserId && row.commenterUserId == commenterUserId && row.rawgId == rawgId && row.commentText.equals(commentText));
        if (removed) {
            saveComments();
        }
        return removed;
    }

    public boolean addLike(int ownerUserId, int likerUserId, int rawgId) {
        for (LikeRow row : likeRows) {
            if (row.ownerUserId == ownerUserId && row.likerUserId == likerUserId && row.rawgId == rawgId) {
                return false;
            }
        }
        LikeRow row = new LikeRow();
        row.ownerUserId = ownerUserId;
        row.likerUserId = likerUserId;
        row.rawgId = rawgId;
        row.createdAt = LocalDateTime.now().toString();
        likeRows.add(row);
        saveLikes();
        return true;
    }

    public boolean removeLike(int ownerUserId, int likerUserId, int rawgId) {
        boolean removed = likeRows.removeIf(row ->
                row.ownerUserId == ownerUserId && row.likerUserId == likerUserId && row.rawgId == rawgId);
        if (removed) {
            saveLikes();
        }
        return removed;
    }

    public int getLikeCount(int ownerUserId, int rawgId) {
        int total = 0;
        for (LikeRow row : likeRows) {
            if (row.ownerUserId == ownerUserId && row.rawgId == rawgId) {
                total++;
            }
        }
        return total;
    }

    public List<String> getComments(int ownerUserId, int rawgId) {
        List<String> comments = new ArrayList<>();
        commentRows.stream()
                .filter(row -> row.ownerUserId == ownerUserId && row.rawgId == rawgId)
                .sorted((a, b) -> a.createdAt.compareTo(b.createdAt))
                .forEach(row -> {
                    User user = userRepository.getAllUsers().stream()
                            .filter(u -> u.getId() == row.commenterUserId)
                            .findFirst()
                            .orElse(null);
                    String displayName = user == null
                            ? "Unknown"
                            : (user.getDisplayName() == null || user.getDisplayName().isBlank()
                            ? user.getUsername()
                            : user.getDisplayName());
                    comments.add(displayName + ": " + row.commentText);
                });
        return comments;
    }

    public List<String> getAllCommentsForGame(int rawgId) {
        List<String> comments = new ArrayList<>();
        commentRows.stream()
                .filter(row -> row.rawgId == rawgId)
                .sorted((a, b) -> a.createdAt.compareTo(b.createdAt))
                .forEach(row -> {
                    User user = userRepository.getAllUsers().stream()
                            .filter(u -> u.getId() == row.commenterUserId)
                            .findFirst()
                            .orElse(null);
                    String displayName = user == null
                            ? "Unknown"
                            : (user.getDisplayName() == null || user.getDisplayName().isBlank()
                            ? user.getUsername()
                            : user.getDisplayName());
                    comments.add(displayName + ": " + row.commentText);
                });
        return comments;
    }

    public boolean followUser(int followerUserId, int followedUserId) {
        for (FollowRow row : followRows) {
            if (row.followerUserId == followerUserId && row.followedUserId == followedUserId) {
                return false;
            }
        }
        FollowRow row = new FollowRow();
        row.followerUserId = followerUserId;
        row.followedUserId = followedUserId;
        row.createdAt = LocalDateTime.now().toString();
        followRows.add(row);
        saveFollows();
        return true;
    }

    public boolean unfollowUser(int followerUserId, int followedUserId) {
        boolean removed = followRows.removeIf(row -> row.followerUserId == followerUserId && row.followedUserId == followedUserId);
        if (removed) {
            saveFollows();
        }
        return removed;
    }

    public List<FollowPair> getAllFollowPairs() {
        List<FollowPair> pairs = new ArrayList<>();
        for (FollowRow row : followRows) {
            pairs.add(new FollowPair(row.followerUserId, row.followedUserId));
        }
        return pairs;
    }

    private void loadAll() {
        loadUserGames();
        loadComments();
        loadLikes();
        loadFollows();
    }

    private void loadUserGames() {
        userGameRows.clear();
        if (!userGamesFile.toFile().exists()) {
            return;
        }
        try {
            for (String line : java.nio.file.Files.readAllLines(userGamesFile)) {
                if (line.isBlank()) {
                    continue;
                }
                String[] p = split(line, 10);
                UserGameRow row = new UserGameRow();
                row.userId = Integer.parseInt(p[0]);
                row.rawgId = Integer.parseInt(p[1]);
                row.personalRating = parseDouble(p[2]);
                row.userRecord = decode(p[3]);
                row.isPublic = Boolean.parseBoolean(p[4]);
                row.status = decode(p[5]);
                row.isFavorite = Boolean.parseBoolean(p[6]);
                row.hoursPlayed = parseDouble(p[7]);
                row.dateAdded = decode(p[8]);
                row.dateCompleted = decode(p[9]);
                userGameRows.add(row);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load user games file.", e);
        }
    }

    private void loadComments() {
        commentRows.clear();
        if (!commentsFile.toFile().exists()) {
            return;
        }
        try {
            for (String line : java.nio.file.Files.readAllLines(commentsFile)) {
                if (line.isBlank()) {
                    continue;
                }
                String[] p = split(line, 5);
                CommentRow row = new CommentRow();
                row.ownerUserId = Integer.parseInt(p[0]);
                row.commenterUserId = Integer.parseInt(p[1]);
                row.rawgId = Integer.parseInt(p[2]);
                row.commentText = decode(p[3]);
                row.createdAt = decode(p[4]);
                commentRows.add(row);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load comments file.", e);
        }
    }

    private void loadLikes() {
        likeRows.clear();
        if (!likesFile.toFile().exists()) {
            return;
        }
        try {
            for (String line : java.nio.file.Files.readAllLines(likesFile)) {
                if (line.isBlank()) {
                    continue;
                }
                String[] p = split(line, 4);
                LikeRow row = new LikeRow();
                row.ownerUserId = Integer.parseInt(p[0]);
                row.likerUserId = Integer.parseInt(p[1]);
                row.rawgId = Integer.parseInt(p[2]);
                row.createdAt = decode(p[3]);
                likeRows.add(row);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load likes file.", e);
        }
    }

    private void loadFollows() {
        followRows.clear();
        if (!followsFile.toFile().exists()) {
            return;
        }
        try {
            for (String line : java.nio.file.Files.readAllLines(followsFile)) {
                if (line.isBlank()) {
                    continue;
                }
                String[] p = split(line, 3);
                FollowRow row = new FollowRow();
                row.followerUserId = Integer.parseInt(p[0]);
                row.followedUserId = Integer.parseInt(p[1]);
                row.createdAt = decode(p[2]);
                followRows.add(row);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load follows file.", e);
        }
    }

    private void saveUserGames() {
        List<String> lines = new ArrayList<>();
        for (UserGameRow row : userGameRows) {
            lines.add(row.userId + "\t" + row.rawgId + "\t" + row.personalRating + "\t" + encode(row.userRecord) + "\t" + row.isPublic + "\t" + encode(row.status) + "\t" + row.isFavorite + "\t" + row.hoursPlayed + "\t" + encode(row.dateAdded) + "\t" + encode(row.dateCompleted));
        }
        write(userGamesFile, lines, "user games");
    }

    private void saveComments() {
        List<String> lines = new ArrayList<>();
        for (CommentRow row : commentRows) {
            lines.add(row.ownerUserId + "\t" + row.commenterUserId + "\t" + row.rawgId + "\t" + encode(row.commentText) + "\t" + encode(row.createdAt));
        }
        write(commentsFile, lines, "comments");
    }

    private void saveLikes() {
        List<String> lines = new ArrayList<>();
        for (LikeRow row : likeRows) {
            lines.add(row.ownerUserId + "\t" + row.likerUserId + "\t" + row.rawgId + "\t" + encode(row.createdAt));
        }
        write(likesFile, lines, "likes");
    }

    private void saveFollows() {
        List<String> lines = new ArrayList<>();
        for (FollowRow row : followRows) {
            lines.add(row.followerUserId + "\t" + row.followedUserId + "\t" + encode(row.createdAt));
        }
        write(followsFile, lines, "follows");
    }

    private void write(Path path, List<String> lines, String label) {
        try {
            java.nio.file.Files.write(path, lines);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save " + label + " file.", e);
        }
    }

    private double parseDouble(String value) {
        return value == null || value.isBlank() ? 0.0 : Double.parseDouble(value);
    }

    private String encode(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("\\", "\\\\")
                .replace("\t", "\\t")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private String decode(String value) {
        return value
                .replace("\\r", "\r")
                .replace("\\n", "\n")
                .replace("\\t", "\t")
                .replace("\\\\", "\\");
    }

    private String[] split(String line, int expectedParts) {
        String[] raw = line.split("\t", -1);
        String[] out = new String[expectedParts];
        for (int i = 0; i < expectedParts; i++) {
            out[i] = i < raw.length ? raw[i] : "";
        }
        return out;
    }
    public static class FollowPair {
        public final int followerUserId;
        public final int followedUserId;
        public FollowPair(int followerUserId, int followedUserId) {
            this.followerUserId = followerUserId;
            this.followedUserId = followedUserId;
        }
    }
    private static class UserGameRow {
        int userId;
        int rawgId;
        double personalRating;
        String userRecord;
        boolean isPublic;
        String status;
        boolean isFavorite;
        double hoursPlayed;
        String dateAdded;
        String dateCompleted;
    }
    private static class CommentRow {
        int ownerUserId;
        int commenterUserId;
        int rawgId;
        String commentText;
        String createdAt;
    }
    private static class LikeRow {
        int ownerUserId;
        int likerUserId;
        int rawgId;
        String createdAt;
    }
    private static class FollowRow {
        int followerUserId;
        int followedUserId;
        String createdAt;
    }
}
