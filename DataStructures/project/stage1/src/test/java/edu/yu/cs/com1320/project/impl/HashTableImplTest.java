package edu.yu.cs.com1320.project.impl;

 import org.junit.jupiter.api.*;

 import java.sql.SQLOutput;

 import static org.junit.jupiter.api.Assertions.*;

 class HashTableImplTest {
     HashTableImpl<Integer,String> c =  new HashTableImpl<>();
     HashTableImpl<String,String> f = new HashTableImpl<>();
     @Test
     void putIntString() {
         String s = c.put(19,"Jason");
         assertEquals(s,null);
         s = c.put(19,"changed Value");
         assertEquals(s,"Jason");

         s = c.put(36,"YU");
         assertEquals(s,null);

         s = c.put(49,"Target");
         assertEquals(s,null);

     }

     @Test
     void putStringString() {
         String s = f.put("Shalom","Hello");
         assertNull(s);
         s = f.put("Shalom","UpdatedHello");
         assertEquals(s,"Hello");
         s = f.get("Shalom");
         assertEquals(s,"UpdatedHello");
     }

     @Test
     void get() {
         c.put(19,"Jason");
         c.put(19,"changed Value");
         c.put(36,"YU");
         c.put(49,"Target");
         assertEquals(c.get(19),"changed Value");
         assertEquals(c.get(36),"YU");
         assertEquals(c.get(49),"Target");
         c.put(49,null);
         assertEquals(c.get(49),null);
     }
 }