package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.MinHeap;

import java.util.NoSuchElementException;

public class MinHeapImpl<E extends Comparable<E>> extends MinHeap<E> {

    public MinHeapImpl() {
        this.elements = (E[]) new Comparable[5];
    }


    @Override
    public void reHeapify(E element) {
        int index = getArrayIndex(element);
        downHeap(index);
        //Maybe check the count here to see if the index exists - possible null pointer being thrown in upHeap method
        upHeap(index);
    }

    @Override
    protected int getArrayIndex(E element) {
        if (count == 0 || element == null) {
            throw new NoSuchElementException();
        }
        for (int i = 1; i < this.elements.length; i++) {
            if (this.elements[i].equals(element)) {
                return i;
            }
        }
        throw new NoSuchElementException();
    }

    @Override
    protected void doubleArraySize() {
        int current_size = this.elements.length;
        E[] temp = (E[]) new Comparable[current_size * 2];
        for (int i = 0; i < current_size; i++) {
            temp[i] = this.elements[i];
        }
        this.elements = temp;
    }
}
