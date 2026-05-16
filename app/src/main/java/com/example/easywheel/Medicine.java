package com.example.easywheel;

public class Medicine {

    private String name;
    private int quantity;
    private String price;

    public Medicine(String name, int quantity, String price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getPrice() {
        return price;
    }

    // ADD THESE (important for update)
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}