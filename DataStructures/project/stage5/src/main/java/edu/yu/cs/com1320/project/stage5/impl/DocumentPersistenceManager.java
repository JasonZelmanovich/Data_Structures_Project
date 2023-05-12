package edu.yu.cs.com1320.project.stage5.impl;

import com.google.gson.*;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;
import jakarta.xml.bind.DatatypeConverter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;

/**
 * created by the document store and given to the BTree via a call to BTree.setPersistenceManager
 */
public class DocumentPersistenceManager implements PersistenceManager<URI, Document> {
    JsonSerializer<Document> documentJsonSerializer = new JsonSerializer<Document>() {
        @Override
        public JsonElement serialize(Document document, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject obj = new JsonObject();
            if(document.getDocumentTxt() != null){
                obj.addProperty("txt",gson.toJson(document.getDocumentTxt()));
                obj.addProperty("wordMap",gson.toJson(document.getWordMap()));
            }else{
                String base64Encoded = DatatypeConverter.printBase64Binary(document.getDocumentBinaryData());
                obj.addProperty("binaryData",gson.toJson(base64Encoded));
            }
            obj.addProperty("uri", gson.toJson(document.getKey()));
            return obj;
        }
    };
    Gson gson = new GsonBuilder().registerTypeAdapter(Document.class, documentJsonSerializer).create();
    public DocumentPersistenceManager(File baseDir){
    }

    @Override
    public void serialize(URI uri, Document val) throws IOException {
        
    }

    @Override
    public Document deserialize(URI uri) throws IOException {
        return null;
    }

    @Override
    public boolean delete(URI uri) throws IOException {
        return false;
    }

}


