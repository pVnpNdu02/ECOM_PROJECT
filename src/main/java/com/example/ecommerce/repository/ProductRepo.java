package com.example.ecommerce.repository;

import com.example.ecommerce.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;
import java.util.Optional;

//public interface ProductRepo extends MongoRepository<Product, String> {
//    List<Product> findByMerchant(String merchantId);  // Querying products by merchant ID or email
//}
public interface ProductRepo extends MongoRepository<Product, String> {

    // This will correctly search inside the merchants array
    @Query("{ 'merchants.merchantId': ?0 }")
    List<Product> findByMerchantId(String merchantId);
    @Query("{ 'name': ?0, 'description': ?1, 'type': ?2, 'usp': ?3, 'imageUrl': ?4, 'merchants.merchantId': ?5 }")
    Optional<Product> findExistingProduct(
            String name, String description, String type,
            String usp, String imageUrl, String merchantId
    );


}
