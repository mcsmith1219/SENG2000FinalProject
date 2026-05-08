package service;
import database.GameRepository;
import database.UserGameRepository;
import datastructures.PriorityQueueEntry;
import datastructures.Trie;
import model.RecordGame;
import model.User;
import model.UserGameEntry;
import storage.FileStorage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.TreeMap;

/**
 * Class:
 * CatalogService() - service layer that manages user game catalogs with multiple data structures for efficient operations.
 *
 * Methods:
 * addGame(User, RecordGame) - adds a game to user's catalog and updates indexes.
 * removeGame(User, int) - removes a game from user's catalog and updates indexes.
 * getUserGames(User) - retrieves all games in user's catalog and rebuilds indexes.
 * getPublicUserGames(User) - retrieves only public games from user's catalog.
 * updateEntry(...) - updates a game entry's details and refreshes indexes.
 * addComment(...) - adds a comment to a game entry.
 * deleteComment(...) - deletes a comment from a game entry.
 * addLike(...) - adds a like to a game entry.
 * removeLike(...) - removes a like from a game entry.
 * getLikeCount(...) - gets total like count for a game entry.
 * getComments(...) - retrieves comments for a specific game entry.
 * getAllCommentsForGame(int) - retrieves all comments for a specific game across all users.
 * followUser(...) - allows one user to follow another user's catalog activity.
 * unfollowUser(...) - allows one user to unfollow another user.
 * searchMyGames(User, String) - searches user's games by title prefix using Trie autocomplete.
 * getTitleSuggestions(User, String, int) - gets title suggestions based on prefix with limit using Trie autocomplete.
 * getGamesSortedByTitle(User) - returns user's games sorted by title using TreeMap ordering.
 * getTopRatedGames(User, int) - returns top rated games in user's catalog using PriorityQueue ranking.
 **/

public class CatalogService {
    private final GameRepository gameRepository;
    private final UserGameRepository userGameRepository;
    private final Trie trie = new Trie();
    private final TreeMap<String, List<UserGameEntry>> treeMap = new TreeMap<>();
    private final HashSet<String> hashSet = new HashSet<>();
    private final FileStorage fileStorage = new FileStorage("data_structures");

    public CatalogService(GameRepository gameRepository, UserGameRepository userGameRepository) {
        this.gameRepository = gameRepository;
        this.userGameRepository = userGameRepository;
    }

    public boolean addGame(User user, RecordGame game) {
        String key = user.getUsername().toLowerCase() + "::" + game.getRawgId();
        if (hashSet.contains(key)) {
            return false;
        }
        gameRepository.upsertGame(game);
        boolean added = userGameRepository.addGameToUser(user.getId(), game.getRawgId());
        if (added) {
            hashSet.add(key);
            saveStructureSnapshots(user);
        }
        return added;
    }

    public boolean removeGame(User user, int rawgId) {
        boolean removed = userGameRepository.removeGameFromUser(user.getId(), rawgId);
        if (removed) {
            getUserGames(user);
        }
        return removed;
    }

    public List<UserGameEntry> getUserGames(User user) {
        List<UserGameEntry> games = userGameRepository.getUserGames(user.getId());
        rebuildIndexes(user, games);
        saveStructureSnapshots(user);
        return games;
    }

    public List<UserGameEntry> getPublicUserGames(User user) {
        return userGameRepository.getPublicUserGames(user.getId());
    }

    public boolean updateEntry(User user, int rawgId, String recordText, double personalRating, boolean isPublic, String status, boolean isFavorite, double hoursPlayed, String dateCompleted) {
        boolean updated = userGameRepository.updateUserGameEntry(user.getId(), rawgId, recordText, personalRating, isPublic, status, isFavorite, hoursPlayed, dateCompleted);
        if (updated) {
            getUserGames(user);
        }
        return updated;
    }

    public boolean addComment(User owner, User commenter, int rawgId, String text) {
        return userGameRepository.addComment(owner.getId(), commenter.getId(), rawgId, text);
    }

    public boolean deleteComment(User owner, User commenter, int rawgId, String text) {
        return userGameRepository.deleteComment(owner.getId(), commenter.getId(), rawgId, text);
    }

    public boolean addLike(User owner, User liker, int rawgId) {
        return userGameRepository.addLike(owner.getId(), liker.getId(), rawgId);
    }

    public boolean removeLike(User owner, User liker, int rawgId) {
        return userGameRepository.removeLike(owner.getId(), liker.getId(), rawgId);
    }

