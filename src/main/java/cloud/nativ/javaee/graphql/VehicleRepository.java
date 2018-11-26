package cloud.nativ.javaee.graphql;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import org.bson.Document;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class VehicleRepository {

    @Inject
    private MongoDatabase database;
    private MongoCollection<Document> vehicles;

    @PostConstruct
    void initialize() {
        vehicles = database.getCollection("vehicle");
    }

    public List<Map<String, Object>> findAll() {
        List<Map<String, Object>> all = new ArrayList<>();
        MongoCursor<Document> cursor = vehicles.find().iterator();
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

    public Map<String, Object> findByVin17(String vin17) {
        MongoIterable<Document> documents = vehicles.find(new Document("vin17", vin17));
        Optional<Document> vehicle = Optional.ofNullable(documents.first());
        return vehicle.orElseThrow(NotFoundException::new);
    }
}
