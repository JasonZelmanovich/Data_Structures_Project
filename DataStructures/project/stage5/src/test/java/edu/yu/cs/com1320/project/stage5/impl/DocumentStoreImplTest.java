package edu.yu.cs.com1320.project.stage5.impl;

import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.DocumentStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.SecureRandom;
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

        text2 = "Etiam consectetur mollis fringilla. Donec eros eros, li accumsan non pretium euismod, ultrices sagittis velit. Pellentesque rhoncus consectetur nibh et commodo. Maecenas ut volutpat nisl, et vehicula ante. Cras dignissim urna fringilla enim feugiat aliquam. Nam vitae orci fermentum, condimentum nisl at, rhoncus velit. Fusce quis placerat erat, eu maximus nunc. Aenean vestibulum mi eget dui congue, eu lobortis diam volutpat. In placerat tortor vel metus sagittis hendrerit. Mauris tempor consectetur ligula vitae venenatis. Nulla interdum justo orci, eu scelerisque felis placerat vel. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus quam arcu, maximus non interdum ac, commodo ac sapien.";
        u4 = URI.create("text2");
        stream4 = new ByteArrayInputStream(text2.getBytes());

        text3 = "Sed sit amet nibh in tellus rutrum posuere. li li Suspendisse ornare odio augue, nec fermentum ligula pretium eget. Maecenas in turpis sed elit tristique sollicitudin quis eget nulla. Vestibulum odio tortor, posuere a diam nec, elementum fermentum mi. Praesent mattis feugiat lorem ut finibus. Quisque velit massa, feugiat sit amet egestas non, pretium a lorem. Integer sed massa bibendum, ornare ligula a, fringilla est. Maecenas eget diam sed sapien congue mattis.";
        u5 = URI.create("text3");
        stream5 = new ByteArrayInputStream(text3.getBytes());

        text4 = "Donec volutpat erat sed ante convallis, vel li li li commodo dolor eleifend. Vivamus sed fringilla nisi, iaculis semper orci. Nulla id fringilla urna. Nam venenatis libero quis ipsum cursus elementum. Aliquam ultrices, velit ac eleifend suscipit, ex mi tincidunt tellus, porttitor accumsan lectus sem id mi. Maecenas nisi dui, maximus eget mauris sit amet, mattis blandit dolor. Proin efficitur orci purus, eget iaculis justo dignissim a. Sed eleifend sem ac lectus pretium, id malesuada purus volutpat. Cras porttitor varius est, eu malesuada augue dictum eu. Cras nisl enim, aliquam nec arcu eget, dapibus cursus arcu. Etiam fringilla accumsan cursus. In ac finibus enim, nec accumsan elit.";
        u6 = URI.create("text4");
        stream6 = new ByteArrayInputStream(text4.getBytes());

