package edu.yu.cs.com1320.project.stage3.impl;

import edu.yu.cs.com1320.project.CommandSet;
import edu.yu.cs.com1320.project.GenericCommand;
import edu.yu.cs.com1320.project.Undoable;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.impl.TooSimpleTrie;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.stage3.Document;
import edu.yu.cs.com1320.project.stage3.DocumentStore;

import javax.print.Doc;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;
import java.util.function.Function;

public class DocumentStoreImpl implements DocumentStore {
    private StackImpl<Undoable> cmdStack;
    private HashTableImpl<URI, DocumentImpl> hashTable;
    private TrieImpl<Document> trie;

    public DocumentStoreImpl() {
        hashTable = new HashTableImpl<>();
        cmdStack = new StackImpl<>();
        trie = new TrieImpl<>();
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
        if (uri == null || format == null) {
            throw new IllegalArgumentException();
        }
        if (input == null) {
            DocumentImpl temp = hashTable.put(uri, null);
            if (temp != null) {
                Function undoDeleteLambda = (u) -> hashTable.put(u, temp) == null;
                cmdStack.push(new GenericCommand<URI>(uri, undoDeleteLambda));
            }
            return temp == null ? 0 : temp.hashCode();
        }
        byte[] read = input.readAllBytes();
        return storeDocument(format, read, uri);
    }

    /**
     * @param f      - the requested format of the document for the input to be stored in
     * @param bArray - the byte array of data being stored
     * @param uri    - the proper uri associated with the given document
     * @return the old hashcode if there was one, if no previous value return 0
     */
//    private int storeProperFormat(DocumentFormat f, byte[] bArray, URI uri) {
//        switch (f) {
//            case TXT -> {
//                String s = new String(bArray);
//                DocumentImpl doc1 = new DocumentImpl(uri, s);
//                DocumentImpl old1 = hashTable.put(uri, doc1);
//                if (old1 != null) {
//                    Function undoReplaceLambda = (u) -> hashTable.put(u, old1) == doc1;
//                    cmdStack.push(new GenericCommand<URI>(uri, undoReplaceLambda));
//                } else {
//                    Function undoNewLambda = (u) -> hashTable.put(u, null) == doc1; //essentially deleting the added document
//                    cmdStack.push(new GenericCommand<URI>(uri, undoNewLambda));
//                }
//                return old1 == null ? 0 : old1.hashCode();
//            }
//            case BINARY -> {
//                DocumentImpl doc2 = new DocumentImpl(uri, bArray);
//                DocumentImpl old2 = hashTable.put(uri, doc2);
//                Function undoReplaceLambda;
//                if (old2 != null) {
//                    undoReplaceLambda = (u) -> hashTable.put(u, old2) == doc2;
//                } else {
//                    undoReplaceLambda = (u) -> hashTable.put(u, null) == doc2;
//                }
//                cmdStack.push(new GenericCommand<URI>(uri, undoReplaceLambda));
//                return old2 == null ? 0 : old2.hashCode();
//            }
//        }
//        return 0;
//    }

    private int storeDocument(DocumentFormat f, byte[] bArray, URI uri){
        DocumentImpl doc;
        DocumentImpl old;
        if(f.equals(DocumentFormat.TXT)){
            String s = new String(bArray);
            doc = new DocumentImpl(uri, s);
            for(String word : doc.getWords()){
                trie.put(word,doc);
            }
        }else if(f.equals((DocumentFormat.BINARY))){
            doc = new DocumentImpl(uri, bArray);
        }else{
            doc = null;
            return 0;
        }
        old = hashTable.put(uri, doc);
        Function undoReplaceLambda;
        if (old != null) {
            for(String word : old.getWords()){
                Object obj = trie.delete(word,old);
                assert obj != null : "Old doc word should have been in the tree for deletion with specified document";
            }
            undoReplaceLambda = (u) -> {
                for(String word : doc.getWords()){
                    trie.delete(word,doc);
                }
                for(String word : old.getWords()){
                    trie.put(word,old);
                }
                return hashTable.put(u, old) == doc;
            };
        } else {
            undoReplaceLambda = (u) -> {
                for(String word : doc.getWords()){
                    trie.delete(word,doc);
                }
                return hashTable.put(u, null) == doc;
            };
        }
        cmdStack.push(new GenericCommand<URI>(uri, undoReplaceLambda));
        return old == null ? 0 : old.hashCode();
    }

    /**
     * @param uri the unique identifier of the document to get
     * @return the given document
     */
    @Override
    public Document get(URI uri) {
        return hashTable.get(uri);
    }

    /**
     * @param uri the unique identifier of the document to delete
     * @return true if the document is deleted, false if no document exists with that URI
     */
    @Override
    public boolean delete(URI uri) {
        if (!hashTable.containsKey(uri)) {
            return false;
        } else {
            Document temp = hashTable.put(uri, null);
            Function undoDeleteLambda = (u) -> hashTable.put(u, temp) == null;
            cmdStack.push(new GenericCommand<URI>(uri, undoDeleteLambda));
            return true;
        }
    }

