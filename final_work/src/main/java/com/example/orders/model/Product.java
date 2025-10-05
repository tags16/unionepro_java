package com.example.orders.model;

public record Product(
    int id,
    String description,
    double price,
    int quantity,
    Integer categoryId
) {}