package edu.yu.cs.com1320.project.stage5.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

class DocumentImplTest {
    DocumentImpl doc, doc2, doc6Words, var1, doc55;
    byte[] b;

    @BeforeEach
    void setUp() {
        doc = new DocumentImpl(URI.create("Hello"), "Testing",null);
        String s = "Testing";
        b = s.getBytes();
        doc2 = new DocumentImpl(URI.create("beep"), b);
        var1 = new DocumentImpl(URI.create("foop"), "Shalom",null);
        doc6Words = new DocumentImpl(URI.create("doc3"), "Hello this document has six words",null);
        doc55 = new DocumentImpl((URI.create("doc55")), "This document has the letter 5 five times 5 and 5, more 5 and also 5 and the word and 4 times",null);
    }

    @Test
    void testEquals() {
        assertNotEquals(doc, doc2);
        assertNotEquals(doc, var1);
        assertNotEquals(doc2, var1);
        DocumentImpl var2 = new DocumentImpl(URI.create("foop"), "Shalom",null);
        assertEquals(var2, var1);
    }

    @Test
    void getDocumentTxt() {
        assertEquals(doc.getDocumentTxt(), "Testing");
        assertNull(doc2.getDocumentTxt());
    }

    @Test
    void getDocumentBinaryData() {
        assertNull(doc.getDocumentBinaryData());
        assertEquals(doc2.getDocumentBinaryData(), b);
    }

    @Test
    void getKey() {
        assertEquals(doc.getKey(), URI.create("Hello"));
        assertEquals(doc2.getKey(), URI.create("beep"));
    }

    @Test
    void wordCount() {
        assertEquals(doc.wordCount("Jaosn"), 0);
        assertEquals(doc.wordCount("Testing"), 1);
        assertEquals(doc2.wordCount("Jason"), 0);
        assertEquals(doc.wordCount(""), 0);
        assertEquals(doc6Words.wordCount("six"), 1);
        assertEquals(doc55.wordCount("5"), 5);
        assertEquals(doc55.wordCount("and"), 4);
    }

    @Test
    void getWords() {
        assertTrue(doc.getWords().contains("Testing"));
        assertEquals(doc.getWords().size(), 1);
        assertEquals(doc2.getWords().size(), 0);
        assertEquals(doc6Words.getWords().size(), 6);
        System.out.println(doc6Words.getWords());
    }
}