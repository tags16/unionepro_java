package com.example.orders.dao;

import com.example.orders.model.OrderRecord;
import com.example.orders.util.DbUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDao {

    public int createOrder(Connection conn, int productId, int customerId, int quantity, String status) throws SQLException {
        String sql = """
            INSERT INTO orders (product_id, customer_id, qty, status_id)
            VALUES (?, ?, ?, (SELECT id FROM order_status WHERE name = ?))
            RETURNING id
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.setInt(2, customerId);
            ps.setInt(3, quantity);
            ps.setString(4, status);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new SQLException("Order creation failed, no ID obtained.");
            }
        }
    }

    public List<OrderRecord> findLastOrders(int limit) throws SQLException {
        List<OrderRecord> orders = new ArrayList<>();
        String sql = """
            SELECT
                o.id, o.order_date, c.first_name, c.last_name,
                p.description, o.qty, s.name as status_name, p.price
            FROM orders o
            JOIN customer c ON o.customer_id = c.id
            JOIN product p ON o.product_id = p.id
            JOIN order_status s ON o.status_id = s.id
            ORDER BY o.order_date DESC
            LIMIT ?
            """;

        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orders.add(new OrderRecord(
                        rs.getInt("id"),
                        rs.getTimestamp("order_date").toLocalDateTime(),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("description"),
                        rs.getInt("qty"),
                        rs.getString("status_name"),
                        rs.getDouble("price")
                    ));
                }
            }
        }
        return orders;
    }

    public void deleteOrdersByCustomer(int customerId) throws SQLException {
        String sql = "DELETE FROM orders WHERE customer_id = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ps.executeUpdate();
        }
    }
}