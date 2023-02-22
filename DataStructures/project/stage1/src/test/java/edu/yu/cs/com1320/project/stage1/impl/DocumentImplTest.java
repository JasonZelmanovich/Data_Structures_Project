package edu.yu.cs.com1320.project.stage1.impl;

 import edu.yu.cs.com1320.project.stage1.Document;
 import org.junit.jupiter.api.BeforeEach;
 import org.junit.jupiter.api.Test;

 import java.net.URI;
 import java.net.URISyntaxException;

 import static org.junit.jupiter.api.Assertions.*;

 class DocumentImplTest {
     DocumentImpl doc;
     DocumentImpl doc2;

     byte[] b;
     @BeforeEach
     void setUp() {
         doc = new DocumentImpl(URI.create("Hello"),"Testing");
         String s = "Testing";
         b = s.getBytes();
         doc2 = new DocumentImpl(URI.create("beep"), b);
     }

     @Test
     void testEquals() throws URISyntaxException {
         assertEquals(doc.equals(doc2),false);
     }

     @Test
     void getDocumentTxt() {
         assertEquals(doc.getDocumentTxt(),"Testing");
         assertEquals(doc2.getDocumentTxt(),null);
     }

     @Test
     void getDocumentBinaryData() {
         assertEquals(doc.getDocumentBinaryData(),null);
         assertEquals(doc2.getDocumentBinaryData(), b);
     }

     @Test
     void getKey() {
         assertNotNull(doc.getKey());
         assertNotNull(doc2.getKey());
     }
 }