//        dstore.put(stream3, u3, text);
//        dstore.put(stream4, u4, text);
//        dstore.put(stream5, u5, text);
//        dstore.put(stream6, u6, text);
    }

    @AfterEach
    void tearDown() {
        dstore = null;
    }

    public static byte[] generateRandomByteArray(int length) {
        SecureRandom random = new SecureRandom();
        byte[] byteArray = new byte[length];
        random.nextBytes(byteArray);
        return byteArray;
    }

    public static URI generateRandomURI() {
        String scheme = "http";
        String host = "example.com";
        int port = 8080;
        String path = "/path/to/resource";

        Random random = new Random();
        String uuid = Integer.toString(random.nextInt(100000));
        String uriString = String.format("%s://%s:%d%s/%s", scheme, host, port, path, uuid);
        return URI.create(uriString);
    }

    public static String generateRandomString(int length) {
        final String alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        final SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; ++i) {
            sb.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }

        return sb.toString();
    }

    @Test
    void RemoveAllTraceWhenDocInCmdSet() throws IOException {
        dstore = new DocumentStoreImpl();
        List<URI> docArray = new ArrayList<>();
        dstore.setMaxDocumentBytes(500);
        String s = "Delete togethe should be 10 of these and 50 length";
        for (int i = 0; i < 10; i++) {
            URI uri = generateRandomURI();
            InputStream in = new ByteArrayInputStream(s.getBytes());
            dstore.put(in, uri, DocumentStore.DocumentFormat.TXT);
            docArray.add(uri);
        }
        dstore.deleteAll("Delete"); // delete all with given keyword
        s = "This one to be deletedd from cmdset";
        dstore.put(new ByteArrayInputStream(s.getBytes()), docArray.get(0), DocumentStore.DocumentFormat.TXT);
        dstore.setMaxDocumentBytes(0);//should remove all trace of one of the docs (check for in the cmd set)
        //int len = dstore.search("Delete").size();
        //assertEquals(len,9);

    }

    @Test
    void deleteExisting() throws IOException {
        dstore = new DocumentStoreImpl();
        List<URI> docArray = new ArrayList<>();
        dstore.setMaxDocumentBytes(1000);
        for (int i = 0; i < 10; i++) {
            URI uri = generateRandomURI();
            InputStream in = new ByteArrayInputStream(generateRandomByteArray(100));
            dstore.put(in, uri, DocumentStore.DocumentFormat.BINARY);
            docArray.add(uri);
        }
        for (int i = 0; i < 5; i++) {
            dstore.put(null, docArray.get(i), DocumentStore.DocumentFormat.TXT);
        }
        for (int i = 0; i < 5; i++) {
            assertNull(dstore.get(docArray.get(i)));
        }
        for (int i = 0; i < 5; i++) {
            dstore.undo(docArray.get(i));
        }
        for (int i = 0; i < 5; i++) {
            assertNotNull(dstore.get(docArray.get(i)));
        }
    }

    @Test
    void putWithReplace() throws IOException {
        dstore = new DocumentStoreImpl();
        List<URI> docArray = new ArrayList<>();
        dstore.setMaxDocumentBytes(1000);
        for (int i = 0; i < 10; i++) {
            URI uri = generateRandomURI();
            InputStream in = new ByteArrayInputStream(generateRandomByteArray(100));
            dstore.put(in, uri, DocumentStore.DocumentFormat.BINARY);
            docArray.add(uri);
        }
        for (int i = 0; i < 2; i++) {
            InputStream in = new ByteArrayInputStream(new String("Shoudld be in").getBytes());
            dstore.put(in, docArray.get(i), DocumentStore.DocumentFormat.TXT);
        }
        for (int i = 0; i < 2; i++) {
            Document doc = new DocumentImpl(docArray.get(i), new String("Shoudld be in"),null);
            assertEquals(dstore.get(docArray.get(i)), doc);
        }
    }

    @Test
    void putThenDeleteThenUndo_memory() throws IOException {
        dstore = new DocumentStoreImpl();
        List<URI> docArray = new ArrayList<>();
        Document[] docs = new Document[10];

        for (int i = 0; i < 10; i++) {
            URI uri = generateRandomURI();
            docs[i] = new DocumentImpl(uri, "test",null);
            InputStream in = new ByteArrayInputStream(generateRandomByteArray(100));
            dstore.put(in, uri, DocumentStore.DocumentFormat.BINARY);
            docArray.add(uri);
        }
        for (URI u : docArray) {
            dstore.delete(u);
        }
        dstore.setMaxDocumentBytes(100);
        for (int i = 9; i >= 0; i--) {
            dstore.undo();
            assertEquals(dstore.get(docs[i].getKey()).getKey(), docs[i].getKey());
        }
    }

    @Test
    void putWithDocsOverLimitCount() throws IOException {
        dstore = new DocumentStoreImpl();
        dstore.setMaxDocumentCount(10);
        List<URI> docArray = new ArrayList<>();
        Document[] docs = new Document[10];
        for (int i = 0; i < 10; i++) {
            URI uri = generateRandomURI();
            docs[i] = new DocumentImpl(uri, "test", null);
            InputStream in = new ByteArrayInputStream(generateRandomByteArray(100));
            dstore.put(in, uri, DocumentStore.DocumentFormat.BINARY);
            docArray.add(uri);
        }
        //put one doc over the limit
        URI u = URI.create("Extrashouldbeherebottomofheap");
        dstore.put(new ByteArrayInputStream(generateRandomByteArray(1)), u, DocumentStore.DocumentFormat.BINARY);
        assertEquals(dstore.get(u).getKey(), u);
        assertNotNull(dstore.get(docArray.get(0)));
        //put multiple over the limit
        ArrayList<URI> newUri = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            URI p = generateRandomURI();
            newUri.add(p);
            InputStream in = new ByteArrayInputStream(generateRandomByteArray(100));
            dstore.put(in, p, DocumentStore.DocumentFormat.BINARY);
        }
        for (URI p : newUri) {
            assertNotNull(dstore.get(p));
        }
        for (URI p : docArray) {
            assertNotNull(dstore.get(p)); //stage 5 all stay in now since they are offloaded to disk
        }
    }

    @Test
    void putWithDocsOverLimitByte() throws IOException {
        //Bytes Test
        dstore = new DocumentStoreImpl();
        dstore.setMaxDocumentBytes(1000);
        List<URI> docArray = new ArrayList<>();
        Document[] docs = new Document[10];
        for (int i = 0; i < 10; i++) {
            URI uri = generateRandomURI();
            docs[i] = new DocumentImpl(uri, "test", null);
            InputStream in = new ByteArrayInputStream(generateRandomByteArray(100));
            dstore.put(in, uri, DocumentStore.DocumentFormat.BINARY);
            docArray.add(uri);
        }
        //put one doc over the limit
        URI u = URI.create("Extrashouldbeherebottomofheap");
        dstore.put(new ByteArrayInputStream(generateRandomByteArray(1)), u, DocumentStore.DocumentFormat.BINARY);
        assertEquals(dstore.get(u).getKey(), u);
        assertNotNull(dstore.get(docArray.get(0)));
        //put multiple over the limit
        ArrayList<URI> newUri = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            URI p = generateRandomURI();
            newUri.add(p);
            InputStream in = new ByteArrayInputStream(generateRandomByteArray(100));
            dstore.put(in, p, DocumentStore.DocumentFormat.BINARY);
        }
        for (URI p : newUri) {
            assertNotNull(dstore.get(p));
        }
        for (URI p : docArray) {
            assertNotNull(dstore.get(p));
        }
    }

    @Test
    void put() throws IOException {
        assertEquals(dstore.put(stream1, u1, text), 0); // first entry for uri so returns 03
        assertEquals(dstore.get(u1).getDocumentTxt(), "Jason");// Input Stream passed in TEXT: "Jason"
        assertNull(dstore.get(u1).getDocumentBinaryData());
        assertNotEquals(dstore.put(stream2, u1, binary), 0);// second entry returns hashcode of previous doc in table i.e. NOT ZERO
        assertArrayEquals(dstore.get(u1).getDocumentBinaryData(), ste.getBytes());//checks binary array returns values for the string: "Steve"
        assertNull(dstore.get(u1).getDocumentTxt());

        stream2 = new ByteArrayInputStream(ste.getBytes());
        assertEquals(dstore.put(stream2, u2, binary), 0);

        assertThrows(IllegalArgumentException.class, () -> {
            dstore.put(null, null, DocumentStore.DocumentFormat.TXT);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            dstore.put(null, URI.create("Test"), null);
        });

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
        assertThrows(IllegalArgumentException.class, () -> {
            dstore.delete(null);
        });
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
        dstore.undo(u2);
//        assertDoesNotThrow(() -> {
//0
//        });
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

        l = dstore.search("li");
        ArrayList<URI> t = new ArrayList<>();
        for (Document d : l) {
            System.out.println("li: " + d.getKey());
            t.add(d.getKey());
        }
        assertEquals(Arrays.asList(URI.create("text4"), URI.create("text3"), URI.create("text2")),t);

        l = dstore.search("sit");
        t = new ArrayList<>();
        for (Document d : l) {
            t.add(d.getKey());
            System.out.println("sit:" + d.getKey());
        }
        assertEquals(Arrays.asList(URI.create("text3"), URI.create("text1"), URI.create("text4"), URI.create("text2")),t);

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
        assertEquals(Arrays.asList(URI.create("text3"), URI.create("text4"), URI.create("text2"), URI.create("text1")),t);

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
        assertEquals(t, Arrays.asList(URI.create("text1"), URI.create("text2")));
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

    @Test
    void putOverflowToDisk() throws IOException {
        Document t1 = new DocumentImpl(u3,text1,null);
        Document t2 = new DocumentImpl(u4,text2,null);
        Document t3 = new DocumentImpl(u5,text3,null);
        Document t4 = new DocumentImpl(u6,text4,null);
        int bytesUsed = t1.getDocumentTxt().getBytes().length + t2.getDocumentTxt().getBytes().length + t3.getDocumentTxt().getBytes().length + t4.getDocumentTxt().getBytes().length;
        System.out.println("bytes used: " + bytesUsed);
        dstore = new DocumentStoreImpl(new File("C:\\Users\\jason\\Desktop\\test2"));
        dstore.put(stream3, u3, text);
        dstore.put(stream4, u4, text);
        dstore.put(stream5, u5, text);
        dstore.put(stream6, u6, text);
        //first test to user.dir , starting with 4 docs in memory
        dstore.setMaxDocumentCount(3);//should move text1 doc to disk,  3 docs in memory
        assertEquals(t1,dstore.get(u3));//should move text2 into disk and take text 1 into memory,  still 3 docs in memory
        dstore.setMaxDocumentCount(2);//should have text2 and text3 on the disk, text1 & text 4 in memory
        dstore.setMaxDocumentBytes(0);//should move text1 and text4 into memory, nothing in memory
        dstore.setMaxDocumentBytes(bytesUsed);
        dstore.setMaxDocumentCount(4);
        dstore.search("non");
        dstore.searchByPrefix("com"); // all docs should be removed from disk and back into memory
        dstore.put(new ByteArrayInputStream(generateRandomByteArray(2565)),URI.create("Go_Straight_to_folder"),binary);
        dstore.undo();
    }
}