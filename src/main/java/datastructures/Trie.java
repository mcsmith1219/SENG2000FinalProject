package datastructures;
import storage.FileStorage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class:
 * Trie() - prefix tree data structure for efficient string searching and autocomplete.
 *
 * Methods:
 * insert(String) - inserts a word into the trie.
 * autocomplete(String) - returns all words starting with given prefix.
 * findNode(String) - finds the node corresponding to a text prefix.
 * collect(Node, List) - recursively collects all words from a node.
 * clear() - removes all words from the trie.
 * saveToFile(FileStorage, String) - persists trie contents to file.
 **/

public class Trie {
    private static class Node {
        Map<Character, Node> children = new HashMap<>();
        boolean isWord;
        String fullWord;
    }
    private final Node root = new Node();

    public void insert(String word) {
        if (word == null || word.isBlank()) {
            return;
        }
        String normalized = word.toLowerCase();
        Node current = root;
        for (char c : normalized.toCharArray()) {
            current = current.children.computeIfAbsent(c, k -> new Node());
        }
        current.isWord = true;
        current.fullWord = word;
    }

    public List<String> autocomplete(String prefix) {
        List<String> results = new ArrayList<>();
        if (prefix == null) {
            return results;
        }
        Node node = findNode(prefix.toLowerCase());
        if (node == null) {
            return results;
        }
        collect(node, results);
        return results;
    }

    private Node findNode(String text) {
        Node current = root;
        for (char c : text.toCharArray()) {
            current = current.children.get(c);
            if (current == null) {
                return null;
            }
        }
        return current;
    }

    private void collect(Node node, List<String> results) {
        if (node.isWord && node.fullWord != null) {
            results.add(node.fullWord);
        }
        for (Node child : node.children.values()) {
            collect(child, results);
        }
    }

    public void clear() {
        root.children.clear();
        root.isWord = false;
        root.fullWord = null;
    }

    public void saveToFile(FileStorage storage, String fileName) {
        storage.writeLines(fileName, autocomplete(""));
    }
}
