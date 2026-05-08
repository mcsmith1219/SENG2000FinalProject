package datastructures;
import storage.FileStorage;
import java.util.ArrayList;
import java.util.List;

/**
 * Class:
 * BTree() - generic B-Tree implementation for balanced key-value storage.
 * Maintains order and prevents skewed tree with guaranteed O(log n) operations.
 *
 * Methods:
 * BTree(int) - constructor with order parameter t.
 * search(K) - searches for value by key in O(log n) time.
 * insert(K, V) - inserts key-value pair maintaining B-Tree properties.
 * insertNonFull(Node, K, V) - inserts into non-full node.
 * splitChild(Node, int) - splits full child node during insertion.
 * dumpInOrder() - returns in-order traversal as list of strings.
 * dumpInOrder(Node, List) - recursive in-order traversal helper.
 * saveToFile(FileStorage, String) - persists B-Tree contents to file.
 **/

public class BTree<K extends Comparable<K>, V> {
    private static class Entry<K, V> {
        K key;
        V value;
        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
    private static class Node<K, V> {
        boolean leaf = true;
        List<Entry<K, V>> entries = new ArrayList<>();
        List<Node<K, V>> children = new ArrayList<>();
    }
    private final int t;
    private Node<K, V> root = new Node<>();

    public BTree(int t) {
        this.t = t;
    }

    public V search(K key) {
        return search(root, key);
    }

    private V search(Node<K, V> node, K key) {
        int i = 0;
        while (i < node.entries.size() && key.compareTo(node.entries.get(i).key) > 0) {
            i++;
        }
        if (i < node.entries.size() && key.compareTo(node.entries.get(i).key) == 0) {
            return node.entries.get(i).value;
        }
        if (node.leaf) {
            return null;
        }
        return search(node.children.get(i), key);
    }

    public void insert(K key, V value) {
        Node<K, V> r = root;
        if (r.entries.size() == 2 * t - 1) {
            Node<K, V> s = new Node<>();
            root = s;
            s.leaf = false;
            s.children.add(r);
            splitChild(s, 0);
            insertNonFull(s, key, value);
        } else {
            insertNonFull(r, key, value);
        }
    }

    private void insertNonFull(Node<K, V> node, K key, V value) {
        int i = node.entries.size() - 1;
        if (node.leaf) {
            node.entries.add(null);
            while (i >= 0 && key.compareTo(node.entries.get(i).key) < 0) {
                node.entries.set(i + 1, node.entries.get(i));
                i--;
            }
            node.entries.set(i + 1, new Entry<>(key, value));
        } else {
            while (i >= 0 && key.compareTo(node.entries.get(i).key) < 0) {
                i--;
            }
            i++;
            if (node.children.get(i).entries.size() == 2 * t - 1) {
                splitChild(node, i);
                if (key.compareTo(node.entries.get(i).key) > 0) {
                    i++;
                }
            }
            insertNonFull(node.children.get(i), key, value);
        }
    }

    private void splitChild(Node<K, V> parent, int index) {
        Node<K, V> fullChild = parent.children.get(index);
        Node<K, V> newNode = new Node<>();
        newNode.leaf = fullChild.leaf;
        for (int j = 0; j < t - 1; j++) {
            newNode.entries.add(fullChild.entries.remove(t));
        }
        if (!fullChild.leaf) {
            for (int j = 0; j < t; j++) {
                newNode.children.add(fullChild.children.remove(t));
            }
        }
        Entry<K, V> middle = fullChild.entries.remove(t - 1);
        parent.children.add(index + 1, newNode);
        parent.entries.add(index, middle);
    }

    public List<String> dumpInOrder() {
        List<String> lines = new ArrayList<>();
        dumpInOrder(root, lines);
        return lines;
    }

    private void dumpInOrder(Node<K, V> node, List<String> lines) {
        int i;
        for (i = 0; i < node.entries.size(); i++) {
            if (!node.leaf) {
                dumpInOrder(node.children.get(i), lines);
            }
            lines.add(node.entries.get(i).key + " = " + node.entries.get(i).value);
        }
        if (!node.leaf) {
            dumpInOrder(node.children.get(i), lines);
        }
    }

    public void saveToFile(FileStorage storage, String fileName) {
        storage.writeLines(fileName, dumpInOrder());
    }
}
