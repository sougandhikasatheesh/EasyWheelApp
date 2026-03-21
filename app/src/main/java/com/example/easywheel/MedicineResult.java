package com.example.easywheel;

public class MedicineResult {

    String name;
    String shop;
    int quantity;
    int price;

    public MedicineResult(String name, String shop, int quantity, int price) {
        this.name = name;
        this.shop = shop;
        this.quantity = quantity;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public String getShop() {
        return shop;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getPrice() {
        return price;
    }
}