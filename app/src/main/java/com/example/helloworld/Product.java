package com.example.helloworld;

public class Product {
    private String id;
    private String name;
    private String description;
    private String price;
    private String category;
    private String date;
    private String condition;

    // Constructor
    public Product(String id, String name, String description, String price, String category, String date, String condition) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.date = date;
        this.condition = condition;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getPrice() { return price; }
    public String getCategory() { return category; }
    public String getDate() { return date; }
    public String getCondition() { return condition; }

    public boolean isNew() {
        return "New".equalsIgnoreCase(condition);
    }
}
