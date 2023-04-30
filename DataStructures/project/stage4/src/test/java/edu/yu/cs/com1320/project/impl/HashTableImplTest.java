package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.impl.HashTableImpl;
import org.testng.annotations.Test;

import static org.junit.jupiter.api.Assertions.*;
class HashTableImplTest {
    HashTableImpl<Integer, String> c = new HashTableImpl<>();
    HashTableImpl<String, String> f = new HashTableImpl<>();

    @Test
    void putIntString() {
        String s = c.put(19, "Jason");
        assertNull(s);
        s = c.put(19, "changed Value");
        assertEquals(s, "Jason");

        s = c.put(36, "YU");
        assertNull(s);

        s = c.put(49, "Target");
        assertNull(s);

    }

    @Test
    void putStringString() {
        String s = f.put("Shalom", "Hello");
        assertNull(s);
        s = f.put("Shalom", "UpdatedHello");
        assertEquals(s, "Hello");
        s = f.get("Shalom");
        assertEquals(s, "UpdatedHello");
    }

    /*Test for collisions - ensure base array (size 5) using separate chaining is functioning properly
      1) put 100 <integer,integer> into a HashTable Impl
      2) key will be a 1 to 100, Value will be key + 18
      3) for loop assigning values and ensuring return value is null for each put
      4) for loop getting values and ensuring return value == key + 18
      5) update keys for value == key + 36 - check for old return value in put and new value in get
     */
    @Test
    void collisionTestingInteger() {
        for (int i = 1; i <= 100; i++) {
            assertNull(c.put(i, i + 18));
        }
        for (int i = 1; i <= 100; i++) {
            assertEquals(c.get(i), i + 18);
        }
        for (int i = 1; i <= 100; i++) {
            assertEquals(c.put(i, i + 36), i + 18);
        }
        for (int i = 1; i <= 100; i++) {
            assertEquals(c.get(i), i + 36);
        }
    }

    /*
    1)
     */
    @Test
    void collisionTestingString() {
        for (int i = 65; i <= 88; i++) {
            String k = "";
            String v = "";
            for (int j = i; j <= i + 2; j++) {
                k += (char) j;
            }
            for (int j = i + 2; j >= i; j--) {
                v += (char) j;
            }
            assertNull(f.put(k, v));
        }

        for (int i = 97; i <= 120; i++) {
            String k = "";
            String v = "";
            for (int j = i; j <= i + 2; j++) {
                k += (char) j;
            }
            for (int j = i + 2; j >= i; j--) {
                v += (char) j;
            }
            assertNull(f.put(k, v));
        }

        for (int i = 65; i <= 87; i++) {
            String k = "";
            String v = "";
            for (int j = i; j <= i + 2; j++) {
                k += (char) j;
            }
            for (int j = i + 2; j >= i; j--) {
                v += (char) j;
            }
            assertEquals(f.get(k), v);
        }

        for (int i = 97; i <= 120; i++) {
            String k = "";
            String v = "";
            for (int j = i; j <= i + 2; j++) {
                k += (char) j;
            }
            for (int j = i + 2; j >= i; j--) {
                v += (char) j;
            }
            assertEquals(f.get(k), v);
        }
    }

    @Test
    void get() {
        c.put(19, "Jason");
        c.put(19, "changed Value");
        c.put(36, "YU");
        c.put(49, "Target");
        assertEquals(c.get(19), "changed Value");
        assertEquals(c.get(36), "YU");
        assertEquals(c.get(49), "Target");
        c.put(49, null);
        assertNull(c.get(49));
    }

    @Test
    void containsKey() {
        c.put(19, "Jason");
        c.put(19, "changed Value");
        c.put(36, "YU");
        c.put(49, "Target");
        assertTrue(c.containsKey(19));
        assertTrue(c.containsKey(36));
        assertTrue(c.containsKey(49));
        c.put(49, null);
        assertFalse(c.containsKey(49));

        c.put(55, null);
        assertFalse(c.containsKey(55));

        assertNull(f.put("Jason", "jay"));
        assertTrue(f.containsKey("Jason"));
        assertEquals(f.put("Jason", null), "jay");
        assertFalse(f.containsKey("Jason"));
    }
}