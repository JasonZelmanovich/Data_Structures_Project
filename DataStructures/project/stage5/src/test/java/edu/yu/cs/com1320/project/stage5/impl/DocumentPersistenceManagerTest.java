package edu.yu.cs.com1320.project.stage5.impl;

import edu.yu.cs.com1320.project.stage5.Document;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

class DocumentPersistenceManagerTest {

    @Test
    void serialize() throws IOException {
        File baseDir = new File("C:\\Users\\jason\\Desktop");
        DocumentPersistenceManager manager = new DocumentPersistenceManager(baseDir);
        URI uri = URI.create("https://www.google.com/Testing123/doc");
        Document doc = new DocumentImpl(uri,"Shalom this is my text of stuff",null);
        manager.serialize(doc.getKey(),doc);
    }

    @Test
    void deserialize() throws IOException {
        URI uri = URI.create("https://www.google.com/Testing123/doc");
        File baseDir = new File("C:\\Users\\jason\\Desktop");
        DocumentPersistenceManager manager = new DocumentPersistenceManager(baseDir);
        Document doc = manager.deserialize(uri);

    }

    @Test
    void delete() throws IOException {
        URI uri = URI.create("https://www.google.com/Testing123/doc");
        File baseDir = new File("C:\\Users\\jason\\Desktop");
        DocumentPersistenceManager manager = new DocumentPersistenceManager(baseDir);
        manager.delete(uri);
    }
}