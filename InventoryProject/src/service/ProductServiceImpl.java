package service;

import db.DBConnection;
import model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductServiceImpl implements ProductService {

    @Override
    public int addProduct(Product p) throws Exception {
        String sql = "INSERT INTO products(sku, name, category, price, quantity, supplier) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getSku());
            ps.setString(2, p.getName());
            ps.setString(3, p.getCategory());
            ps.setDouble(4, p.getPrice());
            ps.setInt(5, p.getQuantity());
            ps.setString(6, p.getSupplier());
            int affected = ps.executeUpdate();
            if (affected == 0) throw new SQLException("Insert failed, no rows affected.");
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    p.setId(id);
                    return id;
                }
            }
            return -1;
        }
    }

    @Override
    public List<Product> getAllProducts() throws Exception {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT id, sku, name, category, price, quantity, supplier FROM products ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Product p = new Product(
                        rs.getInt("id"),
                        rs.getString("sku"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getInt("quantity"),
                        rs.getString("supplier")
                );
                list.add(p);
            }
        }
        return list;
    }

    @Override
    public boolean updateProduct(Product p) throws Exception {
        String sql = "UPDATE products SET sku = ?, name = ?, category = ?, price = ?, quantity = ?, supplier = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getSku());
            ps.setString(2, p.getName());
            ps.setString(3, p.getCategory());
            ps.setDouble(4, p.getPrice());
            ps.setInt(5, p.getQuantity());
            ps.setString(6, p.getSupplier());
            ps.setInt(7, p.getId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteProduct(int id) throws Exception {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Product getProductById(int id) throws Exception {
        String sql = "SELECT id, sku, name, category, price, quantity, supplier FROM products WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Product(
                            rs.getInt("id"),
                            rs.getString("sku"),
                            rs.getString("name"),
                            rs.getString("category"),
                            rs.getDouble("price"),
                            rs.getInt("quantity"),
                            rs.getString("supplier")
                    );
                }
            }
        }
        return null;
    }
}
