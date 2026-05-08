package database;
import datastructures.BTree;
import model.RecordGame;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Class:
 * GameRepository() - data access object for game records persisted in TSV file.
 * Maintains in-memory cache with B-Tree index for fast lookups.
 *
 * Methods:
 * GameRepository(DatabaseManager) - constructor that loads games from file.
 * upsertGame(RecordGame) - inserts or updates a game record.
 * findByRawgId(int) - finds game by RAWG ID using B-Tree index.
 * getAllGames() - returns list of all games in repository.
 * rebuildIndex() - rebuilds B-Tree index from current games.
 * load() - loads games from TSV file into memory.
 * save() - persists current games to TSV file.
 * parseInt(String), parseDouble(String), safeInt(Integer), safeDouble(Double) - helper parsers.
 * encode(String), decode(String) - TSV escape/unescape helpers.
 * split(String, int) - splits TSV line into parts.
 **/

public class GameRepository {
    private final Path gamesFile;
    private final Map<Integer, RecordGame> gamesById = new LinkedHashMap<>();
    private BTree<Integer, Integer> rawgIndex = new BTree<>(3);

    public GameRepository(DatabaseManager db) {
        this.gamesFile = db.getDataDirectory().resolve("games.tsv");
        load();
    }

    public void upsertGame(RecordGame game) {
        if (game == null || game.getRawgId() == null) {
            return;
        }
        gamesById.put(game.getRawgId(), game);
        rebuildIndex();
        save();
    }

    public RecordGame findByRawgId(int rawgId) {
        Integer id = rawgIndex.search(rawgId);
        return id == null ? null : gamesById.get(id);
    }

    public List<RecordGame> getAllGames() {
        return new ArrayList<>(gamesById.values());
    }

    private void rebuildIndex() {
        rawgIndex = new BTree<>(3);
        for (Integer rawgId : gamesById.keySet()) {
            rawgIndex.insert(rawgId, rawgId);
        }
    }

    private void load() {
        gamesById.clear();
        if (!gamesFile.toFile().exists()) {
            return;
        }
        try {
            List<String> lines = java.nio.file.Files.readAllLines(gamesFile);
            for (String line : lines) {
                if (line.isBlank()) {
                    continue;
                }
                String[] parts = split(line, 7);
                RecordGame game = new RecordGame(
                        Integer.parseInt(parts[0]),
                        decode(parts[1]),
                        decode(parts[2]),
                        0.0,
                        parseInt(parts[3]),
                        parseDouble(parts[4]),
                        decode(parts[5]),
                        decode(parts[6])
                );
                gamesById.put(game.getRawgId(), game);
            }
            rebuildIndex();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load games file.", e);
        }
    }

    private void save() {
        List<String> lines = new ArrayList<>();
        for (RecordGame game : gamesById.values()) {
            lines.add(game.getRawgId()
                    + "\t" + encode(game.getName())
                    + "\t" + encode(game.getReleaseDate())
                    + "\t" + safeInt(game.getNumberOfRatings())
                    + "\t" + safeDouble(game.getRatingRawg())
                    + "\t" + encode(game.getSynopsis())
                    + "\t" + encode(game.getImageUrl()));
        }
        try {
            java.nio.file.Files.write(gamesFile, lines);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save games file.", e);
        }
    }

    private int parseInt(String value) {
        return value == null || value.isBlank() ? 0 : Integer.parseInt(value);
    }

    private double parseDouble(String value) {
        return value == null || value.isBlank() ? 0.0 : Double.parseDouble(value);
    }

    private int safeInt(Integer value) {
        return value == null ? 0 : value;
    }

    private double safeDouble(Double value) {
        return value == null ? 0.0 : value;
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
}
