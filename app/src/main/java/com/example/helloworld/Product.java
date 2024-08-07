package com.example.helloworld;

public class Product {
    private String id;
    private String name;
    private String description;
    private String price;
    private String category;
    private String date;
    private String condition;
    private int quantity; // Added quantity field

    // Constructor
    public Product(String id, String name, String description, String price, String category, String date, String condition, int quantity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.date = date;
        this.condition = condition;
        this.quantity = quantity; // Initialize quantity
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    public int getQuantity() { return quantity; } // Getter for quantity
    public void setQuantity(int quantity) { this.quantity = quantity; } // Setter for quantity

    public boolean isNew() {
        return "New".equalsIgnoreCase(condition);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price='" + price + '\'' +
                ", category='" + category + '\'' +
                ", date='" + date + '\'' +
                ", condition='" + condition + '\'' +
                ", quantity=" + quantity + // Include quantity in toString
                '}';
    }
}
