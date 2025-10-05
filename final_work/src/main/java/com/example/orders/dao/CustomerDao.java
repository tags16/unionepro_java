package com.example.orders.dao;

import com.example.orders.model.Customer;
import com.example.orders.util.DbUtil;

import java.sql.*;

public class CustomerDao {

    public int insert(Customer cst) throws SQLException {
        String sql = "INSERT INTO customer (first_name, last_name, phone, email) VALUES (?, ?, ?, ?) RETURNING id";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cst.firstName());
            ps.setString(2, cst.lastName());
            ps.setString(3, cst.phone());
            ps.setString(4, cst.email());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new SQLException("Insert failed, no ID obtained.");
            }
        }
    }

    public void deleteById(int id) throws SQLException {
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM customer WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}