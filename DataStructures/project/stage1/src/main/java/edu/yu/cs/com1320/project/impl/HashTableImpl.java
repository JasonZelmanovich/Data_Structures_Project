package edu.yu.cs.com1320.project.impl;

 import edu.yu.cs.com1320.project.HashTable;


 public class HashTableImpl<Key, Value> implements HashTable{
     private final CustomLinkedList[] entries;
     public HashTableImpl() {
         this.entries = new CustomLinkedList[5];
     }

     /**
      * @param k the key whose value should be returned
      * @return the value that is stored in the HashTable for k, or null if there is no such key in the table
      */
     @Override
     public Value get(Object k) {
         Key currentK = (Key) k;
         int index = hash(currentK);
         CustomLinkedList<Key, Value> temp = entries[index];
         if(temp != null){
             if(temp.getKey().equals(currentK)){
                 return temp.getValue();
             }else{
                 while(temp.getNext() != null) {
                     temp = temp.getNext();
                     if(temp.getKey().equals(currentK)){
                         return temp.getValue();
                     }
                 }

             }
         }
         return null;
     }

     /**
      * @param k the key at which to store the value
      * @param v the value to store.
      *          To delete an entry, put a null value.
      * @return if the key was already present in the HashTable, return the previous value stored for the key. If the key was not already present, return null.
      */
     @Override
     public Value put(Object k, Object v) {
         Key currentK = (Key) k;
         Value currentV = (Value) v;
         int index = hash(currentK);
         CustomLinkedList<Key, Value> temp = entries[index];
         if(currentV == null){
             return delete(currentK, index);
         }
         if(temp != null){
             if(temp.getKey().equals(currentK)){
                 return temp.put(currentV);
             }else{
                 while(temp.getNext() != null) {
                     temp = temp.getNext();
                     if(temp.getKey().equals(currentK)){
                         return temp.put(currentV);
                     }
                 }
                 temp.next = new CustomLinkedList<Key, Value>(currentK, currentV);
                 return null;
             }
         }else{
             entries[index] = new CustomLinkedList<Key, Value>(currentK, currentV);
         }
         return null;
     }

     private Value delete(Key k, int index) {
         CustomLinkedList<Key, Value> temp = entries[index];
         if(temp != null){
             if(temp.getKey().equals(k)){
                 Value old = temp.getValue();
                 entries[index] = temp.next;
                 return old;
             }else{
                 while(temp.getNext() != null) {
                     CustomLinkedList prev = temp;
                     temp = temp.getNext();
                     if(temp.getKey().equals(k)){
                         Value old = temp.getValue();
                         prev.next = temp.next;
                         return old;
                     }
                 }
             }
         }
         return null;
     }

     private int hash(Key k){
         return (k.hashCode() & 0x7fffffff) % this.entries.length;
     }

     private class CustomLinkedList<Key,Value> {
         private final Key k;
         private Value v;
         private CustomLinkedList next = null;
         private CustomLinkedList(Key k, Value v) {
             this.k = k;
             this.v = v;
         }

         private Key getKey(){
             return this.k;
         }

         private Value getValue(){
             return this.v;
         }

         private Value put(Value v){
             Value old = this.v;
             this.v = v;
             return old;
         }

         private CustomLinkedList getNext(){
             return this.next;
         }

     }
 }