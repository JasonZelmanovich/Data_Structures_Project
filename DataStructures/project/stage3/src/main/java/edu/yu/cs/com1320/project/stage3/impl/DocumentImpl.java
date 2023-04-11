package edu.yu.cs.com1320.project.stage3.impl;

import edu.yu.cs.com1320.project.stage3.Document;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DocumentImpl implements Document {
    private final URI uri;
    private String txt;
    private byte[] binaryData;
    private HashMap<String,Integer> wordMap;

    public DocumentImpl(URI uri, String txt) {
        if ((txt == null) || (txt.length() < 1) || (uri == null)) {
            throw new IllegalArgumentException();
        }
        this.uri = uri;
        this.txt = txt;
        createWordMap(txt.getBytes());
    }

    public DocumentImpl(URI uri, byte[] binaryData) {
        if ((uri == null) || (binaryData == null) || (binaryData.length == 0)) {
            throw new IllegalArgumentException();
        }
        this.uri = uri;
        this.binaryData = binaryData;
    }

    private void createWordMap(byte[] bytes){
        String s = new String(bytes);
        s = s.replaceAll("[^A-Za-z0-9 ]","");
        String[] words = s.split(" ");
        this.wordMap = new HashMap<>();
        for(String str : words){
            if(this.wordMap.containsKey(str)){
                int current = this.wordMap.get(str);
                this.wordMap.put(str,++current);
            }else{
                this.wordMap.put(str,1);
            }
        }
    }

    /**
     * @return content of text document
     */
    @Override
    public String getDocumentTxt() {
        return this.txt;
    }

    /**
     * @return content of binary data document
     */
    @Override
    public byte[] getDocumentBinaryData() {
        return this.binaryData;
    }

    /**
     * @return URI which uniquely identifies this document
     */
    @Override
    public URI getKey() {
        return uri;
    }

    /**
     * how many times does the given word appear in the document?
     *
     * @param word
     * @return the number of times the given words appears in the document. If it's a binary document, return 0.
     */
    @Override
    public int wordCount(String word) {
        if(this.wordMap == null){
            return 0;
        }
        if(!this.wordMap.containsKey(word)){
            return 0;
        }else{
            return this.wordMap.get(word);
        }
    }

    /**
     * @return all the words that appear in the document
     */
    @Override
    public Set<String> getWords() {
        HashSet<String> allWords = new HashSet<>();
        if(this.wordMap != null) {
            allWords.addAll(this.wordMap.keySet());
        }
        return allWords;
    }

    /**
     * Returns a hash code value for the object. This method is
     * supported for the benefit of hash tables such as those provided by
     * {@link HashMap}.
     * <p>
     * The general contract of {@code hashCode} is:
     * <ul>
     * <li>Whenever it is invoked on the same object more than once during
     *     an execution of a Java application, the {@code hashCode} method
     *     must consistently return the same integer, provided no information
     *     used in {@code equals} comparisons on the object is modified.
     *     This integer need not remain consistent from one execution of an
     *     application to another execution of the same application.
     * <li>If two objects are equal according to the {@link
     *     \equals(Object) equals} method, then calling the {@code
     *     hashCode} method on each of the two objects must produce the
     *     same integer result.
     * <li>It is <em>not</em> required that if two objects are unequal
     *     according to the {@link \equals(Object) equals} method, then
     *     calling the {@code hashCode} method on each of the two objects
     *     must produce distinct integer results.  However, the programmer
     *     should be aware that producing distinct integer results for
     *     unequal objects may improve the performance of hash tables.
     * </ul>
     *
     * @return a hash code value for this object.
     * @implSpec As far as is reasonably practical, the {@code hashCode} method defined
     * by class {@code Object} returns distinct integers for distinct objects.
     * @see Object#equals(Object)
     * @see System#identityHashCode
     */
    @Override
    public int hashCode() {
        int result = this.uri.hashCode();
        result = 31 * result + (this.txt != null ? this.txt.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(this.binaryData);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DocumentImpl)) {
            return false;
        }
        DocumentImpl t = (DocumentImpl) obj;
        return t.hashCode() == this.hashCode();
    }
}