package edu.yu.cs.com1320.project.stage5.impl;

import edu.yu.cs.com1320.project.stage5.Document;

import java.net.URI;
import java.util.*;

public class DocumentImpl implements Document {
    private final URI uri;
    private String txt;
    private byte[] binaryData;
    private Map<String, Integer> wordMap;

    private long nanoTime;

    public DocumentImpl(URI uri, String text, Map<String, Integer> wordCountMap){
        if ((text == null) || (text.length() < 1) || (uri == null)) {
            throw new IllegalArgumentException();
        }
        this.uri = uri;
        this.txt = text;
        if(wordCountMap == null){
            createWordMap(text.getBytes());
        }else{
            this.wordMap = wordCountMap;
        }
        this.nanoTime = 0;
    }

    public DocumentImpl(URI uri, byte[] binaryData) {
        if ((uri == null) || (binaryData == null) || (binaryData.length == 0)) {
            throw new IllegalArgumentException();
        }
        this.uri = uri;
        this.binaryData = binaryData;
        this.nanoTime = 0;
    }

    private void createWordMap(byte[] bytes) {
        String s = new String(bytes);
        s = s.replaceAll("[^A-Za-z0-9 ]", "");
        String[] words = s.split(" ");
        this.wordMap = new HashMap<>();
        for (String str : words) {
            if (this.wordMap.containsKey(str)) {
                int current = this.wordMap.get(str);
                this.wordMap.put(str, ++current);
            } else {
                this.wordMap.put(str, 1);
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
        if (this.wordMap == null) {
            return 0;
        }
        if (!this.wordMap.containsKey(word)) {
            return 0;
        } else {
            return this.wordMap.get(word);
        }
    }

    /**
     * @return all the words that appear in the document
     */
    @Override
    public Set<String> getWords() {
        HashSet<String> allWords = new HashSet<>();
        if (this.wordMap != null) {
            allWords.addAll(this.wordMap.keySet());
        }
        return allWords;
    }

    /**
     * return the last time this document was used, via put/get or via a search result
     * (for stage 4 of project)
     */
    @Override
    public long getLastUseTime() {
        return this.nanoTime;
    }

    @Override
    public void setLastUseTime(long timeInNanoseconds) {
        this.nanoTime = timeInNanoseconds;
    }

    /**
     * @return a copy of the word to count map, so it can be serialized
     */
    @Override
    public Map<String, Integer> getWordMap() {
        return this.wordMap;
    }

    /**
     * This must set the word to count map during deserialization
     *
     * @param wordMap
     */
    @Override
    public void setWordMap(Map<String, Integer> wordMap) {
        this.wordMap = wordMap;
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
    public int compareTo(Document o) {
        if (o == null) {
            throw new NullPointerException();
        }
        if (this.getLastUseTime() < o.getLastUseTime()) {
            return -1;
        } else if (this.getLastUseTime() > o.getLastUseTime()) {
            return 1;
        } else {
            return 0;

        }
    }
}