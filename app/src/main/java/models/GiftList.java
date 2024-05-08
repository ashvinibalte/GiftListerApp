package models;

import java.util.ArrayList;
import java.util.List;

public class GiftList {
    private String docId;
    private String name;
    private String creator;
    private int numItems;
    private double totalPledgedAmount;
    private double totalAmount;
    private List<String> tags;
    private List<Product> products;  // List to store products

    // Default constructor for Firebase
    public GiftList() {
        products = new ArrayList<>();  // Initialize the product list
    }

    // Constructor with all fields
    public GiftList(String docId, String name, String creator, int numItems, double totalPledgedAmount, double totalAmount, List<String> tags, List<Product> products) {
        this.docId = docId;
        this.name = name;
        this.creator = creator;
        this.numItems = numItems;
        this.totalPledgedAmount = totalPledgedAmount;
        this.totalAmount = totalAmount;
        this.tags = tags;
        this.products = products;
    }

    // Getters and setters
    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public int getNumItems() {
        return numItems;
    }

    public void setNumItems(int numItems) {
        this.numItems = numItems;
    }

    public double getTotalPledgedAmount() {
        return totalPledgedAmount;
    }

    public void setTotalPledgedAmount(double totalPledgedAmount) {
        this.totalPledgedAmount = totalPledgedAmount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    // Method to calculate total amount from products
    public void calculateTotalAmount() {
        totalAmount = 0;  // Reset total amount
        for (Product product : products) {
            totalAmount += product.getPrice();  // Sum up the prices of each product
        }
    }
}
