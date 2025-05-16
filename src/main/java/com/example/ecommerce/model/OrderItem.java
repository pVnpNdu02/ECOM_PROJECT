package com.example.ecommerce.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderItem {
    private String productId;

    private String merchantId;
    private int quantity;
    private double price;
    // Add this field to store the product name
    private String productName;
}
