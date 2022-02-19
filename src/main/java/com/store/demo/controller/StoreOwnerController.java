package com.store.demo.controller;

import com.store.demo.model.Product;
import com.store.demo.service.StoreOwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class StoreOwnerController {

    @Autowired
    private StoreOwnerService storeOwnerService;

    @PostMapping("/products")
    public ResponseEntity<Boolean> createProduct(@RequestBody Product product) {
        return storeOwnerService.createProduct(product);
    }

    @GetMapping("/products/{productCode}")
    public ResponseEntity<Product> getProduct(@PathVariable String productCode) {
        return storeOwnerService.getProduct(productCode);
    }

    @DeleteMapping("/products/{productCode}")
    public ResponseEntity<Boolean> deleteProduct(@PathVariable String productCode) {
        return storeOwnerService.deleteProduct(productCode);
    }

    @PutMapping("/products")
    public ResponseEntity<Boolean> amendProducts(@RequestBody Product product) {
        return storeOwnerService.amendProduct(product);
    }
}
