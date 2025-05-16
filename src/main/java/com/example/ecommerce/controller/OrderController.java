package com.example.ecommerce.controller;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.OrderItem;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.OrderRepo;
import com.example.ecommerce.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderRepo orderRepository;
    @Autowired
    private ProductRepo productRepo;

    @GetMapping("/merchant/{merchantId}")
    public List<Order> getOrdersByMerchant(@PathVariable String merchantId) {
        List<Order> orders = orderRepository.findByMerchantId(merchantId);

        // Loop through each order and fetch product names based on productId
        for (Order order : orders) {
            for (OrderItem item : order.getItems()) {
                Optional<Product> productOpt = productRepo.findById(item.getProductId());
                if (productOpt.isPresent()) {
                    Product product = productOpt.get();
                    item.setProductName(product.getName());  // Set the product name
                } else {
                    // Handle the case if the product is not found
                    System.out.println("Product not found for ID: " + item.getProductId());
                }
            }
        }

        return orders;
    }
}