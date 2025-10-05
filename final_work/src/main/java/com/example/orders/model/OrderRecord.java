package com.example.orders.model;

import java.time.LocalDateTime;

public record OrderRecord(
        int id,
        LocalDateTime orderDate,
        String firstName,
        String lastName,
        String productDescription,
        int qty,
        String statusName,
        double price
) {}
