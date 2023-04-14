package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.stage3.Document;
import edu.yu.cs.com1320.project.stage3.impl.DocumentImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TrieImplTest {
    TrieImpl<Integer> trieInt;
    @BeforeEach
    void setUp() {
        trieInt = new TrieImpl<>();
        trieInt.put("Jason",15);
        trieInt.put("Jason",26);
        trieInt.put("Jason", 16);
        trieInt.put("Jason",14);
        trieInt.put("Java",28);
        trieInt.put("Japan",29);
        trieInt.put("Jack",98);
        trieInt.put("Jasoaop",76);
    }

    @AfterEach
    void tearDown() {
        trieInt = null;
    }

    @Test
    void putAndGetAllSorted() {
        List l = trieInt.getAllSorted("Jason", new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2-o1;
            }
        });
        ArrayList<Integer> expected = new ArrayList<>(Arrays.asList(26,16,15,14));
        assertEquals(l,expected);
    }

    @Test
    void getAllWithPrefixSorted() {
        Comparator c = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2-o1;
            }
        };
        List l = trieInt.getAllWithPrefixSorted("Jas",c);
        ArrayList<Integer> expected = new ArrayList<>(Arrays.asList(76,26,16,15,14));
        assertEquals(l,expected);
        List m = trieInt.getAllWithPrefixSorted("Ja",c);
        assertEquals(m.size(),8);
    }

    @Test
    void deleteAllWithPrefix() {
        Set del = trieInt.deleteAllWithPrefix("Jas");
        assertEquals(del.size(),5);
        del = trieInt.deleteAllWithPrefix("J");
        assertEquals(del.size(),3);
    }

    @Test
    void deleteAll() {
        Set del = trieInt.deleteAll("Jason");
        assertEquals(del.size(),4);

        del = trieInt.deleteAll("Jack");
        assertEquals(del.size(),1);

        del = trieInt.deleteAll("Japan");
        assertEquals(del.size(),1);

        trieInt.put("Jasoaop",64);
        del = trieInt.deleteAll("Jasoaop");
        assertEquals(del.size(),2);
    }

    @Test
    void delete() {
        assertEquals(trieInt.delete("Jason",15),15);
        assertNull(trieInt.delete("Jason",15));
        assertEquals(trieInt.delete("Java",28),28);
    }
}