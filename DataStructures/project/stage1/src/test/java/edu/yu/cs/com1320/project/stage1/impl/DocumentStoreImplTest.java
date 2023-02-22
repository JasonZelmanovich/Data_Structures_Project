package edu.yu.cs.com1320.project.stage1.impl;

 import edu.yu.cs.com1320.project.stage1.DocumentStore;
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
     URI b, e;
     String a, d;
     DocumentStore.DocumentFormat c, f;
     InputStream s, g;

     @BeforeEach
     void setUp() {
         a = "Jason";
         b = URI.create(a);
         g = new ByteArrayInputStream(a.getBytes());
         c = DocumentStore.DocumentFormat.TXT;

         d = "Steve";
         s = new ByteArrayInputStream(d.getBytes());
         e = URI.create(d);
         f = DocumentStore.DocumentFormat.BINARY;

         dstore = new DocumentStoreImpl();
     }

     @AfterEach
     void tearDown() {
         dstore = null;
     }

     @Test
     void put() {
         //adding with nothing prior
         try {
             assertEquals(dstore.put(g, b, c), 0);
         } catch (IOException e) {
             e.printStackTrace();
         }

         try {
             assertEquals(dstore.put(s, e, f), 0);
         } catch (IOException e) {
             e.printStackTrace();
         }

         //adding with a doc prior
         int code = dstore.get(b).hashCode();
         String t = "replaced";
         InputStream str = new ByteArrayInputStream(t.getBytes());
         try {
             assertEquals(dstore.put(str, b, c), code);
         } catch (IOException e) {
             e.printStackTrace();
         }

         code = dstore.get(e).hashCode();
         str = new ByteArrayInputStream(t.getBytes());
         try {
             assertEquals(dstore.put(str, e, f), code);
         } catch (IOException e) {
             e.printStackTrace();
         }

     }

     @Test
     void get() {

     }

     @Test
     void delete() {
     }
 }