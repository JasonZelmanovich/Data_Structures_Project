package edu.yu.cs.com1320.project.stage5.impl;

import com.google.gson.*;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;
import jakarta.xml.bind.DatatypeConverter;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Map;

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

    JsonDeserializer<Document> documentJsonDeserializer = new JsonDeserializer<Document>() {
        @Override
        public Document deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Document doc;
            URI uri = URI.create(jsonObject.get("uri").getAsString());
            if(jsonObject.has("txt")){
                Map<String, Integer> map = gson.fromJson(jsonObject.get("wordMap"), Map.class);
                doc = new DocumentImpl(uri,jsonObject.get("txt").getAsString(),map);
            }else{
                byte[] base64Decoded = DatatypeConverter.parseBase64Binary(jsonObject.get("binaryData").getAsString());
                doc = new DocumentImpl(uri,base64Decoded);
            }
            return doc;
        }
    };
    GsonBuilder builder = new GsonBuilder();
    Gson gson;
    File baseDir = null;
    public DocumentPersistenceManager(File baseDir){
        if(baseDir.isDirectory()){
            this.baseDir = baseDir;
        }
        builder.registerTypeAdapter(Document.class,documentJsonSerializer);
        builder.registerTypeAdapter(Document.class, documentJsonDeserializer);
        gson = builder.create();
    }

    private String getBase(){
        String dir;
        if(this.baseDir != null){
            dir = this.baseDir.getAbsolutePath();
        }else{
            dir = System.getProperty("user.dir");
        }
        return dir;
    }

    @Override
    public void serialize(URI uri, Document val) throws IOException {
        String host = uri.getHost();
        String dir;
        String jsonDoc = gson.toJson(val);
        dir = getBase();
        File file = new File(dir, host + uri.getPath() + ".json");
        if(!file.getParentFile().isDirectory()){
            file.getParentFile().mkdirs();
        }
        file.createNewFile();
        FileWriter writer = new FileWriter(file);
        writer.write(jsonDoc);
        writer.close();
    }

    @Override
    public Document deserialize(URI uri) throws IOException {
        String host = uri.getHost();
        File toDeserialize = new File(getBase(),host + uri.getPath() + ".json");
        Document doc = null;
        if(toDeserialize.isFile()){
            Reader reader = Files.newBufferedReader(toDeserialize.toPath());
            doc = gson.fromJson(reader,Document.class);
        }
        return doc;
    }

    /**
     * delete the file stored on disk that corresponds to the given key
     * @param uri
     * @return true or false to indicate if deletion occured or not
     * @throws IOException
     */
    @Override
    public boolean delete(URI uri) throws IOException {
        boolean b = false;
        File file = new File(getBase(),uri.getHost() + uri.getPath() + ".json");
        if(file.isFile() && file.exists()){
            b = file.delete();
        }
        return b;
    }

}


