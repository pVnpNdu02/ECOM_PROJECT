package com.example.ecommerce.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(doNotUseGetters = true)
@Document(collection = "orders")  // MongoDB collection
public class Order {
    private double total;
    @Id
    private String orderId;
    private String userId;
    private String buyerEmail;
    private String deliveryAddress;
    private String city;
    private String state;
    private String zipCode;
    private String phoneNumber;
    private String status;
    private List<OrderItem> items;
}
