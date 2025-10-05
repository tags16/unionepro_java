package com.example.orders.dao;

import com.example.orders.model.Product;
import com.example.orders.util.DbUtil;

import java.sql.*;

public class ProductDao {

    public int insert(Product product) throws SQLException {
        String sql = "INSERT INTO product (description, price, quantity, category_id) VALUES (?, ?, ?, ?) RETURNING id";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.description());
            ps.setDouble(2, product.price());
            ps.setInt(3, product.quantity());
            ps.setObject(4, product.categoryId()); // setObject для возможного null

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new SQLException("Insert failed, no ID obtained.");
            }
        }
    }

    public void decreaseQuantity(Connection conn, int productId, int amount) throws SQLException {
        String sql = "UPDATE product SET quantity = quantity - ? WHERE id = ? AND quantity >= ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, amount);
            ps.setInt(2, productId);
            ps.setInt(3, amount);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Not enough product in stock or product not found.");
            }
        }
    }

    public void updatePrice(int productId, double newPrice) throws SQLException {
        String sql = "UPDATE product SET price = ? WHERE id = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, newPrice);
            ps.setInt(2, productId);
            ps.executeUpdate();
        }
    }

    public void updateQuantity(int productId, int newQuantity) throws SQLException {
        String sql = "UPDATE product SET quantity = ? WHERE id = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newQuantity);
            ps.setInt(2, productId);
            ps.executeUpdate();
        }
    }

    public void deleteById(int id) throws SQLException {
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM product WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public void printTopProducts(int limit) throws SQLException {
        String sql = """
            SELECT
                p.description,
                SUM(o.qty) AS total_sold
            FROM orders o
            JOIN product p ON o.product_id = p.id
            GROUP BY p.description
            ORDER BY total_sold DESC
            LIMIT ?
            """;
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    System.out.printf(" - %s, sold: %d%n",
                        rs.getString("description"),
                        rs.getInt("total_sold"));
                }
            }
        }
    }
}