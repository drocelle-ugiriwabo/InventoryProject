package model;

public class Product {
    private int id;
    private String sku;
    private String name;
    private String category;
    private double price;
    private int quantity;
    private String supplier;

    public Product() {}

    public Product(int id, String sku, String name, String category, double price, int quantity, String supplier) {
        this.id = id; this.sku = sku; this.name = name; this.category = category;
        this.price = price; this.quantity = quantity; this.supplier = supplier;
    }

    public Product(String sku, String name, String category, double price, int quantity, String supplier) {
        this(0, sku, name, category, price, quantity, supplier);
    }

    // Getters & setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }
}
