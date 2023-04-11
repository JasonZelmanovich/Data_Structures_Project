package edu.yu.cs.com1320.project.impl;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrieImplTest {

    @Test
    void putAndGetAllSorted() {
        TrieImpl<Integer> trie = new TrieImpl<>();
        trie.put("Jason",15);
        trie.put("Jason",26);
        List l = trie.getAllSorted("Jason", new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1-o2;
            }
        });
        ArrayList<Integer> expected = new ArrayList<>(Arrays.asList(26,15));
        assertEquals(l,expected);
    }

    @Test
    void getAllWithPrefixSorted() {
    }

    @Test
    void deleteAllWithPrefix() {
    }

    @Test
    void deleteAll() {
    }

    @Test
    void delete() {
    }
}