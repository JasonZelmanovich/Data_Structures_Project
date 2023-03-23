package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Stack;

public class StackImpl<T> implements Stack<T> {
    private CustomLinkedList<T> head;
    private int size;

    public StackImpl() {
        size = 0;
    }

    /**
     * @param element object to add to the Stack
     */
    @Override
    public void push(T element) {
        if (element == null) {
            return;
        }
        if (this.head == null) {
            this.head = new CustomLinkedList<>(element);
            this.size++;
            return;
        }
        CustomLinkedList<T> newH = new CustomLinkedList<>(element);
        newH.next = this.head;
        this.head = newH;
        this.size++;
    }

    /**
     * removes and returns element at the top of the stack
     *
     * @return element at the top of the stack, null if the stack is empty
     */
    @Override
    public T pop() {
        if (head == null) {
            return null;
        }
        T val = head.getValue();
        head = head.next;
        this.size--;
        return val;
    }

    /**
     * @return the element at the top of the stack without removing it
     */
    @Override
    public T peek() {
        if (head == null) {
            return null;
        }
        return head.getValue();
    }

    /**
     * @return how many elements are currently in the stack
     */
    @Override
    public int size() {
        return this.size;
    }

    private class CustomLinkedList<value> {
        private value v;
        private CustomLinkedList next = null;

        public CustomLinkedList(value v) {
            this.v = v;
        }

        private value getValue() {
            return this.v;
        }

        private void put(value v) {
            this.v = v;
        }
    }
}
