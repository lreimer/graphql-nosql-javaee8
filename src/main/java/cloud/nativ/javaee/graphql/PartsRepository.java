package cloud.nativ.javaee.graphql;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import org.bson.Document;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class PartsRepository {

    @Inject
    private MongoDatabase database;
    private MongoCollection<Document> parts;

    @PostConstruct
    void initialize() {
        parts = database.getCollection("parts");
    }

    public List<Map<String, Object>> findByBaureihe(String baureihe) {
        List<Map<String, Object>> all = new ArrayList<>();
        MongoIterable<Document> documents = parts.find(new Document("baureihe", baureihe));
        MongoCursor<Document> cursor = documents.iterator();
        try {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                all.add(document);
            }
        } finally {
            cursor.close();
        }
        return all;
    }
}
