package financeapp;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import java.util.ArrayList;

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
        MongoCursor<Document> cursor = collection.find().iterator();

        while (cursor.hasNext()) {
            Document d = cursor.next();

            String type = d.getString("Vrsta");

            // SIGURNO čitanje iznosa — bez više NullPointer grešaka
            Object rawAmount = d.get("Iznos");
            double amount = 0.0;

            if (rawAmount instanceof Number) {
                amount = ((Number) rawAmount).doubleValue();
            } else if (rawAmount instanceof String) {
                try { amount = Double.parseDouble((String) rawAmount); }
                catch (Exception ignored) {}
            }

            String description = d.getString("Opis");

            list.add(new Transaction(type, amount, description));
        }
        return list;
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
