package com.example.ecommerce.model;
import jakarta.validation.constraints.Min;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("products")
public class Product {

    @Id
    private String id;

    private String name;
    private String description;
    private String type;
    private String usp;
    private String imageUrl;
    @Min(value = 0, message = "Price must be non-negative")
    private double price;



    private List<MerchantProduct> merchants;  // This stores merchant-specific data


}