    /**
     * undo the last put or delete command
     *
     * @throws IllegalStateException if there are no actions to be undone, i.e. the command stack is empty
     */
    @Override
    public void undo() throws IllegalStateException {
        if (cmdStack.size() == 0) {
            throw new IllegalStateException("No Actions to be undone");
        }
        cmdStack.pop().undo();
    }

    /**
     * undo the last put or delete that was done with the given URI as its key
     *
     * @param uri
     * @throws IllegalStateException if there are no actions on the command stack for the given URI
     */
    @Override
    public void undo(URI uri) throws IllegalStateException {
        boolean found = false;
        if (cmdStack.size() == 0) throw new IllegalStateException("No Actions to be undone");
        StackImpl<Undoable> tempStack = new StackImpl<>();
        while (cmdStack.peek() != null) {
            if (cmdStack.peek() instanceof GenericCommand<?>) { // check instance adn get target
                GenericCommand<URI> temp = (GenericCommand<URI>) cmdStack.peek();
                if(temp.getTarget() == uri){
                    cmdStack.pop().undo();
                    found = true;
                    break;
                }else{tempStack.push(cmdStack.pop());}
            }else{
                CommandSet<URI> temp = (CommandSet<URI>) cmdStack.peek();
                if(temp.containsTarget(uri)){
                    temp.undo(uri);
                    if(temp.size() == 0){
                        cmdStack.pop();
                    }
                    found = true;
                    break;
                }else{tempStack.push(cmdStack.pop());}
            }
        }
        while (tempStack.peek() != null) {
            cmdStack.push(tempStack.pop());
        }
        if (!found) throw new IllegalStateException("No Actions to be undone");
    }

    /**
     * Retrieve all documents whose text contains the given keyword.
     * Documents are returned in sorted, descending order, sorted by the number of times the keyword appears in the document.
     * Search is CASE SENSITIVE.
     *
     * @param keyword
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    @Override
    public List<Document> search(String keyword) {
        Comparator<Document> docComparatorDescending = new Comparator<Document>() {
            @Override
            public int compare(Document o1, Document o2) {
                return o2.wordCount(keyword) - o1.wordCount(keyword);
            }
        };
        return trie.getAllSorted(keyword,docComparatorDescending);
    }

    /**
     * Retrieve all documents that contain text which starts with the given prefix
     * Documents are returned in sorted, descending order, sorted by the number of times the prefix appears in the document.
     * Search is CASE SENSITIVE.
     *
     * @param keywordPrefix
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    @Override
    public List<Document> searchByPrefix(String keywordPrefix) {
        Comparator<Document> documentComparator = new Comparator<Document>() {
            @Override
            public int compare(Document o1, Document o2) {
                HashSet<String> allWords1 = new HashSet<>();
                HashSet<String> allWords2 = new HashSet<>();
                for(String w : o1.getWords()){
                    if(w.startsWith(keywordPrefix)) allWords1.add(w);
                }
                for(String w : o2.getWords()){
                    if(w.startsWith(keywordPrefix)) allWords2.add(w);
                }
                int doc1PrefCount = 0;
                int doc2PrefCount = 0;
                for(String w : allWords1){
                    doc1PrefCount += o1.wordCount(w);
                }
                for(String w : allWords2){
                    doc2PrefCount += o2.wordCount(w);
                }
                return doc2PrefCount - doc1PrefCount;
            }
        };
        return trie.getAllWithPrefixSorted(keywordPrefix,documentComparator);
    }

    /**
     * Completely remove any trace of any document which contains the given keyword
     * Search is CASE SENSITIVE.
     *
     * @param keyword
     * @return a Set of URIs of the documents that were deleted.
     */
    @Override
    public Set<URI> deleteAll(String keyword) {
        Set<Document> docsToBeRemoved = trie.deleteAll(keyword);
        Set<URI> docKeys = new HashSet<>();
        if(docsToBeRemoved.size() == 0){
            return Collections.emptySet();
        }
        for(Document doc : docsToBeRemoved){
            for(String w : doc.getWords()){
                trie.delete(w,doc);
            }
            docKeys.add(doc.getKey());
            this.hashTable.put(doc.getKey(),null);
        }
        //undo logic for CommandSet         create a set of genericCommands to undo each deletion of the document then add the set to CommandSet
        CommandSet<URI> cmdSet = new CommandSet<>();
        for(Document doc : docsToBeRemoved){
            for(String w : doc.getWords()){
                Function undoDeletion = (u) -> {
                  trie.put(w,doc);
                  return this.hashTable.put(u,doc) == null;
                };
                cmdSet.addCommand(new GenericCommand<URI>(doc.getKey(),undoDeletion));
            }
        }//push the CommandSet on to the stack
        cmdStack.push(cmdSet);
        return docKeys;
    }

    /**
     * Completely remove any trace of any document which contains a word that has the given prefix
     * Search is CASE SENSITIVE.
     *
     * @param keywordPrefix
     * @return a Set of URIs of the documents that were deleted.
     */
    @Override
    public Set<URI> deleteAllWithPrefix(String keywordPrefix) {
        return null;
    }
}
