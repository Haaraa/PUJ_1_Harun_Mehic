package financeapp;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;

import static com.mongodb.client.model.Filters.eq;

public class TransactionManager {

    private final MongoCollection<Document> collection;

    public TransactionManager() {
        MongoDatabase db = MongoDBConnection.getDatabase();
        collection = db.getCollection("transactions");
    }

    public void addTransaction(Transaction t) {
        collection.insertOne(t.toDocument());
    }

    public ArrayList<Transaction> getAllTransactions() {
        ArrayList<Transaction> list = new ArrayList<>();
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                Document d = cursor.next();

                ObjectId id = d.getObjectId("_id");
                String type = d.getString("Vrsta");
                String category = d.getString("Kategorija");
                Number amountNum = (Number) d.get("Iznos");
                double amount = amountNum.doubleValue();
                String description = d.getString("Opis");

                list.add(new Transaction(id, type, category, amount, description));
            }
        }
        return list;
    }

    public void updateTransaction(Transaction t) {
        Document updated = new Document("Vrsta", t.getType())
                .append("Kategorija", t.getCategory())
                .append("Iznos", t.getAmount())
                .append("Opis", t.getDescription());

        collection.updateOne(eq("_id", t.getId()), new Document("$set", updated));
    }

    public void deleteTransaction(ObjectId id) {
        collection.deleteOne(eq("_id", id));
    }

    public double getTotalIncome() {
        return getAllTransactions().stream()
                .filter(t -> t.getType().equals("Prihod"))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getTotalExpense() {
        return getAllTransactions().stream()
                .filter(t -> t.getType().equals("Rashod"))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }
}
