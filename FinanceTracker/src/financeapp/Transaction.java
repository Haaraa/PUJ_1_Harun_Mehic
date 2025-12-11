package financeapp;

import org.bson.Document;
import org.bson.types.ObjectId;

public class Transaction {
    private ObjectId id;
    private String type;      // prihod / rashod
    private String category;  // plata / hrana / racuni / zabava / prijevoz / ostalo
    private double amount;
    private String description;

    public Transaction(String type, String category, double amount, String description) {
        this.id = new ObjectId();
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.description = description;
    }

    public Transaction(ObjectId id, String type, String category, double amount, String description) {
        this.id = id;
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.description = description;
    }

    public ObjectId getId() { return id; }
    public String getType() { return type; }
    public String getCategory() { return category; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }

    public Document toDocument() {
        return new Document("_id", id)
                .append("Vrsta", type)
                .append("Kategorija", category)
                .append("Iznos", amount)
                .append("Opis", description);
    }
}
