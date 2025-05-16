package com.example.ecommerce.service;

import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class UserService{

    @Autowired
    private UserRepo userRepository;

    // Login method to directly compare plain text passwords
    public Optional<User> login(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent() && user.get().getPassword().equals(password)) {
            return user;  // Password matches, return user
        }
        return Optional.empty();  // Invalid credentials
    }

    // Registration method (store password in plain text)
    public User registerUser(User user) throws Exception {
        // Check if the email already exists
        Optional<User> existingEmail = userRepository.findByEmail(user.getEmail());
        if (existingEmail.isPresent()) {
            throw new Exception("Email already in use.");
        }

        // Check if the phone number is already in use for the same role (MERCHANT or CUSTOMER)
        Optional<User> existingPhone = userRepository.findByPhoneAndRole(user.getPhone(), user.getRole());
        if (existingPhone.isPresent()) {
            throw new Exception("Phone number is already in use for this role.");
        }

        // Auto-generate merchant ID if user is a merchant
        if ("MERCHANT".equalsIgnoreCase(user.getRole())) {
            String uniqueMerchantId = generateUniqueMerchantId();
            user.setMerchantId(uniqueMerchantId);
        }
        System.out.println("Checking phone: " + user.getPhone() + " for role: " + user.getRole());

        // Save and return the user
        return userRepository.save(user);
    }
    public boolean phoneExists(String phone, String role) {
        Optional<User> userOptional;

        // Check if the phone number already exists for the given role (merchant or user)
        userOptional = userRepository.findByPhoneAndRole(phone, role);

        // If the user already exists with this phone number and role, return true
        return userOptional.isPresent();
    }
    public boolean phoneExistsExceptCurrentUser(String phone, String role, String currentUserId) {
        Optional<User> userOptional = userRepository.findByPhoneAndRole(phone, role);
        return userOptional.isPresent() && !userOptional.get().getId().equals(currentUserId);
    }




    // Method to find user by email (for registration and login)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public boolean existsByMerchantId(String merchantId) {
        return userRepository.existsByMerchantId(merchantId);
    }
    public User saveUser(User user) {
        return userRepository.save(user);  // Save user using MongoRepository
    }
    public String getMerchantIdByUsername(String username) {
        // Assuming the User entity has a merchantId field and is stored in a database
        User user = userRepository.findByName(username);

        if (user != null) {
            return user.getMerchantId();  // Return the merchantId associated with the user
        } else {
            return null;
        }
    }
    public String generateResetToken(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return null;

        User user = userOpt.get();
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);
        return token;
    }

    public boolean resetPassword(String token, String newPassword) {
        Optional<User> userOpt = userRepository.findByResetToken(token);
        if (userOpt.isEmpty()) return false;

        User user = userOpt.get();
        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) return false;

        user.setPassword(newPassword);
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
        return true;
    }
    public String generateUniqueMerchantId() {
        Random random = new Random();
        String merchantId;
        do {
            merchantId = String.valueOf(random.nextInt(900) + 100); // 100–999
        } while (userRepository.existsByMerchantId(merchantId));
        return merchantId;
    }


    public Optional<User> findByMerchantId(String merchantId) {
        return userRepository.findByMerchantId(merchantId);
    }
    public String getPasswordByEmail(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        return userOpt.map(User::getPassword).orElse(null);  // returns password or null
    }
    public Optional<User> getUserById(String userId) {
        return userRepository.findById(userId);
    }

}