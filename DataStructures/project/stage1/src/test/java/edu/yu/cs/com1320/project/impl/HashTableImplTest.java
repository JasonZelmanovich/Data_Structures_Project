package edu.yu.cs.com1320.project.impl;

 import org.junit.jupiter.api.*;

 import java.sql.SQLOutput;

 import static org.junit.jupiter.api.Assertions.*;

 class HashTableImplTest {
     HashTableImpl<Integer,String> c =  new HashTableImpl<>();
     @Test
     void put() {
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

     @Test
     void showcase(){
         c.put(19,"Jason");
         c.put(19,"changed Value");
         c.put(36,"YU");
         c.put(49,"Target");
         c.put(49,null);
         System.out.println(c.get(49));
     }
 }