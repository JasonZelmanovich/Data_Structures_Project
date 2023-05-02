package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Trie;

import java.util.*;

public class TrieImpl<Value> implements Trie<Value> {
    private static final int alphabetSize = 128;
    private Node root;

    public TrieImpl() {
    }

    /**
     * add the given value at the given key
     *
     * @param key
     * @param val
     */
    @Override
    public void put(String key, Value val) {
        if (val == null) {
            return;
        }
        if (key == null) {
            throw new IllegalArgumentException();
        }
        this.root = put(root, key, (Value) val, 0);
    }

    private Node put(Node x, String key, Value val, int d) {
        //create a new node
        if (x == null) {
            x = new Node();
        }
        //we've reached the last node in the key,
        //set the value for the key and return the node
        if (d == key.length()) {
            if (x.val == null) x.val = new HashSet<>();
            x.val.add(val);
            return x;
        }
        //proceed to the next node in the chain of nodes that
        //forms the desired key
        char c = key.charAt(d);
        x.links[c] = this.put(x.links[c], key, val, d + 1);
        return x;
    }

    private Node get(Node x, String key, int d) {
        //link was null - return null, indicating a miss
        if (x == null) {
            return null;
        }
        //we've reached the last node in the key,
        //return the node
        if (d == key.length()) {
            return x;
        }
        //proceed to the next node in the chain of nodes that
        //forms the desired key
        char c = key.charAt(d);
        return this.get(x.links[c], key, d + 1);
    }

    /**
     * get all exact matches for the given key, sorted in descending order.
     * Search is CASE SENSITIVE.
     *
     * @param key
     * @param comparator used to sort values
     * @return a List of matching Values, in descending order
     */
    @Override
    public List<Value> getAllSorted(String key, Comparator<Value> comparator) {
        if (key == null || comparator == null) {
            throw new IllegalArgumentException("String 'key' or comparator was NULL");
        }
        ArrayList<Value> valuesList = new ArrayList<>();
        Node n = get(root, key, 0);
        if (n == null || n.val == null) {
            return valuesList;
        } else {
            valuesList.addAll(n.val);
        }
        valuesList.sort(comparator);
        return valuesList;
    }

    /**
     * get all matches which contain a String with the given prefix, sorted in descending order.
     * For example, if the key is "Too", you would return any value that contains "Tool", "Too", "Tooth", "Toodle", etc.
     * Search is CASE SENSITIVE.
     *
     * @param prefix
     * @param comparator used to sort values
     * @return a List of all matching Values containing the given prefix, in descending order
     */
    @Override
    public List<Value> getAllWithPrefixSorted(String prefix, Comparator<Value> comparator) {
        if (prefix == null || comparator == null) {
            throw new IllegalArgumentException("Prefix string or comparator was NULL");
        }
        ArrayList<Value> valuesList = new ArrayList<>();
        Node x = get(root, prefix, 0);
        HashSet<Value> valuesSet = new HashSet<>();
        collect(x, prefix, valuesSet);
        if (valuesSet.size() > 0) {
            valuesList.addAll(valuesSet);
            valuesList.sort(comparator);
        }
        return valuesList;
    }

    private void collect(Node x, String prefix, Set s) {
        if (x == null) return;
        if (x.val != null) s.addAll(x.val);
        for (char c = 0; c < alphabetSize; c++) {
            collect(x.links[c], prefix + c, s);
        }
    }

    /**
     * Delete the subtree rooted at the last character of the prefix.
     * Search is CASE SENSITIVE.
     *
     * @param prefix
     * @return a Set of all Values that were deleted.
     */
    @Override
    public Set<Value> deleteAllWithPrefix(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("Prefix string was NULL");
        }
        HashSet<Value> deletedValues = new HashSet<>();
        Node x = get(root, prefix, 0);
        if (x == null) {
            return deletedValues;
        }
        collect(x, prefix, deletedValues);
        x.links = new Node[alphabetSize];
        root = delete(root, prefix, 0);
        return deletedValues;
    }

    /**
     * Delete all values from the node of the given key (do not remove the values from other nodes in the Trie)
     *
     * @param key
     * @return a Set of all Values that were deleted.
     */
    @Override
    public Set<Value> deleteAll(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key string was NULL");
        }
        HashSet<Value> deletedValues = new HashSet<>();
        Node x = get(root, key, 0);
        if (x == null) return deletedValues;
        if (x.val != null) deletedValues.addAll(x.val);
        //clean up if it was a leaf node
        this.root = delete(this.root, key, 0);
        return deletedValues;
    }

    /**
     * Remove the given value from the node of the given key (do not remove the value from other nodes in the Trie)
     *
     * @param key
     * @param val
     * @return the value which was deleted. If the key did not contain the given value, return null.
     */
    @Override
    public Value delete(String key, Value val) {
        if (val == null || key == null) {
            throw new IllegalArgumentException("Value or key was NULL");
        }
        Node x = get(this.root, key, 0);
        Value tbd = null;
        if (x == null) {
            return null;
        }
        if (x != null && x.val.contains(val)) {
            tbd = (Value) val;
            x.val.remove(val);
            if (x.val.size() == 0) {
                this.root = delete(this.root, key, 0);
            }
        }
        return tbd;
    }

    /**
     * From sedgewick
     * Returns the root (or whatever node x is passed as a parameter) assuming the root has some non-null child
     *
     * @param x
     * @param key
     * @param d
     * @return
     */
    private Node delete(Node x, String key, int d) {
        if (x == null) return null;
        if (d == key.length()) {
            x.val = null;
        } else {
            char c = key.charAt(d);
            x.links[c] = delete(x.links[c], key, d + 1);
        }
        if (x.val != null) return x;
        for (char c = 0; c < alphabetSize; c++) {
            if (x.links[c] != null) return x;
        }
        return null;
    }

    private class Node<Value> {
        private HashSet<Value> val;
        private Node[] links = new Node[TrieImpl.alphabetSize];
    }
}
