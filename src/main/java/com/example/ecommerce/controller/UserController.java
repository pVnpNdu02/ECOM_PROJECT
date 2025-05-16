package com.example.ecommerce.controller;



import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.UserRepo;
//import com.example.ecommerce.service.UserService;
import com.example.ecommerce.service.EmailService;
import com.example.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequestMapping("/users")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8081"})
public class UserController {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private EmailService emailService; // Your email sender
    @Autowired
    private UserService userService;

    // Step 1: Request reset link
    @PostMapping("/request-reset")
    public ResponseEntity<String> requestResetToken(@RequestParam String email) {
        Optional<User> userOpt = userRepo.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String token = UUID.randomUUID().toString();
            LocalDateTime expiry = LocalDateTime.now().plusMinutes(15);

            user.setResetToken(token);
            user.setResetTokenExpiry(expiry);
            userRepo.save(user);

            String resetLink = "http://localhost:3000/reset-password?token=" + token;
            String subject = "Reset your password";
            String body = "Click the link to reset your password: " + resetLink + "\n valid only for 15 minutes ";

            emailService.sendSimpleEmail(email, subject, body);
            return ResponseEntity.ok("Reset link sent to your email.");
        } else {
            return ResponseEntity.status(404).body("Email not found.");
        }
    }

    // Step 2: Reset password using token
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        Optional<User> userOpt = userRepo.findByResetToken(token);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
                return ResponseEntity.status(400).body("Token expired.");
            }

            user.setPassword(newPassword); // Optional: hash this using BCrypt
            user.setResetToken(null);
            user.setResetTokenExpiry(null);
            userRepo.save(user);

            return ResponseEntity.ok("Password updated successfully.");
        } else {
            return ResponseEntity.status(400).body("Invalid token.");
        }
    }
    // Method to check if email exists for other merchants
    private boolean emailExistsForOtherMerchants(String email, String merchantId) {
        Optional<User> userWithEmail = userRepo.findByEmail(email);
        if (userWithEmail.isPresent()) {
            User user = userWithEmail.get();
            return !user.getMerchantId().equals(merchantId); // Ensure it's not the current user's email
        }
        return false;
    }

    @PutMapping("/merchant/{merchantId}")
    public ResponseEntity<?> updateMerchantByMerchantId(
            @PathVariable String merchantId,
            @RequestBody User updatedUser) {

        // First, check if the phone number already exists for a different user with the same role
        boolean phoneExists = userService.phoneExistsExceptCurrentUser(updatedUser.getPhone(), updatedUser.getRole(), merchantId);
        if (phoneExists) {
            return ResponseEntity.status(400).body("Phone number is already registered for this role.");
        }

        // Check if the email is already taken by another merchant (except the current user)
        boolean emailExists = emailExistsForOtherMerchants(updatedUser.getEmail(), merchantId);
        if (emailExists) {
            return ResponseEntity.status(400).body("Email is already registered for another merchant.");
        }

        Optional<User> userOpt = userRepo.findByMerchantId(merchantId);
        if (userOpt.isPresent()) {
            User existingUser = userOpt.get();
            existingUser.setName(updatedUser.getName());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setPhone(updatedUser.getPhone());
            existingUser.setAddress(updatedUser.getAddress());
            existingUser.setPassword(updatedUser.getPassword());
            userRepo.save(existingUser);
            return ResponseEntity.ok(existingUser);
        } else {
            return ResponseEntity.status(404).body("Merchant not found");
        }
    }




    @GetMapping("/merchant-id/{name}")
    public ResponseEntity<String> getMerchantId(@PathVariable String name) {
        User user = userRepo.findByName(name);
        if (user != null) {
            return ResponseEntity.ok(user.getMerchantId());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/id-by-email/{email}")
    public ResponseEntity<String> getIdByEmail(@PathVariable String email) {
        Optional<User> userOpt = userRepo.findByEmail(email);
        if (userOpt.isPresent()) {
            return ResponseEntity.ok(userOpt.get().getId().toString()); // Send `_id` as a string
        } else {
            return ResponseEntity.status(404).body("User not found");
        }
    }
    @GetMapping("/merchant/{merchantId}")
    public ResponseEntity<User> getUserByMerchantId(@PathVariable String merchantId) {
        Optional<User> userOpt = userRepo.findByMerchantId(merchantId);

        if (userOpt.isPresent()) {
            return ResponseEntity.ok(userOpt.get());
        } else {
            return ResponseEntity.status(404).body(null);
        }
    }

    @PostMapping("/checkPhone")
    public ResponseEntity<Map<String, Object>> checkPhone(@RequestBody Map<String, String> request) {
        String phone = request.get("phone");
        String role = request.get("role");

        // Validate input (optional, depending on your needs)
        if (phone == null || role == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Phone and role must be provided"));
        }

        // Check if the phone exists for the given role
        boolean exists = userService.phoneExists(phone, role);

        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("exists", exists);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getUserProfile(@PathVariable String id) {
        Optional<User> userOpt = userService.getUserById(id);
        if (userOpt.isPresent()) {
            return ResponseEntity.ok(userOpt.get());
        } else {
            return ResponseEntity.status(404).body("User not found");
        }
    }

        // ❌ PROBLEM: you started another @GetMapping here inside this method!
        @GetMapping("/merchant-name/{merchantId}")
        public ResponseEntity<String> getMerchantName(@PathVariable String merchantId) {
            Optional<User> merchantOptional = userRepo.findByMerchantId(merchantId);

            if (merchantOptional.isPresent()) {
                String merchantName = merchantOptional.get().getName();
                return ResponseEntity.ok(merchantName);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Merchant not found");
            }
        }

}
