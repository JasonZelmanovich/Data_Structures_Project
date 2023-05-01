package edu.yu.cs.com1320.project.stage4.impl;

import edu.yu.cs.com1320.project.CommandSet;
import edu.yu.cs.com1320.project.GenericCommand;
import edu.yu.cs.com1320.project.Undoable;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.impl.MinHeapImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.stage4.Document;
import edu.yu.cs.com1320.project.stage4.DocumentStore;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;
import java.util.function.Function;

public class DocumentStoreImpl implements DocumentStore {
    private StackImpl<Undoable> cmdStack;
    private HashTableImpl<URI, DocumentImpl> hashTable;
    private TrieImpl<Document> trie;
    private MinHeapImpl<Document> minHeap;
    private int doc_count_limit;
    private int doc_bytes_limit;
    private int num_current_docs_used;
    private int num_total_bytes_used;

    public DocumentStoreImpl() {
        hashTable = new HashTableImpl<>();
        cmdStack = new StackImpl<>();
        trie = new TrieImpl<>();
        minHeap = new MinHeapImpl<>();
        this.num_current_docs_used = 0;
        this.num_total_bytes_used = 0;
        this.doc_bytes_limit = -1;
        this.doc_count_limit = -1;
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
            if (temp != null) {// if there was actually something to be deleted
                this.num_current_docs_used--;
                int mem;
                if (temp.getDocumentTxt() != null) {
                    mem = temp.getDocumentTxt().getBytes().length;
                } else {
                    mem = temp.getDocumentBinaryData().length;
                }
                this.num_total_bytes_used -= mem;
                temp.setLastUseTime(Long.MIN_VALUE);
                minHeap.reHeapify(temp);
                minHeap.remove();
                for (String s : temp.getWords()) {
                    trie.delete(s, temp);
                }
                Function undoDeleteLambda = (u) -> {
                    manageMemoryOnPut(temp, format);//manage memory and then put the undone doc into the docStore
                    //put the deleted doc back into the doc store
                    this.num_current_docs_used++;
                    this.num_total_bytes_used += mem;
                    for (String s : temp.getWords()) {
                        trie.put(s, temp);
                    }
                    temp.setLastUseTime(System.nanoTime());
                    minHeap.insert(temp);
                    return hashTable.put(u, temp) == null;
                };
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

    private int storeDocument(DocumentFormat f, byte[] bArray, URI uri) {
        DocumentImpl doc;
        DocumentImpl old;
        int memory;
        if (f.equals(DocumentFormat.TXT)) {
            String s = new String(bArray);
            doc = new DocumentImpl(uri, s);
            manageMemoryOnPut(doc, f);
            for (String word : doc.getWords()) {
                trie.put(word, doc);
            }
            memory = doc.getDocumentTxt().getBytes().length;
        } else if (f.equals((DocumentFormat.BINARY))) {
            doc = new DocumentImpl(uri, bArray);
            manageMemoryOnPut(doc, f);
            memory = doc.getDocumentBinaryData().length;
        } else {
            doc = null;
            return 0;
        }
        this.num_total_bytes_used += memory;
        //Already inserted into trie &
        old = hashTable.put(uri, doc);
        doc.setLastUseTime(System.nanoTime());
        this.minHeap.insert(doc);
        //All insertions finished
        storeDocUndoLogic(old, doc, uri, memory);//Handle deletion of OLD if necessary, create the undo lambda, check for old doc existence
        this.num_current_docs_used++; // update docStore memory status in regard to number of documents
        int tbr = old == null ? 0 : old.hashCode();
        manageMemory();
        return tbr;
    }

    private void storeDocUndoLogic(Document old, Document doc, URI uri, int newMem) {
        Function undoReplaceLambda;
        DocumentFormat oldformat;
        int new_doc_mem = newMem;
        int oldMem;
        if (old != null) { //If an old document was being replaced
            old.setLastUseTime(Long.MIN_VALUE);
            this.minHeap.reHeapify(old);
            minHeap.remove();
            //removed Old doc from the heap
            for (String word : old.getWords()) {//delete all trace of old doc from the TRIE
                Object obj = trie.delete(word, old);
                assert obj != null : "Old doc word should have been in the tree for deletion with specified document";
            }
            this.num_current_docs_used--; //update docStore memory status i.e. remove old doc from the # of docs
            if (old.getDocumentTxt() != null) { //check the format of the old doc and set the amount of memory it needs
                oldformat = DocumentFormat.TXT;
                oldMem = old.getDocumentTxt().getBytes().length;
            } else {
                oldformat = DocumentFormat.BINARY;
                oldMem = old.getDocumentBinaryData().length;
            }
            this.num_total_bytes_used -= oldMem; //update docStore memory status i.e. remove old doc from the memory of docStore
            undoReplaceLambda = (u) -> { //undo if something was deleted as a result of docs insertion
                for (String word : doc.getWords()) {//remove new doc from the trie
                    trie.delete(word, doc);
                }
                doc.setLastUseTime(Long.MIN_VALUE);
                minHeap.reHeapify(doc);
                minHeap.remove();//remove new doc from the heap
                this.num_current_docs_used--;//update memory status for # of docs
                this.num_total_bytes_used -= new_doc_mem; // remove the new doc memory size from docStores memory status
                manageMemoryOnPut(old, oldformat);
                for (String word : old.getWords()) { //put old doc into the trie
                    trie.put(word, old);
                }
                old.setLastUseTime(System.nanoTime());
                minHeap.insert(old);//put old doc into the heap with new time
                //update docStore memory status
                this.num_current_docs_used++;
                this.num_total_bytes_used += oldMem;
                return hashTable.put(u, old) == doc; //replace hashtable with what was originally there
            };
        } else { // undo if nothing was deleted as result of docs insertion ( just remove doc )
            undoReplaceLambda = (u) -> {
                for (String word : doc.getWords()) {
                    trie.delete(word, doc);
                }
                doc.setLastUseTime(Long.MIN_VALUE);
                minHeap.reHeapify(doc);
                minHeap.remove();
                this.num_current_docs_used--;
                this.num_current_docs_used -= new_doc_mem;
                return hashTable.put(u, null) == doc;
            };
        }
        cmdStack.push(new GenericCommand<URI>(uri, undoReplaceLambda));
    }

    /**
     * Check if the newDoc will exceed memory limits
     * if not: do nothing and return
     * if yes: Completely remove any trace of the least used doc from the doc store and then return
     * once cleared out
     *
     * @param newDoc
     */
    private void manageMemoryOnPut(Document newDoc, DocumentFormat format) {
        if (this.doc_bytes_limit != -1) {
            int memoryNeeded = 0;
            if (format.equals(DocumentFormat.TXT)) {
                memoryNeeded += newDoc.getDocumentTxt().getBytes().length;
            } else {
                memoryNeeded += newDoc.getDocumentBinaryData().length;
            }
            if (memoryNeeded > this.doc_bytes_limit) {
                throw new IllegalArgumentException();
            }
            while (this.doc_bytes_limit < this.num_total_bytes_used + memoryNeeded && this.num_current_docs_used != 0) {//delete least recently used docs until we are under the limit
                removeAllTraceOfLeastUsedDoc();
            }
        }
        if (this.doc_count_limit != -1) {
            if (this.doc_count_limit < this.num_current_docs_used + 1 && this.num_current_docs_used != 0) {
                removeAllTraceOfLeastUsedDoc();
            }
        }
    }

    private void manageMemory() {
        if (this.doc_bytes_limit != -1) {
            while (this.doc_bytes_limit < this.num_total_bytes_used) {
                removeAllTraceOfLeastUsedDoc();
            }
        }
        if (this.doc_count_limit != -1) {
            while (this.doc_count_limit < this.num_current_docs_used) {
                removeAllTraceOfLeastUsedDoc();
            }
        }
    }

    private void removeAllTraceOfLeastUsedDoc() {
        Document leastUsedDoc = minHeap.remove();
        this.num_current_docs_used--;
        URI uri = leastUsedDoc.getKey();
        hashTable.put(leastUsedDoc.getKey(), null);//delete from the hashtable
        for (String s : leastUsedDoc.getWords()) {
            trie.delete(s, leastUsedDoc);//delete from the trie if it was of txt format
        }
        //find the doc in the command stack and remove it
        StackImpl<Undoable> tempStack = new StackImpl<>();
        while (cmdStack.peek() != null) {
            if (cmdStack.peek() instanceof GenericCommand) { // check instance and get target
                GenericCommand<URI> temp = (GenericCommand<URI>) cmdStack.peek();
                if (temp.getTarget().equals(uri)) {
                    cmdStack.pop();
                } else {
                    tempStack.push(cmdStack.pop());
                }
            } else {
                CommandSet<URI> temp = (CommandSet<URI>) cmdStack.peek();
                if (temp.containsTarget(uri)) { // if the command set contains the doc that must be removed
                    Iterator<GenericCommand<URI>> I = temp.iterator();
                    while (I.hasNext()) {
                        GenericCommand<URI> genComm = I.next();
                        if (genComm.getTarget().equals(uri)) {
                            I.remove();
                        }
                    }
                } else {
                    tempStack.push(cmdStack.pop());
                }
            }
        }
        while (tempStack.peek() != null) {
            cmdStack.push(tempStack.pop());
        }
        if (leastUsedDoc.getDocumentTxt() != null) {
            this.num_total_bytes_used -= leastUsedDoc.getDocumentTxt().getBytes().length;
        } else {
            this.num_total_bytes_used -= leastUsedDoc.getDocumentBinaryData().length;
        }
    }

    /**
     * @param uri the unique identifier of the document to get
     * @return the given document
     */
    @Override
    public Document get(URI uri) {
        Document gotDoc = hashTable.get(uri);
        if (gotDoc != null) {
            gotDoc.setLastUseTime(System.nanoTime());
            minHeap.reHeapify(gotDoc);
        }
        return gotDoc;
    }

    /**
     * @param uri the unique identifier of the document to delete
     * @return true if the document is deleted, false if no document exists with that URI
     */
    @Override
    public boolean delete(URI uri) {
        if (uri == null) {
            throw new IllegalArgumentException("uri was NULL");
        }
        if (!hashTable.containsKey(uri)) {
            return false;
        } else {
            //delete and create undo lambda for doc to be deleted from the hashtable, trie, and heap
            Document temp = hashTable.put(uri, null);
            for (String w : temp.getWords()) {
                trie.delete(w, temp);
            }
            int mem;
            DocumentFormat f;
            if(temp.getDocumentTxt() != null){
                f = DocumentFormat.TXT;
                mem = temp.getDocumentTxt().getBytes().length;
            }else{
                f = DocumentFormat.BINARY;
                mem = temp.getDocumentBinaryData().length;
            }
            temp.setLastUseTime(Long.MIN_VALUE);
            minHeap.reHeapify(temp);
            minHeap.remove();
            this.num_current_docs_used--;
            this.num_total_bytes_used -= mem;
            Function undoDeleteLambda = (u) -> {
                manageMemoryOnPut(temp,f);
                for (String w : temp.getWords()) {
                    trie.put(w, temp);
                }
                temp.setLastUseTime(System.nanoTime());
                minHeap.insert(temp);
                this.num_current_docs_used++;
                this.num_total_bytes_used += mem;
                return hashTable.put(u, temp) == null;
            };
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
        manageMemory();
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
            if (cmdStack.peek() instanceof GenericCommand) { // check instance and get target
                GenericCommand<URI> temp = (GenericCommand<URI>) cmdStack.peek();
                if (temp.getTarget().equals(uri)) {
                    boolean b = cmdStack.pop().undo();
                    assert b == true;
                    found = true;
                    break;
                } else {
                    tempStack.push(cmdStack.pop());
                }
            } else {
                CommandSet<URI> temp = (CommandSet<URI>) cmdStack.peek();
                if (temp.containsTarget(uri)) {
                    boolean b = temp.undo(uri);
                    manageMemory();
                    assert b == true;
                    if (temp.size() == 0) {
                        cmdStack.pop();
                    }
                    found = true;
                    break;
                } else {
                    tempStack.push(cmdStack.pop());
                }
            }
        }
        while (tempStack.peek() != null) {
            cmdStack.push(tempStack.pop());
        }
        manageMemory();
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
        List<Document> temp_list = trie.getAllSorted(keyword, docComparatorDescending);
        long time = System.nanoTime();
        for (Document d : temp_list) {
            d.setLastUseTime(time);
            minHeap.reHeapify(d);
        }
        return temp_list;
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
                for (String w : o1.getWords()) {
                    if (w.startsWith(keywordPrefix)) allWords1.add(w);
                }
                for (String w : o2.getWords()) {
                    if (w.startsWith(keywordPrefix)) allWords2.add(w);
                }
                int doc1PrefCount = 0;
                int doc2PrefCount = 0;
                for (String w : allWords1) {
                    doc1PrefCount += o1.wordCount(w);
                }
                for (String w : allWords2) {
                    doc2PrefCount += o2.wordCount(w);
                }
                return doc2PrefCount - doc1PrefCount;
            }
        };
        List<Document> listOfDocs = trie.getAllWithPrefixSorted(keywordPrefix, documentComparator);
        long time = System.nanoTime();
        for (Document d : listOfDocs) {
            d.setLastUseTime(time);
            minHeap.reHeapify(d);
        }
        return listOfDocs;
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
        if (docsToBeRemoved.size() == 0) {
            return Collections.emptySet();
        }
        for (Document doc : docsToBeRemoved) {
            int mem;
            DocumentFormat f;
            if(doc.getDocumentTxt() != null){//get doc format
                f = DocumentFormat.TXT;
                mem = doc.getDocumentTxt().getBytes().length;
            }else{
                f = DocumentFormat.BINARY;
                mem = doc.getDocumentBinaryData().length;
            }
            for (String w : doc.getWords()) {//remove the doc to be deleted from the trie
                trie.delete(w, doc);
            }
            docKeys.add(doc.getKey());
            this.hashTable.put(doc.getKey(), null);//remove from the hashtable
            doc.setLastUseTime(Long.MIN_VALUE);
            minHeap.reHeapify(doc);
            minHeap.remove();//remove from the Min Heap
            this.num_current_docs_used--;
            this.num_total_bytes_used -= mem;
        }
        //undo logic for CommandSet create a set of genericCommands to undo each deletion of the document then add the set to CommandSet
        CommandSet<URI> cmdSet = new CommandSet<>();
        long time = System.nanoTime();//all the docs going back should have the same nano time per piazza
        for (Document doc : docsToBeRemoved) {
            int mem;
            DocumentFormat f;
            if(doc.getDocumentTxt() != null){//get doc format
                f = DocumentFormat.TXT;
                mem = doc.getDocumentTxt().getBytes().length;
            }else{
                f = DocumentFormat.BINARY;
                mem = doc.getDocumentBinaryData().length;
            }
            Function undoDeletion = (u) -> {
                manageMemoryOnPut(doc, f);
                for (String w : doc.getWords()) {
                    trie.put(w, doc);
                }
                doc.setLastUseTime(time);
                minHeap.insert(doc);
                this.num_current_docs_used++;
                this.num_total_bytes_used += mem;
                return this.hashTable.put(u, doc) == null;
            };
            cmdSet.addCommand(new GenericCommand<>(doc.getKey(), undoDeletion));
        }
        //push the CommandSet on to the stack
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
        Set<Document> docsToBeRemoved = trie.deleteAllWithPrefix(keywordPrefix);
        Set<URI> docKeys = new HashSet<>();
        if (docsToBeRemoved.size() == 0) {
            return Collections.emptySet();
        }
        for (Document doc : docsToBeRemoved) {
            int mem;
            DocumentFormat f;
            if(doc.getDocumentTxt() != null){//get doc format
                f = DocumentFormat.TXT;
                mem = doc.getDocumentTxt().getBytes().length;
            }else{
                f = DocumentFormat.BINARY;
                mem = doc.getDocumentBinaryData().length;
            }
            for (String w : doc.getWords()) {
                trie.delete(w, doc);
            }
            docKeys.add(doc.getKey());
            doc.setLastUseTime(Long.MIN_VALUE);
            minHeap.reHeapify(doc);
            minHeap.remove();
            this.num_current_docs_used--;
            this.num_total_bytes_used -= mem;
            this.hashTable.put(doc.getKey(), null);
        }
        CommandSet<URI> cmdSet = new CommandSet<>();
        long time = System.nanoTime();
        for (Document doc : docsToBeRemoved) {
            int mem;
            DocumentFormat f;
            if(doc.getDocumentTxt() != null){//get doc format
                f = DocumentFormat.TXT;
                mem = doc.getDocumentTxt().getBytes().length;
            }else{
                f = DocumentFormat.BINARY;
                mem = doc.getDocumentBinaryData().length;
            }
            Function undoDeletion = (u) -> {
                manageMemoryOnPut(doc,f);
                for (String w : doc.getWords()) {
                    trie.put(w, doc);
                }
                doc.setLastUseTime(time);
                minHeap.insert(doc);
                this.num_current_docs_used++;
                this.num_total_bytes_used += mem;
                return this.hashTable.put(u, doc) == null;
            };
            cmdSet.addCommand(new GenericCommand<URI>(doc.getKey(), undoDeletion));
        }//push the CommandSet on to the stack
        cmdStack.push(cmdSet);
        return docKeys;
    }

    /**
     * set maximum number of documents that may be stored
     *
     * @param limit
     */
    @Override
    public void setMaxDocumentCount(int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException();
        }
        this.doc_count_limit = limit;
        manageMemory();
    }

    /**
     * set maximum number of bytes of memory that may be used by all the documents in memory combined
     *
     * @param limit
     */
    @Override
    public void setMaxDocumentBytes(int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException();
        }
        this.doc_bytes_limit = limit;
        manageMemory();
    }
}
