package com.example.ecommerce.repository;

import com.example.ecommerce.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

//public interface ProductRepo extends MongoRepository<Product, String> {
//    List<Product> findByMerchant(String merchantId);  // Querying products by merchant ID or email
//}
@Repository
public interface OrderRepo extends MongoRepository<Order, String> {

    // This will correctly search inside the merchants array
    @Query("{'items.merchantId': ?0}")
    List<Order> findByMerchantId(String merchantId);

}