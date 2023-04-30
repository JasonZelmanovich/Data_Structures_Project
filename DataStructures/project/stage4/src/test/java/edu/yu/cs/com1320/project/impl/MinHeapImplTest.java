package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.stage4.Document;
import edu.yu.cs.com1320.project.stage4.impl.DocumentImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

class OmniscientHeap<E extends Comparable<E>> extends MinHeapImpl<E>{
    public OmniscientHeap(){
        this.elements = (E[]) new Comparable[5];
    }
    public boolean publicIsEmpty(){
        return this.isEmpty();
    }
    public boolean publicIsGreater(int i, int j){
        return this.isGreater(i,j);
    }
    public void publicDouble(){
        this.doubleArraySize();
    }
    public int publicGetIndex(E element){
        return this.getArrayIndex(element);
    }
}
class MinHeapImplTest {
    MinHeapImpl<Document> minHeap;
    OmniscientHeap<Document> OmniHeap;
    @BeforeEach
    void setUp() {
        minHeap = new MinHeapImpl<>();
        OmniHeap = new OmniscientHeap<>();
    }
    @AfterEach
    void tearDown() {
        minHeap = null;
        OmniHeap = null;
    }
    @Test
    void isEmpty() {
        assertTrue(OmniHeap.publicIsEmpty());
        Document doc_1 = new DocumentImpl(URI.create("FirstText"),"First Text 1");
        doc_1.setLastUseTime(System.nanoTime());
        OmniHeap.insert(doc_1);
        assertFalse(OmniHeap.publicIsEmpty());
        OmniHeap.remove();
        assertTrue(OmniHeap.publicIsEmpty());
    }
    @Test
    void isGreaterAndGetIndex() {
        Document doc_less = new DocumentImpl(URI.create("less"),"This one was created first and should be the least recently used");
        doc_less.setLastUseTime(System.nanoTime());
        Document doc_greater = new DocumentImpl(URI.create("Greater"),"This one was created second & should be the most recently used");
        doc_greater.setLastUseTime(System.nanoTime());
        OmniHeap.insert(doc_less);
        OmniHeap.insert(doc_greater);
        int indexDocGreater = OmniHeap.getArrayIndex(doc_greater);
        int indexDocLess = OmniHeap.getArrayIndex(doc_less);
        assertTrue(OmniHeap.publicIsGreater(indexDocGreater,indexDocLess));
        doc_less.setLastUseTime(System.nanoTime());
        assertFalse(OmniHeap.publicIsGreater(indexDocGreater,indexDocLess));
        OmniHeap.reHeapify(doc_less);
    }

}