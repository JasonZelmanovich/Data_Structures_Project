package edu.yu.cs.com1320.project.stage5.impl;

import edu.yu.cs.com1320.project.CommandSet;
import edu.yu.cs.com1320.project.GenericCommand;
import edu.yu.cs.com1320.project.Undoable;
import edu.yu.cs.com1320.project.impl.BTreeImpl;
import edu.yu.cs.com1320.project.impl.MinHeapImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.DocumentStore;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;

import javax.print.Doc;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;
import java.util.function.Function;

public class DocumentStoreImpl implements DocumentStore {
    private class Node implements Comparable<Node>{
        URI uri;
        private long nanoTime;
        public Node(URI u){
            this.uri = u;
            this.nanoTime = 0;
        }
        public URI getUri(){
            return this.uri;
        }
        /**
         * Compares this object with the specified object for order.  Returns a
         * negative integer, zero, or a positive integer as this object is less
         * than, equal to, or greater than the specified object.
         *
         * <p>The implementor must ensure {@link Integer#signum
         * signum}{@code (x.compareTo(y)) == -signum(y.compareTo(x))} for
         * all {@code x} and {@code y}.  (This implies that {@code
         * x.compareTo(y)} must throw an exception if and only if {@code
         * y.compareTo(x)} throws an exception.)
         *
         * <p>The implementor must also ensure that the relation is transitive:
         * {@code (x.compareTo(y) > 0 && y.compareTo(z) > 0)} implies
         * {@code x.compareTo(z) > 0}.
         *
         * <p>Finally, the implementor must ensure that {@code
         * x.compareTo(y)==0} implies that {@code signum(x.compareTo(z))
         * == signum(y.compareTo(z))}, for all {@code z}.
         *
         * @param o the object to be compared.
         * @return a negative integer, zero, or a positive integer as this object
         * is less than, equal to, or greater than the specified object.
         * @throws NullPointerException if the specified object is null
         * @throws ClassCastException   if the specified object's type prevents it
         *                              from being compared to this object.
         * @apiNote It is strongly recommended, but <i>not</i> strictly required that
         * {@code (x.compareTo(y)==0) == (x.equals(y))}.  Generally speaking, any
         * class that implements the {@code Comparable} interface and violates
         * this condition should clearly indicate this fact.  The recommended
         * language is "Note: this class has a natural ordering that is
         * inconsistent with equals."
         */
        @Override
        public int compareTo(Node o) {
            if (o == null) {
                throw new NullPointerException();
            }
            if (btree.get(this.uri).getLastUseTime() < btree.get(o.getUri()).getLastUseTime()) {
                return -1;
            } else if (btree.get(this.uri).getLastUseTime() > btree.get(o.getUri()).getLastUseTime()) {
                return 1;
            } else {
                return 0;
            }
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return Objects.equals(uri, node.getUri());
        }
        @Override
        public int hashCode() {
            return Objects.hash(uri);
        }
    }
    private HashMap<URI,Node> nodeHashMap;
    private HashSet<URI> uriOnDisk;
    private StackImpl<Undoable> cmdStack;
    private BTreeImpl<URI, DocumentImpl> btree;
    private TrieImpl<URI> trie;
    private MinHeapImpl<Node> minHeap;
    private PersistenceManager pm;
    private int doc_count_limit;
    private int doc_bytes_limit;
    private int num_current_docs_used;
    private int num_total_bytes_used;

    public DocumentStoreImpl() {
        uriOnDisk = new HashSet<>();
        nodeHashMap = new HashMap<>();
        btree = new BTreeImpl<>();
        cmdStack = new StackImpl<>();
        trie = new TrieImpl<>();
        minHeap = new MinHeapImpl<>();
        this.num_current_docs_used = 0;
        this.num_total_bytes_used = 0;
        this.doc_bytes_limit = -1;
        this.doc_count_limit = -1;
        pm = new DocumentPersistenceManager(null);
        btree.setPersistenceManager(pm);
    }

