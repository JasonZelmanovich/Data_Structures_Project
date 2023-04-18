package edu.yu.cs.com1320.project.stage3.impl;

import edu.yu.cs.com1320.project.stage3.Document;
import edu.yu.cs.com1320.project.stage3.DocumentStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DocumentStoreImplTest {
    DocumentStoreImpl dstore;
    URI u1, u2, u3, u4, u5, u6;
    String jas, ste, text1, text2, text3, text4;
    DocumentStore.DocumentFormat text, binary;
    InputStream stream2, stream1, stream3, stream4, stream5, stream6;

    @BeforeEach
    void setUp() throws IOException {
        jas = "Jason";
        u1 = URI.create(jas);
        stream1 = new ByteArrayInputStream(jas.getBytes());
        text = DocumentStore.DocumentFormat.TXT;

        ste = "Steve";
        stream2 = new ByteArrayInputStream(ste.getBytes());
        u2 = URI.create(ste);
        binary = DocumentStore.DocumentFormat.BINARY;

        dstore = new DocumentStoreImpl();

        //Doc 1
        text1 = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin eget urna nulla. Interdum et malesuada fames ac ante ipsum primis in faucibus. Aenean eu leo non turpis auctor dapibus. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. In volutpat maximus lectus, quis tincidunt leo rutrum nec. Aenean sit amet sollicitudin leo, non ultricies nunc. Sed tempor a est non feugiat. Sed molestie, purus ac cursus pellentesque, ex ligula dictum enim, quis volutpat magna nisl ac enim. Duis consectetur, lectus in tempus rhoncus, sapien ante ullamcorper nulla, quis venenatis lacus massa ut sapien. Phasellus dignissim mauris lacus, quis consequat turpis vestibulum at";
        u3 = URI.create("text1");
        stream3 = new ByteArrayInputStream(text1.getBytes());

        text2 = "Etiam consectetur mollis fringilla. Donec eros eros, accumsan non pretium euismod, ultrices sagittis velit. Pellentesque rhoncus consectetur nibh et commodo. Maecenas ut volutpat nisl, et vehicula ante. Cras dignissim urna fringilla enim feugiat aliquam. Nam vitae orci fermentum, condimentum nisl at, rhoncus velit. Fusce quis placerat erat, eu maximus nunc. Aenean vestibulum mi eget dui congue, eu lobortis diam volutpat. In placerat tortor vel metus sagittis hendrerit. Mauris tempor consectetur ligula vitae venenatis. Nulla interdum justo orci, eu scelerisque felis placerat vel. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus quam arcu, maximus non interdum ac, commodo ac sapien.";
        u4 = URI.create("text2");
        stream4 = new ByteArrayInputStream(text2.getBytes());

        text3 = "Sed sit amet nibh in tellus rutrum posuere. Suspendisse ornare odio augue, nec fermentum ligula pretium eget. Maecenas in turpis sed elit tristique sollicitudin quis eget nulla. Vestibulum odio tortor, posuere a diam nec, elementum fermentum mi. Praesent mattis feugiat lorem ut finibus. Quisque velit massa, feugiat sit amet egestas non, pretium a lorem. Integer sed massa bibendum, ornare ligula a, fringilla est. Maecenas eget diam sed sapien congue mattis.";
        u5 = URI.create("text3");
        stream5 = new ByteArrayInputStream(text3.getBytes());

        text4 = "Donec volutpat erat sed ante convallis, vel commodo dolor eleifend. Vivamus sed fringilla nisi, iaculis semper orci. Nulla id fringilla urna. Nam venenatis libero quis ipsum cursus elementum. Aliquam ultrices, velit ac eleifend suscipit, ex mi tincidunt tellus, porttitor accumsan lectus sem id mi. Maecenas nisi dui, maximus eget mauris sit amet, mattis blandit dolor. Proin efficitur orci purus, eget iaculis justo dignissim a. Sed eleifend sem ac lectus pretium, id malesuada purus volutpat. Cras porttitor varius est, eu malesuada augue dictum eu. Cras nisl enim, aliquam nec arcu eget, dapibus cursus arcu. Etiam fringilla accumsan cursus. In ac finibus enim, nec accumsan elit.";
        u6 = URI.create("text4");
        stream6 = new ByteArrayInputStream(text4.getBytes());

        dstore.put(stream3, u3, text);
        dstore.put(stream4, u4, text);
        dstore.put(stream5, u5, text);
        dstore.put(stream6, u6, text);
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

        dstore.put(stream2, u1, text);
        InputStream in = new ByteArrayInputStream(jas.getBytes());
        dstore.put(in, u2, text);
        assertEquals(dstore.get(u1).getDocumentTxt(), "Steve");
        dstore.undo(u1);
        assertEquals(dstore.get(u1).getDocumentTxt(), "Jason");
        dstore.undo(u1);
        assertEquals(dstore.get(u2).getDocumentTxt(), "Jason");
        assertThrows(IllegalStateException.class, () -> {
            dstore.undo(u1);
        });
        assertDoesNotThrow(() -> {
            dstore.undo(u2);
        });
        for (int i = 0; i < 4; i++) { //since setup adds 4 documents
            dstore.undo();
        }
        assertThrows(IllegalStateException.class, () -> {
            dstore.undo();
        });

        URI[] uriArray = new URI[24];
        String k = "";
        for (int i = 65; i <= 88; i++) {
            k += (char) i;
            URI u = URI.create(k);
            InputStream stream = new ByteArrayInputStream(k.getBytes());
            dstore.put(stream, u, DocumentStore.DocumentFormat.TXT);
            uriArray[i - 65] = u;
        }
        assertNotNull(dstore.get(uriArray[0]));

        dstore.undo(uriArray[1]);

        assertNotNull(dstore.get(uriArray[0]));

        assertNull(dstore.get(uriArray[1]));
    }

    @Test
    void search() throws IOException {
        List<Document> l = dstore.search("lorem");
        for (Document d : l) {
            assertEquals(d.getKey(), URI.create("text3"));
        }

        l = dstore.search("Lorem");
        ArrayList<URI> t = new ArrayList<>();
        for (Document d : l) {
            System.out.println("Lorem search: " + d.getKey());
            t.add(d.getKey());
        }
        assertEquals(t, Arrays.asList(URI.create("text2"), URI.create("text1")));

        l = dstore.search("sit");
        t = new ArrayList<>();
        for (Document d : l) {
            t.add(d.getKey());
            System.out.println("sit:" + d.getKey());
        }
        assertEquals(t, Arrays.asList(URI.create("text3"), URI.create("text1"), URI.create("text4"), URI.create("text2")));

        String tempText = "Test for binary document";
        InputStream temp = new ByteArrayInputStream(tempText.getBytes());
        dstore.put(temp, URI.create("Temp"), binary);
        assertEquals(dstore.search("Test").size(), 0);

        l = dstore.search("ipsum");
        t = new ArrayList<>();
        for (Document d : l) {
            t.add(d.getKey());
            System.out.println(d.getKey() + ": contains 'ipsum'");
        }
        assertEquals(t, Arrays.asList(URI.create("text1"), URI.create("text4"), URI.create("text2")));

        l = dstore.search("");
        assertEquals(l, Collections.EMPTY_LIST);
    }

    @Test
    void searchByPrefix() throws IOException {
        List<Document> l = dstore.searchByPrefix("lor");
        ArrayList<URI> t = new ArrayList<>();
        for (Document d : l) {
            System.out.println(d.getKey() + ": contains prefix 'lor'");
            t.add(d.getKey());
        }
        assertEquals(t, List.of(URI.create("text3")));

        String tempText = "Test for binary document";
        InputStream temp = new ByteArrayInputStream(tempText.getBytes());
        dstore.put(temp, URI.create("Temp"), binary);
        assertEquals(dstore.search("Test").size(), 0);

        l = dstore.searchByPrefix("");
        for (Document d : l) {
            System.out.println(d.getKey() + ": contains prefix ''"); //returns all documents except for temp doc bc binary
        }

        l = dstore.searchByPrefix("li");
        t = new ArrayList<>();
        for (Document d : l) {
            System.out.println(d.getKey() + ": contains prefix 'li'");
            t.add(d.getKey());
        }
        assertEquals(t, Arrays.asList(URI.create("text3"), URI.create("text4"), URI.create("text2"), URI.create("text1")));

        l = dstore.searchByPrefix("foop");
        assertEquals(l, Collections.EMPTY_LIST);
    }

    @Test
    void deleteAll() {
        Set<URI> del = dstore.deleteAll("Lorem");
        for (URI u : del) {
            System.out.println(u);
        }

        assertEquals(dstore.search("Lorem"), Collections.EMPTY_LIST);
        List<Document> l = dstore.searchByPrefix("");
        List<URI> t = new ArrayList<>();
        for (Document d : l) {
            t.add(d.getKey());
            System.out.println(d.getKey() + ": contains prefix ''"); //Only return text3, text4
        }
        assertEquals(t, Arrays.asList(URI.create("text4"), URI.create("text3")));

        l = dstore.searchByPrefix("a");
        t = new ArrayList<>();
        for (Document d : l) {
            t.add(d.getKey());
            System.out.println(d.getKey() + ": contains prefix 'a'"); //Only return text3, text4
        }
        assertEquals(t, Arrays.asList(URI.create("text4"), URI.create("text3")));

        dstore.undo(URI.create("text2"));
        l = dstore.search("Lorem");
        t = new ArrayList<>();
        for (Document d : l) {
            t.add(d.getKey());
            System.out.println(d.getKey());
        }
        assertEquals(t, List.of(URI.create("text2")));

        dstore.undo();
        l = dstore.search("Lorem");
        t = new ArrayList<>();
        for (Document d : l) {
            t.add(d.getKey());
            System.out.println(d.getKey());
        }
        assertEquals(t, Arrays.asList(URI.create("text2"), URI.create("text1")));
        dstore.undo(URI.create("text2"));
        l = dstore.searchByPrefix("");
        t = new ArrayList<>();
        assertEquals(l.size(), 3);
        for (Document d : l) {
            System.out.println("ALl but text 2" + d.getKey());
            t.add(d.getKey());
        }
        assertFalse(t.contains(URI.create("text2")));

        dstore.undo();
        l = dstore.searchByPrefix("");
        t = new ArrayList<>();
        assertEquals(l.size(), 2);
        for (Document d : l) {
            System.out.println("ALl but text 2 & 4" + d.getKey());
            t.add(d.getKey());
        }
        assertFalse(t.contains(URI.create("text4")));

        dstore.undo();
        dstore.undo();
        assertThrows(IllegalStateException.class, () -> {
            dstore.undo();
        });
    }

    @Test
    void deleteAllWithPrefix() {
        Set<URI> del = dstore.deleteAllWithPrefix("dol");
        for (URI u : del) {
            System.out.println("Deleted doc with prefix 'dol' :" + u);
        }
        assertFalse(del.contains(URI.create("text3")));

        List<Document> l = dstore.searchByPrefix("");
        List<URI> t = new ArrayList<>();
        for (Document d : l) {
            t.add(d.getKey());
            System.out.println(d.getKey() + ": contains prefix ''"); //Only return text3
        }
        assertEquals(t, List.of(URI.create("text3")));

        //undo deleted element text1 and check its existence
        dstore.undo(URI.create("text1"));
        l = dstore.searchByPrefix("");
        t = new ArrayList<>();
        for (Document d : l) {
            t.add(d.getKey());
            System.out.println(d.getKey() + ": after undo, contains prefix ''"); //Only return text3
        }
        assertEquals(t, Arrays.asList(URI.create("text1"), URI.create("text3")));

        dstore.deleteAllWithPrefix("");//delete all documents
        assertEquals(dstore.deleteAllWithPrefix("a"), Collections.EMPTY_SET);
    }
}