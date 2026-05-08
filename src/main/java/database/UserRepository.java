package database;
import datastructures.BTree;
import model.User;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Class:
 * UserRepository() - data access object for user records persisted in TSV file.
 * Maintains in-memory cache with B-Tree index for fast username lookups.
 *
 * Methods:
 * UserRepository(DatabaseManager) - constructor that loads users from file.
 * createUser(String, String, String) - creates a new user account.
 * findUser(String) - finds user by username using B-Tree index.
 * getAllUsers() - returns list of all users in repository.
 * rebuildIndex() - rebuilds B-Tree index from current users.
 * load() - loads users from TSV file into memory.
 * save() - persists current users to TSV file.
 * encode(String), decode(String) - TSV escape/unescape helpers.
 * split(String, int) - splits TSV line into parts.
 **/

public class UserRepository {
    private final Path usersFile;
    private final Map<Integer, User> usersById = new LinkedHashMap<>();
    private BTree<String, Integer> usernameIndex = new BTree<>(3);
    private int nextId = 1;

    public UserRepository(DatabaseManager db) {
        this.usersFile = db.getDataDirectory().resolve("users.tsv");
        load();
    }

    public boolean createUser(String username, String password, String displayName) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return false;
        }
        String normalized = username.trim().toLowerCase();
        if (usernameIndex.search(normalized) != null) {
            return false;
        }
        User user = new User(nextId++, username.trim(), password, displayName == null ? "" : displayName.trim());
        usersById.put(user.getId(), user);
        rebuildIndex();
        save();
        return true;
    }

    public User findUser(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }
        Integer userId = usernameIndex.search(username.trim().toLowerCase());
        if (userId == null) {
            return null;
        }
        return usersById.get(userId);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(usersById.values());
    }

    private void rebuildIndex() {
        usernameIndex = new BTree<>(3);
        for (User user : usersById.values()) {
            usernameIndex.insert(user.getUsername().toLowerCase(), user.getId());
        }
    }

    private void load() {
        usersById.clear();
        if (!usersFile.toFile().exists()) {
            return;
        }
        try {
            List<String> lines = java.nio.file.Files.readAllLines(usersFile);
            for (String line : lines) {
                if (line.isBlank()) {
                    continue;
                }
                String[] parts = split(line, 4);
                int id = Integer.parseInt(parts[0]);
                String username = decode(parts[1]);
                String password = decode(parts[2]);
                String displayName = decode(parts[3]);
                usersById.put(id, new User(id, username, password, displayName));
                nextId = Math.max(nextId, id + 1);
            }
            rebuildIndex();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load users file.", e);
        }
    }

    private void save() {
        List<String> lines = new ArrayList<>();
        for (User user : usersById.values()) {
            lines.add(user.getId()
                    + "\t" + encode(user.getUsername())
                    + "\t" + encode(user.getPassword())
                    + "\t" + encode(user.getDisplayName()));
        }
        try {
            java.nio.file.Files.write(usersFile, lines);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save users file.", e);
        }
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
