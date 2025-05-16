package com.example.ecommerce.controller;

import com.example.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class MerchantController {

    @Autowired
    private UserService userService;

    @GetMapping("/merchant-id")
    public ResponseEntity<String> getMerchantId(@RequestParam String username) {

        String merchantId = userService.getMerchantIdByUsername(username);

        if (merchantId != null) {
            return ResponseEntity.ok(merchantId);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Merchant not found");
        }
    }



}