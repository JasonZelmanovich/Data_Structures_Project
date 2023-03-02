package edu.yu.cs.com1320.project.stage1.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

class DocumentImplTest {
    DocumentImpl doc;
    DocumentImpl doc2;
    DocumentImpl var1;
    byte[] b;

    @BeforeEach
    void setUp() {
        doc = new DocumentImpl(URI.create("Hello"), "Testing");
        String s = "Testing";
        b = s.getBytes();
        doc2 = new DocumentImpl(URI.create("beep"), b);
        var1 = new DocumentImpl(URI.create("foop"), "Shalom");

    }

    @Test
    void testEquals() {
        assertNotEquals(doc, doc2);
        assertNotEquals(doc, var1);
        assertNotEquals(doc2, var1);
        DocumentImpl var2 = new DocumentImpl(URI.create("foop"), "Shalom");
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
}