package com.example.ecommerce.controller;

import com.example.ecommerce.model.MerchantProduct;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.ProductRepo;
import com.example.ecommerce.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {

    @Autowired
    private final ProductRepo productRepo;
@Autowired
private UserRepo userRepo;
    public ProductController(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }
    @PostMapping("/add")
public ResponseEntity<?> addProduct(@RequestBody Product product, @RequestParam String merchantId) {
    Optional<Product> existingProduct = productRepo.findExistingProduct(
            product.getName(),
            product.getDescription(),
            product.getType(),
            product.getUsp(),
            product.getImageUrl(),
            merchantId
    );

    if (existingProduct.isPresent()) {
        String existingProductId = existingProduct.get().getId();
        return ResponseEntity.status(409)
                .body("Product already exists. Update here: /products/update/" + existingProductId + "/" + merchantId);
    }

    // ✅ Extract merchant data from request
    if (product.getMerchants() == null || product.getMerchants().isEmpty()) {
        return ResponseEntity.badRequest().body("Merchant data is missing.");
    }

    MerchantProduct incomingMerchant = product.getMerchants().get(0);

    // ✅ Validate stock > 0
    if (incomingMerchant.getStock() <= 0) {
        return ResponseEntity.badRequest().body("Stock must be greater than 0.");
    }
    Optional<User> merchantOpt = userRepo.findByMerchantId(merchantId);
    String merchantName = merchantOpt.map(User::getName).orElse("Unknown Merchant");
    // ✅ Construct the merchant data
    MerchantProduct merchantProduct = new MerchantProduct();
    merchantProduct.setMerchantId(merchantId);
    merchantProduct.setMerchantName(merchantName);
    merchantProduct.setPrice(incomingMerchant.getPrice());
    merchantProduct.setStock(incomingMerchant.getStock());

    // ✅ Set product's merchants list
    product.setMerchants(Collections.singletonList(merchantProduct));

    Product saved = productRepo.save(product);
    return ResponseEntity.ok(saved);
}
    // 🔍 Get Products by Merchant Email/ID

    @GetMapping("/merchant/{merchantId}")
    public List<Product> getProductsByMerchant(@PathVariable String merchantId) {
        return productRepo.findByMerchantId(merchantId);
    }


@GetMapping("/{id}")
public ResponseEntity<?> getProductById(@PathVariable String id) {
    Optional<Product> productOpt = productRepo.findById(id);
    if (productOpt.isPresent()) {
        return ResponseEntity.ok(productOpt.get());
    } else {
        return ResponseEntity.status(404).body("{\"error\": \"Product not found\"}");
    }
}


@PutMapping("/update/{id}/{merchantId}")
public ResponseEntity<?> updateProduct(
        @PathVariable String id,
        @PathVariable String merchantId,
        @RequestBody Product updatedProduct) {

    Optional<Product> optionalProduct = productRepo.findById(id);

    if (optionalProduct.isPresent()) {
        Product existingProduct = optionalProduct.get();

        // ✅ Update global product info
        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setType(updatedProduct.getType());
        existingProduct.setUsp(updatedProduct.getUsp());
        existingProduct.setImageUrl(updatedProduct.getImageUrl());

        // 🔄 Update merchant-specific price and stock
        if (existingProduct.getMerchants() != null) {
            for (MerchantProduct merchantProduct : existingProduct.getMerchants()) {
                if (merchantProduct.getMerchantId().equals(merchantId)) {
                    merchantProduct.setPrice(updatedProduct.getPrice());

                    // ✅ Safer stock update
                    if (updatedProduct.getMerchants() != null && !updatedProduct.getMerchants().isEmpty()) {
                        merchantProduct.setStock(updatedProduct.getMerchants().get(0).getStock());
                    }

                    break;
                }
            }
        }

        productRepo.save(existingProduct);
        return ResponseEntity.ok(existingProduct);
    } else {
        return ResponseEntity.status(404).body("Product not found");
    }
}

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable String id) {
        Optional<Product> productOpt = productRepo.findById(id);
        if (productOpt.isPresent()) {
            productRepo.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

}
