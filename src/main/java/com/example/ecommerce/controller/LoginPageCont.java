package com.example.ecommerce.controller;
import com.example.ecommerce.repository.UserRepo;
import com.example.ecommerce.model.User;
import com.example.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "http://localhost:3000")
public class LoginPageCont {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        Optional<User> loggedIn = userService.login(user.getEmail(), user.getPassword());
        if (loggedIn.isPresent()) {
            User userService= loggedIn.get();
       userService.setId(userService.getId().toString());
            return ResponseEntity.ok(userService); // Ensure the full `User` object is returned
        }// Sends back full user object
         else {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }



@PostMapping("/register")
public ResponseEntity<?> register(@RequestBody User user) {


    Optional<User> existingEmail = userService.findByEmail(user.getEmail());
    if (existingEmail.isPresent()) {
        return ResponseEntity.status(400).body("Email already in use");
    }


    boolean phoneExists = userService.phoneExists(user.getPhone(), user.getRole());
    if (phoneExists) {
        return ResponseEntity.status(400).body("Phone number is already registered for this role.");
    }


    if ("merchant".equalsIgnoreCase(user.getRole())) {
        if (user.getMerchantId() == null || user.getMerchantId().isEmpty()) {

            user.setMerchantId(userService.generateUniqueMerchantId());
        } else if (userService.existsByMerchantId(user.getMerchantId())) {
            return ResponseEntity.status(400).body("Merchant ID already exists. Please choose a different one.");
        }
    }


    User saved = userService.saveUser(user);
    return ResponseEntity.ok(saved);
}

}

