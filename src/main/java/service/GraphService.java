package service;
import datastructures.BFSGraph;
import model.User;
import storage.FileStorage;
import java.util.List;
import java.util.Set;

/**
 * Class:
 * GraphService() - service layer that manages user follow relationships using a BFS graph data structure.
 *
 * Methods:
 * addUser(User) - adds a user vertex to the graph.
 * follow(User, User) - creates a directed edge from follower to followed user.
 * unfollow(User, User) - removes the directed edge from follower to followed user.
 * getFollowing(User) - retrieves set of usernames that the user is following.
 * getFollowers(User) - retrieves set of usernames that are following the user.
 * getMutuals(User, User) - retrieves set of mutual followers between two users.
 * recommendFollows(User) - generates follow recommendations based on graph connections.
 * saveSnapshot() - saves current graph state to file for persistence.
 **/

public class GraphService {
    private final BFSGraph graph = new BFSGraph();
    private final FileStorage fileStorage = new FileStorage("data_structures");

    public void addUser(User user) {
        graph.addVertex(user.getUsername());
        saveSnapshot();
    }

    public void follow(User follower, User followed) {
        graph.addEdge(follower.getUsername(), followed.getUsername());
        saveSnapshot();
    }

    public void unfollow(User follower, User followed) {
        graph.removeEdge(follower.getUsername(), followed.getUsername());
        saveSnapshot();
    }

    public Set<String> getFollowing(User user) {
        return graph.getNeighbors(user.getUsername());
    }

    public Set<String> getFollowers(User user) {
        return graph.getIncoming(user.getUsername());
    }

    public Set<String> getMutuals(User a, User b) {
        return graph.getMutualNeighbors(a.getUsername(), b.getUsername());
    }

    public List<String> recommendFollows(User user) {
        return graph.recommend(user.getUsername());
    }

    public void saveSnapshot() {
        fileStorage.writeLines("bfs_graph.txt", graph.snapshot());
    }
}
