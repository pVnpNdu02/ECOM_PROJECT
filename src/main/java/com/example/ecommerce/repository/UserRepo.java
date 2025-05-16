
package com.example.ecommerce.repository;

import com.example.ecommerce.model.User;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserRepo extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByResetToken(String token);
    Optional<User> findByEmailAndPassword(String email, String password);
    boolean existsByMerchantId(String merchantId);
    User findByName(String name);
    Optional<User> findByMerchantId(String merchantId);
    Optional<User> findByPhoneAndRole(String phone, String role);
}
