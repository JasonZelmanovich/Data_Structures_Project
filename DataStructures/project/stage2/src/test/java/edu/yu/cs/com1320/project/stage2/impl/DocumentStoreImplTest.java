package edu.yu.cs.com1320.project.stage2.impl;

import edu.yu.cs.com1320.project.stage2.DocumentStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

class DocumentStoreImplTest {
    DocumentStoreImpl dstore;
    URI u1, u2;
    String jas, ste;
    DocumentStore.DocumentFormat text, binary;
    InputStream stream2, stream1;

    @BeforeEach
    void setUp() {
        jas = "Jason";
        u1 = URI.create(jas);
        stream1 = new ByteArrayInputStream(jas.getBytes());
        text = DocumentStore.DocumentFormat.TXT;

        ste = "Steve";
        stream2 = new ByteArrayInputStream(ste.getBytes());
        u2 = URI.create(ste);
        binary = DocumentStore.DocumentFormat.BINARY;

        dstore = new DocumentStoreImpl();
    }

    @AfterEach
    void tearDown() {
        dstore = null;
    }

    @Test
    void put() throws IOException {

        assertEquals(dstore.put(stream1, u1, text), 0); // first entry for uri so returns 0
        assertEquals(dstore.get(u1).getDocumentTxt(), "Jason");// Input Stream passed in TEXT: "Jason"
        assertNull(dstore.get(u1).getDocumentBinaryData());
        assertNotEquals(dstore.put(stream2, u1, binary), 0);// second entry returns hashcode of previous doc in table i.e. NOT ZERO
        assertArrayEquals(dstore.get(u1).getDocumentBinaryData(), ste.getBytes());//checks binary array returns values for the string: "Steve"
        assertNull(dstore.get(u1).getDocumentTxt());

        stream2 = new ByteArrayInputStream(ste.getBytes());
        assertEquals(dstore.put(stream2, u2, binary), 0);


        //adding with a doc prior
        int code = dstore.get(u1).hashCode();
        String t = "replaced";
        InputStream str = new ByteArrayInputStream(t.getBytes());

        assertEquals(dstore.put(str, u1, text), code);
        assertEquals(dstore.get(u1).getDocumentTxt(), "replaced");
        assertNull(dstore.get(u1).getDocumentBinaryData());


        code = dstore.get(u2).hashCode();
        str = new ByteArrayInputStream(t.getBytes());

        assertEquals(dstore.put(str, u2, binary), code);
        assertArrayEquals(dstore.get(u2).getDocumentBinaryData(), t.getBytes());
        assertNull(dstore.get(u2).getDocumentTxt());
    }

    @Test
    void get() throws IOException {
        assertEquals(dstore.put(stream1, u1, binary), 0);
        assertArrayEquals(dstore.get(u1).getDocumentBinaryData(), jas.getBytes());
        assertNull(dstore.get(u1).getDocumentTxt());
        assertEquals(dstore.put(stream2, u2, text), 0);
        assertEquals(dstore.get(u2).getDocumentTxt(), ste);
        assertNull(dstore.get(u2).getDocumentBinaryData());

    }

    @Test
    void delete() throws IOException {
        assertEquals(dstore.put(stream1, u1, binary), 0);
        assertFalse(dstore.delete(URI.create("foop")));
        assertTrue(dstore.delete(u1));
        assertFalse(dstore.delete(u1));

        assertEquals(dstore.put(stream2, u2, text), 0);
        assertFalse(dstore.delete(URI.create("testing123")));
        assertTrue(dstore.delete(u2));
        assertFalse(dstore.delete(u2));
    }

    @Test
    void undoTest() throws IOException {
        dstore.put(stream1, u1, text);
        assertNotNull(dstore.get(u1));
        assertTrue(dstore.delete(u1));
        assertNull(dstore.get(u1));
        dstore.undo();
        assertNotNull(dstore.get(u1));

        dstore.put(stream2, u1, binary);
        dstore.undo();

        while (dstore.cmdStack.peek() != null) {
            System.out.println(dstore.cmdStack.pop().toString());
        }
    }

}