    public int getLikeCount(User owner, int rawgId) {
        return userGameRepository.getLikeCount(owner.getId(), rawgId);
    }

    public List<String> getComments(User owner, int rawgId) {
        return userGameRepository.getComments(owner.getId(), rawgId);
    }

    public List<String> getAllCommentsForGame(int rawgId) {
        return userGameRepository.getAllCommentsForGame(rawgId);
    }

    public boolean followUser(User follower, User followed) {
        return userGameRepository.followUser(follower.getId(), followed.getId());
    }

    public boolean unfollowUser(User follower, User followed) {
        return userGameRepository.unfollowUser(follower.getId(), followed.getId());
    }

    public List<UserGameEntry> searchMyGames(User user, String titleQuery) {
        List<UserGameEntry> games = userGameRepository.getUserGames(user.getId());
        rebuildIndexes(user, games);
        String q = titleQuery.toLowerCase().trim();
        if (q.isEmpty()) {
            return games;
        }
        List<String> matches = trie.autocomplete(q);
        List<UserGameEntry> results = new ArrayList<>();
        for (String title : matches) {
            List<UserGameEntry> entries = treeMap.get(title.toLowerCase());
            if (entries != null) {
                results.addAll(entries);
            }
        }
        return results;
    }

    public List<String> getTitleSuggestions(User user, String prefix, int limit) {
        List<UserGameEntry> games = userGameRepository.getUserGames(user.getId());
        rebuildIndexes(user, games);
        List<String> matches = trie.autocomplete(prefix == null ? "" : prefix.trim().toLowerCase());
        if (matches.size() > limit) {
            return new ArrayList<>(matches.subList(0, limit));
        }
        return matches;
    }

    public List<UserGameEntry> getGamesSortedByTitle(User user) {
        List<UserGameEntry> games = userGameRepository.getUserGames(user.getId());
        rebuildIndexes(user, games);
        List<UserGameEntry> results = new ArrayList<>();
        for (List<UserGameEntry> entries : treeMap.values()) {
            results.addAll(entries);
        }
        return results;
    }

    public List<PriorityQueueEntry> getTopRatedGames(User user, int limit) {
        List<UserGameEntry> games = userGameRepository.getUserGames(user.getId());
        PriorityQueue<PriorityQueueEntry> priorityQueue =
                new PriorityQueue<>(Comparator.comparingDouble(PriorityQueueEntry::getScore));
        for (UserGameEntry entry : games) {
            priorityQueue.offer(new PriorityQueueEntry(
                    entry.getGame().getName(),
                    entry.getPersonalRating()
            ));
            if (priorityQueue.size() > limit) {
                priorityQueue.poll();
            }
        }
        List<PriorityQueueEntry> results = new ArrayList<>();
        while (!priorityQueue.isEmpty()) {
            results.add(priorityQueue.poll());
        }
        Collections.reverse(results);
        savePriorityQueueSnapshot(results);
        return results;
    }

    private void rebuildIndexes(User user, List<UserGameEntry> games) {
        trie.clear();
        treeMap.clear();
        hashSet.clear();
        for (UserGameEntry entry : games) {
            String title = entry.getGame().getName();
            String normalizedTitle = title.toLowerCase();
            trie.insert(title);
            treeMap.computeIfAbsent(normalizedTitle, k -> new ArrayList<>()).add(entry);
            String key = user.getUsername().toLowerCase() + "::" + entry.getGame().getRawgId();
            hashSet.add(key);
        }
    }

    private void saveStructureSnapshots(User user) {
        trie.saveToFile(fileStorage, "trie.txt");
        saveHashSetSnapshot();
        saveTreeMapSnapshot();
        getTopRatedGames(user, 10);
    }

    private void saveHashSetSnapshot() {
        fileStorage.writeLines("hashset.txt", new ArrayList<>(hashSet));
    }

    private void saveTreeMapSnapshot() {
        List<String> lines = new ArrayList<>();
        for (String key : treeMap.keySet()) {
            List<String> titles = new ArrayList<>();
            for (UserGameEntry entry : treeMap.get(key)) {
                titles.add(entry.getGame().getName());
            }
            lines.add(key + " -> " + String.join(", ", titles));
        }
        fileStorage.writeLines("treemap.txt", lines);
    }

    private void savePriorityQueueSnapshot(List<PriorityQueueEntry> rankedGames) {
        List<String> lines = new ArrayList<>();
        for (PriorityQueueEntry game : rankedGames) {
            lines.add(game.toString());
        }
        fileStorage.writeLines("priorityqueue.txt", lines);
    }
}
