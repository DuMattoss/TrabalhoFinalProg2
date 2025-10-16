package dao;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.*;

public class MongoConn {
    private static MongoClient client;
    private static MongoDatabase db;

    public static MongoDatabase db() {
        if (db == null) {
            CodecRegistry pojoCodecRegistry = fromRegistries(
                    MongoClientSettings.getDefaultCodecRegistry(),
                    fromProviders(PojoCodecProvider.builder().automatic(true).build())
            );

            client = MongoClients.create("mongodb://localhost:27017");
            db = client.getDatabase("carrosuenp").withCodecRegistry(pojoCodecRegistry);
        }
        return db;
    }
}
