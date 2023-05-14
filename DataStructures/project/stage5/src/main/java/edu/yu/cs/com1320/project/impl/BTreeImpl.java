package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.BTree;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;

import java.util.Arrays;

public class BTreeImpl<Key extends Comparable<Key>, Value> implements BTree<Key, Value> {
    //max children per B-tree node = MAX-1 (must be an even number and greater than 2)
    private static final int MAX = 4;
    private BTreeImpl.Node root; //root of the B-tree
    private BTreeImpl.Node leftMostExternalNode;
    private int height; //height of the B-tree
    private int n; //number of key-value pairs in the B-tree

    /**
     * @param k
     * @return
     */
    @Override
    public Value get(Key k) {
        return null;
    }

    /**
     * @param k
     * @param v
     * @return
     */
    @Override
    public Value put(Key k, Value v) {
        return null;
    }

    /**
     * @param k
     * @throws Exception
     */
    @Override
    public void moveToDisk(Key k) throws Exception {

    }

    /**
     * @param pm
     */
    @Override
    public void setPersistenceManager(PersistenceManager<Key, Value> pm) {

    }

    //B-tree node data type
    private static final class Node {
        private int entryCount; // number of entries
        private BTreeImpl.Entry[] entries = new BTreeImpl.Entry[BTreeImpl.MAX]; // the array of children
        private BTreeImpl.Node next;
        private BTreeImpl.Node previous;

        // create a node with k entries
        private Node(int k) {
            this.entryCount = k;
        }

        private BTreeImpl.Node getNext() {
            return this.next;
        }

        private void setNext(BTreeImpl.Node next) {
            this.next = next;
        }

        private BTreeImpl.Node getPrevious() {
            return this.previous;
        }

        private void setPrevious(BTreeImpl.Node previous) {
            this.previous = previous;
        }

        private BTreeImpl.Entry[] getEntries() {
            return Arrays.copyOf(this.entries, this.entryCount);
        }

    }

    //internal nodes: only use key and child
    //external nodes: only use key and value
    public static class Entry {
        private Comparable key;
        private Object val;
        private BTreeImpl.Node child;

        public Entry(Comparable key, Object val, BTreeImpl.Node child) {
            this.key = key;
            this.val = val;
            this.child = child;
        }

        public Object getValue() {
            return this.val;
        }

        public Comparable getKey() {
            return this.key;
        }
    }
}
