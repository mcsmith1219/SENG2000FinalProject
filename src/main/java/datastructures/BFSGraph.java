package datastructures;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * Class:
 * BFSGraph() - adjacency list graph for modeling user follow relationships.
 * Uses breadth-first search to find follow recommendations from nearby users.
 *
 * Methods:
 * addVertex(String) - adds a vertex to the graph.
 * addEdge(String, String) - adds a directed edge between users.
 * removeEdge(String, String) - removes a directed edge between users.
 * getNeighbors(String) - returns the users followed by a user.
 * getIncoming(String) - returns the users following a user.
 * getMutualNeighbors(String, String) - returns common followed users.
 * recommend(String) - recommends users using BFS traversal.
 * snapshot() - returns the graph as formatted strings.
 **/

public class BFSGraph {
    private final Map<String, Set<String>> adjacency = new HashMap<>();

    public void addVertex(String username) {
        adjacency.putIfAbsent(username, new TreeSet<>());
    }

    public void addEdge(String from, String to) {
        addVertex(from);
        addVertex(to);
        adjacency.get(from).add(to);
    }

    public void removeEdge(String from, String to) {
        Set<String> edges = adjacency.get(from);
        if (edges != null) {
            edges.remove(to);
        }
    }

    public Set<String> getNeighbors(String username) {
        return new TreeSet<>(adjacency.getOrDefault(username, Collections.emptySet()));
    }

    public Set<String> getIncoming(String username) {
        Set<String> incoming = new TreeSet<>();
        for (Map.Entry<String, Set<String>> entry : adjacency.entrySet()) {
            if (entry.getValue().contains(username)) {
                incoming.add(entry.getKey());
            }
        }
        return incoming;
    }

    public Set<String> getMutualNeighbors(String firstUser, String secondUser) {
        Set<String> mutuals = getNeighbors(firstUser);
        mutuals.retainAll(getNeighbors(secondUser));
        return mutuals;
    }

    public List<String> recommend(String username) {
        Set<String> directConnections = getNeighbors(username);
        Set<String> visited = new HashSet<>();
        Set<String> recommendations = new TreeSet<>();
        Queue<String> queue = new ArrayDeque<>();
        Map<String, Integer> distance = new HashMap<>();
        queue.add(username);
        visited.add(username);
        distance.put(username, 0);
        while (!queue.isEmpty()) {
            String current = queue.poll();
            int currentDistance = distance.get(current);
            if (currentDistance == 2) {
                continue;
            }
            for (String neighbor : getNeighbors(current)) {
                if (visited.add(neighbor)) {
                    int nextDistance = currentDistance + 1;
                    distance.put(neighbor, nextDistance);
                    queue.add(neighbor);
                    if (nextDistance == 2 && !directConnections.contains(neighbor)) {
                        recommendations.add(neighbor);
                    }
                }
            }
        }
        return new ArrayList<>(recommendations);
    }

    public List<String> snapshot() {
        List<String> lines = new ArrayList<>();
        for (String username : new TreeSet<>(adjacency.keySet())) {
            lines.add(username + " -> " + String.join(", ", getNeighbors(username)));
        }
        return lines;
    }
}
