package edu.yu.cs.com1320.project.stage2.impl;

import edu.yu.cs.com1320.project.Command;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.stage2.Document;
import edu.yu.cs.com1320.project.stage2.DocumentStore;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.function.Function;

public class DocumentStoreImpl implements DocumentStore {
    public StackImpl<Command> cmdStack;
    private HashTableImpl<URI, DocumentImpl> hashTable;

    public DocumentStoreImpl() {
        hashTable = new HashTableImpl<>();
        cmdStack = new StackImpl<>();
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
                cmdStack.push(new Command(uri, undoDeleteLambda));
            }
            return temp == null ? 0 : temp.hashCode();
        }
        byte[] read = input.readAllBytes();
        switch (format) {
            case TXT -> {
                String s = new String(read);
                DocumentImpl doc1 = new DocumentImpl(uri, s);
                DocumentImpl old1 = hashTable.put(uri, doc1);
                if (old1 != null) {
                    Function undoReplaceLambda = (u) -> hashTable.put(u, old1) == doc1;
                    cmdStack.push(new Command(uri, undoReplaceLambda));

                } else {
                    Function undoNewLambda = (u) -> hashTable.put(u, null) == doc1;
                    cmdStack.push(new Command(uri, undoNewLambda));
                }
                return old1 == null ? 0 : old1.hashCode();
            }
            case BINARY -> {
                DocumentImpl doc2 = new DocumentImpl(uri, read);
                DocumentImpl old2 = hashTable.put(uri, doc2);
                if (old2 != null) {
                    Function undoReplaceLambda = (u) -> hashTable.put(u, old2) == doc2;
                    cmdStack.push(new Command(uri, undoReplaceLambda));

                } else {
                    Function undoNewLambda = (u) -> hashTable.put(u, null) == doc2;
                    cmdStack.push(new Command(uri, undoNewLambda));
                }
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
            cmdStack.push(new Command(uri, undoDeleteLambda));
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
        if (cmdStack.size() == 0) {
            throw new IllegalStateException("No Actions to be undone");
        }
        StackImpl<Command> tempStack = new StackImpl<>();
        while (cmdStack.peek() != null) {
            if (cmdStack.peek().getUri() == uri) {
                cmdStack.pop().undo();
                break;
            } else {
                tempStack.push(cmdStack.pop());
            }
        }
        while (tempStack.peek() != null) {
            cmdStack.push(tempStack.pop());
        }
    }
}