    public DocumentStoreImpl(File baseDir){
        uriOnDisk = new HashSet<>();
        nodeHashMap = new HashMap<>();
        btree = new BTreeImpl<>();
        cmdStack = new StackImpl<>();
        trie = new TrieImpl<>();
        minHeap = new MinHeapImpl<>();
        this.num_current_docs_used = 0;
        this.num_total_bytes_used = 0;
        this.doc_bytes_limit = -1;
        this.doc_count_limit = -1;
        pm = new DocumentPersistenceManager(baseDir);
        btree.setPersistenceManager(pm);
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
            DocumentImpl temp = btree.get(uri);
            if (temp != null) {// if there was actually something to be deleted
                this.num_current_docs_used--;
                int mem;
                DocumentFormat form;
                if (temp.getDocumentTxt() != null) {
                    mem = temp.getDocumentTxt().getBytes().length;
                    form = DocumentFormat.TXT;
                } else {
                    mem = temp.getDocumentBinaryData().length;
                    form = DocumentFormat.BINARY;
                }
                this.num_total_bytes_used -= mem;
                //Check if temp was moved to disk earlier
                if(uriOnDisk.contains(temp.getKey())){//if it was moved to disk add it back to the heap for proper removal from heap
                    addOldToHeap(temp);
                    uriOnDisk.remove(temp.getKey());
                }
                temp.setLastUseTime(Long.MIN_VALUE);
                minHeap.reHeapify(nodeHashMap.get(temp.getKey()));
                nodeHashMap.remove(temp.getKey());
                minHeap.remove();
                for (String s : temp.getWords()) {
                    trie.delete(s, temp.getKey());
                }
                this.btree.put(uri, null);
                Function undoDeleteLambda = (u) -> {
                    manageMemoryOnPut(temp, form);//manage memory and then put the undone doc into the docStore
                    //put the deleted doc back into the doc store
                    this.num_current_docs_used++;
                    this.num_total_bytes_used += mem;
                    for (String s : temp.getWords()) {
                        trie.put(s, temp.getKey());
                    }
                    Document t = btree.put((URI) u, temp);
                    temp.setLastUseTime(System.nanoTime());
                    Node n = new Node(temp.getKey());
                    nodeHashMap.put(temp.getKey(),n);
                    minHeap.insert(nodeHashMap.get(temp.getKey()));
                    return t == null;
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
        Document old;
        int memory;
        if (f.equals(DocumentFormat.TXT)) {
            String s = new String(bArray);
            doc = new DocumentImpl(uri, s, null);
            manageMemoryOnPut(doc, f);
            for (String word : doc.getWords()) {
                trie.put(word, doc.getKey());
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
        //Already inserted into trie, now btree
        old = btree.get(uri);
        if (btree.get(uri) == null) {
            Node n = new Node(uri);
            nodeHashMap.put(uri, n);
        }
        if (btree.get(uri) != null && uriOnDisk.contains(old.getKey())) {
            Node n = new Node(old.getKey());
            nodeHashMap.put(old.getKey(), n);
            uriOnDisk.remove(old.getKey());
            btree.get(uri).setLastUseTime(Long.MIN_VALUE);
            minHeap.insert(n);
            minHeap.reHeapify(nodeHashMap.get(old.getKey()));
            minHeap.remove();
        }else if (btree.get(uri) != null) {
            btree.get(uri).setLastUseTime(Long.MIN_VALUE);
            minHeap.reHeapify(nodeHashMap.get(uri));
            minHeap.remove();
        }
        btree.put(uri, doc);
        doc.setLastUseTime(System.nanoTime());
        this.minHeap.insert(nodeHashMap.get(uri));
        //All insertions finished
        storeDocUndoLogic(old, doc, uri, memory);//Handle deletion of OLD if necessary, create the undo lambda, check for old doc existence
        this.num_current_docs_used++; // update docStore memory status in regard to number of documents
        int tbr = old == null ? 0 : old.hashCode();
        manageMemory();
        return tbr;
    }

    private void addOldToHeap(Document d){
        Node n = new Node(d.getKey());
        nodeHashMap.put(d.getKey(), n);
        minHeap.insert(n);
    }

    private void storeDocUndoLogic(Document old, Document doc, URI uri, int newMem) {
        Function undoReplaceLambda;
        DocumentFormat oldformat;
        int new_doc_mem = newMem;
        int oldMem;
        if (old != null) { //If an old document was being replaced
            for (String word : old.getWords()) {//delete all trace of old doc from the TRIE
                Object obj = trie.delete(word, old.getKey());
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
                    trie.delete(word, doc.getKey());
                }
                if (uriOnDisk.contains(doc.getKey())) {
                    Node n = new Node(doc.getKey());
                    nodeHashMap.put(doc.getKey(), n);
                    uriOnDisk.remove(doc.getKey());
                }
                doc.setLastUseTime(Long.MIN_VALUE);
                minHeap.reHeapify(nodeHashMap.get(doc.getKey()));
                minHeap.remove();
                nodeHashMap.remove(doc.getKey());
                Document temp = btree.put((URI) u, (DocumentImpl) old);
                this.num_current_docs_used--;//update memory status for # of docs
                this.num_total_bytes_used -= new_doc_mem; // remove the new doc memory size from docStores memory status
                manageMemoryOnPut(old, oldformat);
                for (String word : old.getWords()) { //put old doc into the trie
                    trie.put(word, old.getKey());
                }
                old.setLastUseTime(System.nanoTime());
                Node n = new Node(old.getKey());
                nodeHashMap.put(old.getKey(), n);
                minHeap.insert(nodeHashMap.get(old.getKey()));//put old doc into the heap with new time
                //update docStore memory status
                this.num_current_docs_used++;
                this.num_total_bytes_used += oldMem;
                return temp == doc; //replace btree with what was originally there
            };
        } else { // undo if nothing was deleted as result of docs insertion ( just remove doc that was inserted)
            undoReplaceLambda = (u) -> {
                for (String word : doc.getWords()) {
                    trie.delete(word, doc.getKey());
                }
                if (uriOnDisk.contains(doc.getKey())) {
                    Node n = new Node(doc.getKey());
                    nodeHashMap.put(doc.getKey(), n);
                    uriOnDisk.remove(doc.getKey());
                    minHeap.insert(n);
                }
                if (btree.get(doc.getKey()) == null) {
                    throw new IllegalStateException("This should exist in the btree");
                }
                doc.setLastUseTime(Long.MIN_VALUE);
                minHeap.reHeapify(nodeHashMap.get(doc.getKey()));
                minHeap.remove();
                //remove node from hashmap, node is no longer in minheap don't need to keep track of object
                this.num_current_docs_used--;
                this.num_current_docs_used -= new_doc_mem;
                return btree.put((URI) u, null) == doc;
            };
        }
        cmdStack.push(new GenericCommand<URI>(uri, undoReplaceLambda));
    }

    /**
     * @param uri the unique identifier of the document to get
     * @return the given document
     */
    @Override
    public Document get(URI uri) {
        Document gotDoc = btree.get(uri);
        if (gotDoc != null) {
            if(uriOnDisk.contains(gotDoc.getKey())){
                addOldToHeap(gotDoc);
                uriOnDisk.remove(gotDoc.getKey());
            }
            gotDoc.setLastUseTime(System.nanoTime());
            minHeap.reHeapify(nodeHashMap.get(gotDoc.getKey()));
        }
        return gotDoc;
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
        Node leastUsedDoc = minHeap.remove();
        nodeHashMap.remove(leastUsedDoc.getUri());
        this.num_current_docs_used--;
        if (btree.get(leastUsedDoc.getUri()).getDocumentTxt() != null) {
            this.num_total_bytes_used -= btree.get(leastUsedDoc.getUri()).getDocumentTxt().getBytes().length;
        } else {
            this.num_total_bytes_used -= btree.get(leastUsedDoc.getUri()).getDocumentBinaryData().length;
        }
        try {
            btree.moveToDisk(leastUsedDoc.getUri()); //Least used doc is moved out of memory and into disk
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Add URI to hashset of URI's offloaded to disk, in order to re add to heap when it is entered into memory on a get
        uriOnDisk.add(leastUsedDoc.getUri());
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
        if (btree.get(uri) == null) {
            return false;
        } else {
            //delete and create undo lambda for doc to be deleted from the btree, trie, and heap
            Document temp = btree.get(uri);
            for (String w : temp.getWords()) {
                trie.delete(w, temp.getKey());
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
            if(uriOnDisk.contains(temp.getKey())){
                addOldToHeap(temp);
                uriOnDisk.remove(temp.getKey());
            }
            temp.setLastUseTime(Long.MIN_VALUE);
            minHeap.reHeapify(nodeHashMap.get(temp.getKey()));
            minHeap.remove();
            nodeHashMap.remove(temp.getKey());
            btree.put(uri, null);
            this.num_current_docs_used--;
            this.num_total_bytes_used -= mem;
            Function undoDeleteLambda = (u) -> {
                manageMemoryOnPut(temp,f);
                for (String w : temp.getWords()) {
                    trie.put(w, temp.getKey());
                }
                Document t = btree.put((URI) u, (DocumentImpl) temp);
                temp.setLastUseTime(System.nanoTime());
                Node n = new Node(temp.getKey());
                nodeHashMap.put(temp.getKey(),n);
                minHeap.insert(nodeHashMap.get(temp.getKey()));
                this.num_current_docs_used++;
                this.num_total_bytes_used += mem;
                return t == null;
            };
            cmdStack.push(new GenericCommand<URI>(uri, undoDeleteLambda));
            return true;
        }
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
        Set<URI> docKeys = trie.deleteAll(keyword);
        Set<Document> docsToBeRemoved = new HashSet<>();
        for(URI u : docKeys){
            docsToBeRemoved.add(btree.get(u));
        }
        if (docsToBeRemoved.size() == 0) {
            return Collections.emptySet();
        }
        for (URI u : docKeys) {
            int mem;
            DocumentFormat f;
            if(btree.get(u).getDocumentTxt() != null){//get doc format
                f = DocumentFormat.TXT;
                mem = btree.get(u).getDocumentTxt().getBytes().length;
            }else{
                f = DocumentFormat.BINARY;
                mem = btree.get(u).getDocumentBinaryData().length;
            }
            for (String w : btree.get(u).getWords()) {//remove the doc to be deleted from the trie
                trie.delete(w, u);
            }
            if(uriOnDisk.contains(u)){
                addOldToHeap(btree.get(u));
                uriOnDisk.remove(u);
            }
            btree.get(u).setLastUseTime(Long.MIN_VALUE);
            minHeap.reHeapify(nodeHashMap.get(u));
            minHeap.remove();//remove from the Min Heap
            nodeHashMap.remove(u);
            this.btree.put(u, null);//remove from the btree
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
                    trie.put(w, doc.getKey());
                }
                Document t = this.btree.put((URI) u, (DocumentImpl) doc);
                doc.setLastUseTime(time);
                Node n = new Node(doc.getKey());
                nodeHashMap.put(doc.getKey(),n);
                minHeap.insert(nodeHashMap.get(doc.getKey()));
                this.num_current_docs_used++;
                this.num_total_bytes_used += mem;
                return t == null;
            };
            cmdSet.addCommand(new GenericCommand<>(doc.getKey(), undoDeletion));
        }
        //push the CommandSet on to the stack
        cmdStack.push(cmdSet);
        return docKeys;
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
        Comparator<URI> docComparatorDescending = new Comparator<URI>() {
            @Override
            public int compare(URI o1, URI o2) {
                return btree.get(o2).wordCount(keyword) - btree.get(o1).wordCount(keyword);
            }
        };
        List<Document> matching_docs = new ArrayList<>();
        long time = System.nanoTime();
        for (URI u : trie.getAllSorted(keyword, docComparatorDescending)) {
            if(uriOnDisk.contains(btree.get(u).getKey())){
                addOldToHeap(btree.get(u));
                uriOnDisk.remove(btree.get(u).getKey());
            }
            btree.get(u).setLastUseTime(time);
            minHeap.reHeapify(nodeHashMap.get(u));
            matching_docs.add(btree.get(u));
        }
        return matching_docs;
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
        Comparator<URI> documentComparator = new Comparator<URI>() {
            @Override
            public int compare(URI o1, URI o2) {
                HashSet<String> allWords1 = new HashSet<>();
                HashSet<String> allWords2 = new HashSet<>();
                for (String w : btree.get(o1).getWords()) {
                    if (w.startsWith(keywordPrefix)) allWords1.add(w);
                }
                for (String w : btree.get(o2).getWords()) {
                    if (w.startsWith(keywordPrefix)) allWords2.add(w);
                }
                int doc1PrefCount = 0;
                int doc2PrefCount = 0;
                for (String w : allWords1) {
                    doc1PrefCount += btree.get(o1).wordCount(w);
                }
                for (String w : allWords2) {
                    doc2PrefCount += btree.get(o2).wordCount(w);
                }
                return doc2PrefCount - doc1PrefCount;
            }
        };
        List<URI> listOfDocs = trie.getAllWithPrefixSorted(keywordPrefix, documentComparator);
        List<Document> matching_docs = new ArrayList<>();
        long time = System.nanoTime();
        for (URI u : listOfDocs) {
            if(uriOnDisk.contains(btree.get(u).getKey())){
                addOldToHeap(btree.get(u));
                uriOnDisk.remove(btree.get(u).getKey());
            }
            btree.get(u).setLastUseTime(time);
            minHeap.reHeapify(nodeHashMap.get(u));
            matching_docs.add(btree.get(u));
        }
        return matching_docs;
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
        Set<URI> docKeys = trie.deleteAllWithPrefix(keywordPrefix);
        Set<Document> docsToBeRemoved = new HashSet<>();
        for(URI u: docKeys){
            docsToBeRemoved.add(btree.get(u));
        }
        if (docsToBeRemoved.size() == 0) {
            return Collections.emptySet();
        }
        for (URI u : docKeys) {
            int mem;
            DocumentFormat f;
            if(btree.get(u).getDocumentTxt() != null){//get doc format
                f = DocumentFormat.TXT;
                mem = btree.get(u).getDocumentTxt().getBytes().length;
            }else{
                f = DocumentFormat.BINARY;
                mem = btree.get(u).getDocumentBinaryData().length;
            }
            for (String w : btree.get(u).getWords()) {
                trie.delete(w, u);
            }
            btree.get(u).setLastUseTime(Long.MIN_VALUE);
            minHeap.reHeapify(nodeHashMap.get(u));
            minHeap.remove();
            nodeHashMap.remove(u);
            this.num_current_docs_used--;
            this.num_total_bytes_used -= mem;
            this.btree.put(u, null);
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
                    trie.put(w, doc.getKey());
                }
                Document t = this.btree.put((URI) u, (DocumentImpl) doc);
                doc.setLastUseTime(time);
                Node n = new Node(doc.getKey());
                nodeHashMap.put(doc.getKey(),n);
                minHeap.insert(nodeHashMap.get(doc.getKey()));
                this.num_current_docs_used++;
                this.num_total_bytes_used += mem;
                return t == null;
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
