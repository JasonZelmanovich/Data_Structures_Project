package edu.yu.cs.com1320.project.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StackImplTest {
    static StackImpl<Integer> I = new StackImpl<>();
    static StackImpl<String> S = new StackImpl<>();

    @AfterEach
    void tearDown() {
        I = new StackImpl<>();
        S = new StackImpl<>();
    }

    @Test
    void push() {
        for (int i = 0; i < 100; i++) {
            I.push(i);
        }
        assertEquals(I.size(), 100);
        assertEquals(I.peek(), 99);

        for (int i = 65; i <= 88; i++) {
            String k = "";
            k += (char) i;
            S.push(k);
            assertEquals(S.peek(), k);
        }
        assertEquals(S.size(), 24);
        assertNotNull(S.pop());
        assertEquals(S.size(), 23);
    }

    @Test
    void pop() {
        for (int i = 0; i < 100; i++) {
            I.push(i);
        }

        for (int i = 99; i >= 0; i--) {
            assertEquals(I.pop(), i);
        }
        assertNull(I.pop());

        for (int i = 65; i <= 88; i++) {
            String k = "";
            k += (char) i;
            S.push(k);
            assertEquals(S.peek(), k);
        }
        for (int i = 0; i < 24; i++) {
            assertNotNull(S.pop());
        }
        assertNull(S.pop());
    }

    @Test
    void peek() {
        int[] arr = new int[200];
        for (int i = 0; i < 200; i++) {
            int r = (int) (Math.random() * 1000);
            I.push(r);
            arr[i] = r;
        }
        for (int i = 199; i >= 0; i--) {
            assertEquals(I.peek(), arr[i]);
            assertEquals(I.pop(), arr[i]);
        }
        assertNull(I.peek());
        assertNull(I.pop());
    }

    @Test
    void size() {
        for (int i = 0; i < 100; i++) {
            I.push(i);
        }
        for (int i = 0; i < 100; i++) {
            I.push(i);
        }
        for (int i = 0; i < 200; i++) {
            int r = (int) (Math.random() * 1000);
            I.push(r);
        }

        for (int i = 65; i <= 88; i++) {
            String k = "";
            k += (char) i;
            S.push(k);
            assertEquals(S.peek(), k);
        }
        for (int i = 65; i <= 88; i++) {
            String k = "";
            k += (char) i;
            S.push(k);
            assertEquals(S.peek(), k);
        }
        assertEquals(I.size(), 400);
        assertEquals(I.peek(), I.pop());
        assertEquals(I.size(), 399);

        assertEquals(S.size(), 48);
        assertEquals(S.peek(), S.pop());
        assertEquals(S.size(), 47);
    }
}