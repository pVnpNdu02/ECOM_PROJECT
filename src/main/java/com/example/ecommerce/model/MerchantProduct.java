package com.example.ecommerce.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Random;
@Data
@NoArgsConstructor
@AllArgsConstructor

public class MerchantProduct {
    private String merchantId;
    private double price;
    private int stock;
    private String merchantName;
 }


