package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.HashTable;


public class HashTableImpl<Key, Value> implements HashTable {
    private final CustomLinkedList[] entries;

    public HashTableImpl() {
        this.entries = new CustomLinkedList[5];
    }

    /**
     * @param k the key whose value should be returned
     * @return the value that is stored in the HashTable for k, or null if there is no such key in the table
     */
    @Override
    public Value get(Object k) {
        if (k == null) {
            return null;
        }
        Key currentK = (Key) k;
        int index = hash(currentK);
        CustomLinkedList<Key, Value> temp = entries[index];
        if (temp != null) {
            if (temp.getKey().equals(currentK)) {
                return temp.getValue();
            } else {
                while (temp.getNext() != null) {
                    temp = temp.getNext();
                    if (temp.getKey().equals(currentK)) {
                        return temp.getValue();
                    }
                }

            }
        }
        return null;
    }

    /**
     * @param k the key at which to store the value
     * @param v the value to store.
     *          To delete an entry, put a null value.
     * @return if the key was already present in the HashTable, return the previous value stored for the key. If the key was not already present, return null.
     */
    @Override
    public Value put(Object k, Object v) {
        Key currentK = (Key) k;
        Value currentV = (Value) v;
        int index = hash(currentK);
        CustomLinkedList<Key, Value> temp = entries[index];
        if (currentV == null) {
            return delete(currentK, index);
        }
        if (temp != null) {
            if (temp.getKey().equals(currentK)) {
                return temp.put(currentV);
            } else {
                while (temp.getNext() != null) {
                    temp = temp.getNext();
                    if (temp.getKey().equals(currentK)) {
                        return temp.put(currentV);
                    }
                }
                temp.next = new CustomLinkedList<>(currentK, currentV);
                return null;
            }
        } else {
            entries[index] = new CustomLinkedList<>(currentK, currentV);
        }
        return null;
    }

    /**
     * @param o the key whose presence in the hashtable we are inquiring about
     * @return true if the given key is present in the hashtable as a key, false if not
     * @throws NullPointerException if the specified key is null
     */
    @Override
    public boolean containsKey(Object o) {
        if (o == null) {
            throw new NullPointerException();
        }
        Key k = (Key) o;
        int index = hash(k);
        CustomLinkedList<Key, Value> temp = entries[index];
        if (temp != null) {
            if (temp.getKey().equals(k)) {
                return true;
            }
            while (temp.getNext() != null) {
                temp = temp.getNext();
                if (temp.getKey().equals(k)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Value delete(Key k, int index) {
        CustomLinkedList<Key, Value> temp = entries[index];
        if (temp != null) {
            if (temp.getKey().equals(k)) {
                Value old = temp.getValue();
                entries[index] = temp.next;
                return old;
            } else {
                while (temp.getNext() != null) {
                    CustomLinkedList prev = temp;
                    temp = temp.getNext();
                    if (temp.getKey().equals(k)) {
                        Value old = temp.getValue();
                        prev.next = temp.next;
                        return old;
                    }
                }
            }
        }
        return null;
    }

    private int hash(Key k) {
        return (k.hashCode() & 0x7fffffff) % this.entries.length;
    }

    private class CustomLinkedList<key, value> {
        private final key k;
        private value v;
        private CustomLinkedList next = null;

        private CustomLinkedList(key k, value v) {
            this.k = k;
            this.v = v;
        }

        private key getKey() {
            return this.k;
        }

        private value getValue() {
            return this.v;
        }

        private value put(value v) {
            value old = this.v;
            this.v = v;
            return old;
        }

        private CustomLinkedList getNext() {
            return this.next;
        }

    }
}