package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;
import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage5.impl.DocumentPersistenceManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

class BTreeImplTest {

    @Test
    void put() {
        BTreeImpl<URI,Document> btree = new BTreeImpl<>();
        URI j = URI.create("Jason");
        Document doc = new DocumentImpl(j,"Hello World",null);
        URI u = URI.create("binary");
        String s = "Bianry doc cool";
        Document doc2 = new DocumentImpl(u,s.getBytes());
        btree.put(j,doc);
        btree.put(u,doc2);
        assertEquals(doc,btree.get(j));
        assertEquals(doc2,btree.get(u));
    }

    @Test
    void moveToDisk1() throws Exception {
        PersistenceManager pm = new DocumentPersistenceManager(new File("C:\\Users\\jason\\Desktop\\www.google.com\\testing123\\"));
        BTreeImpl<URI,Document> btree = new BTreeImpl<>();
        URI j = URI.create("Jason");
        Document doc = new DocumentImpl(j,"Hello World",null);
        URI u = URI.create("binary");
        String s = "Bianry doc cool";
        Document doc2 = new DocumentImpl(u,s.getBytes());
        btree.put(j,doc);
        btree.put(u,doc2);
        assertEquals(doc,btree.get(j));
        assertEquals(doc2,btree.get(u));
        btree.setPersistenceManager(pm);
        btree.moveToDisk(j);
        assertEquals(doc,btree.get(j));
        btree.moveToDisk(u);
        assertEquals(doc2,btree.get(u));
    }

    @Test
    void moveToDisk2() throws Exception {
        PersistenceManager pm = new DocumentPersistenceManager(new File("C:\\Users\\jason\\Desktop\\www.google.com\\NewFileTest"));
        BTreeImpl<URI,Document> btree = new BTreeImpl<>();
        URI j = URI.create("Jason");
        Document doc = new DocumentImpl(j,"Hello World",null);
        URI u = URI.create("binary");
        String s = "Bianry doc cool";
        Document doc2 = new DocumentImpl(u,s.getBytes());
        URI web = URI.create("https://yu.instructure.com/teest123");
        Document test = new DocumentImpl(web,"Hello World",null);
        btree.put(j,doc);
        btree.put(u,doc2);
        btree.put(web,test);
        assertEquals(doc,btree.get(j));
        assertEquals(doc2,btree.get(u));
        assertEquals(test,btree.get(web));
        btree.setPersistenceManager(pm);
        btree.moveToDisk(j);
        assertEquals(doc,btree.get(j));
        btree.moveToDisk(u);
        assertEquals(doc2,btree.get(u));
        btree.moveToDisk(web);
        assertEquals(test,btree.get(web));
    }

    @AfterAll
    static void deleteAll(){

    }
}