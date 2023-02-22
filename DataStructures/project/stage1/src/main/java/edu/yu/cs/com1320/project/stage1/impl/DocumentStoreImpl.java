package edu.yu.cs.com1320.project.stage1.impl;

 import edu.yu.cs.com1320.project.impl.HashTableImpl;
 import edu.yu.cs.com1320.project.stage1.Document;
 import edu.yu.cs.com1320.project.stage1.DocumentStore;

 import java.io.IOException;
 import java.io.InputStream;
 import java.net.URI;

 public class DocumentStoreImpl implements DocumentStore {
     HashTableImpl<URI, DocumentImpl> hashTable;

     public DocumentStoreImpl(){
         hashTable = new HashTableImpl<>();
     }

     /**
      * @param input  the document being put
      * @param uri    unique identifier for the document
      * @param format indicates which type of document format is being passed
      * @return if there is no previous doc at the given URI, return 0. If there is a previous doc, return the hashCode
         * of the previous doc. If InputStream is null, this is a delete, and thus return either the hashCode of the deleted doc
         * or 0 if there is no doc to delete.
      * @throws IOException              if there is an issue reading input
      * @throws IllegalArgumentException if uri or format are null
      */
     @Override
     public int put(InputStream input, URI uri, DocumentFormat format) throws IOException {
         if(uri == null || format == null){
             throw new IllegalArgumentException();
         }
         if(input == null){
             DocumentImpl temp = hashTable.put(uri,null);
             return temp == null ? 0 : temp.hashCode();
         }
         byte[] read = input.readAllBytes();
         switch (format) {
             case TXT -> {
                 String s = new String(read);
                 DocumentImpl doc1 = new DocumentImpl(uri, s);
                 DocumentImpl old1 = hashTable.put(uri, doc1);
                 return old1 == null ? 0 : old1.hashCode();
             }
             case BINARY -> {
                 DocumentImpl doc2 = new DocumentImpl(uri, read);
                 DocumentImpl old2 = hashTable.put(uri, doc2);
                 return old2 == null ? 0 : old2.hashCode();
             }
         }
         return 0;
     }

     /**
      * @param uri the unique identifier of the document to get
      * @return the given document
      */
     @Override
     public Document get(URI uri){
         return hashTable.get(uri);
     }

     /**
      * @param uri the unique identifier of the document to delete
      * @return true if the document is deleted, false if no document exists with that URI
      */
     @Override
     public boolean delete(URI uri) {
         return hashTable.put(uri, null) != null;
     }
 }
