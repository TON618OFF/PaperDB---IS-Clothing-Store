package com.example.paperdb;

public class ClothingItem {
    private String id;
    private String name;
    private String size;
    private double price;
    private String imagePath; // Путь к изображению

    public ClothingItem(String id, String name, String size, double price, String imagePath) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.price = price;
        this.imagePath = imagePath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
